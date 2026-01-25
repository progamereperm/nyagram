package com.kaleert.nyagram.core.concurrency;

import com.kaleert.nyagram.context.BotContextHolder;
import com.kaleert.nyagram.core.spi.NyagramBotConfig;
import jakarta.annotation.PreDestroy;
import lombok.extern.slf4j.Slf4j;
import org.slf4j.MDC;
import org.springframework.stereotype.Component;

import java.lang.invoke.MethodHandle;
import java.lang.invoke.MethodHandles;
import java.lang.invoke.MethodType;
import java.util.*;
import java.util.concurrent.*;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * Кастомный исполнитель задач (Executor), поддерживающий шардирование по ключу.
 * <p>
 * Гарантирует, что задачи с одинаковым {@code shardKey} (например, userId) будут выполняться
 * последовательно одним и тем же воркером, но задачи с разными ключами могут выполняться параллельно.
 * Поддерживает Виртуальные Потоки (Virtual Threads), если запущено на Java 21+.
 * </p>
 *
 * @since 1.0.0
 */
@Slf4j
@Component
public class NyagramExecutor implements Executor {

    private final List<ThreadPoolExecutor> executorShards;
    private final Map<Long, Integer> userAffinity;
    private final boolean useVirtualThreads;

    private static final int DEFAULT_THREAD_COUNT = Runtime.getRuntime().availableProcessors() * 2;
    private static final int MAX_AFFINITY_ENTRIES = 10000;
    
    /**
     * Инициализирует исполнитель с параметрами из конфигурации.
     * <p>
     * Создает шардированный пул потоков. Если доступно (Java 21+), пытается использовать
     * Виртуальные Потоки (Project Loom), иначе откатывается на платформенные потоки.
     * </p>
     *
     * @param config Конфигурация бота.
     */
    public NyagramExecutor(NyagramBotConfig config) {
        int threadCount = config.getWorkerThreadCount() > 0 ? config.getWorkerThreadCount() : DEFAULT_THREAD_COUNT;
        
        this.userAffinity = Collections.synchronizedMap(new LinkedHashMap<>(16, 0.75f, true) {
            @Override
            protected boolean removeEldestEntry(Map.Entry<Long, Integer> eldest) {
                return size() > MAX_AFFINITY_ENTRIES;
            }
        });

        this.executorShards = new ArrayList<>(threadCount);
        this.useVirtualThreads = tryEnableVirtualThreads();
        
        initShards(threadCount, config.getWorkerThreadPrefix(), config.getWorkerKeepAliveTimeMs());
    }

    private boolean tryEnableVirtualThreads() {
        try {
            Class.forName("java.lang.Thread$Builder$OfVirtual");
            return true;
        } catch (ClassNotFoundException e) {
            return false;
        }
    }

    private void initShards(int count, String prefix, long keepAlive) {
        log.info("Initializing NyagramExecutor. Shards: {}, Mode: {}", 
                count, useVirtualThreads ? "VIRTUAL THREADS (Loom)" : "PLATFORM THREADS");
        int queueCapacityPerShard = 2000; 
    
        for (int i = 0; i < count; i++) {
            ThreadFactory factory = createThreadFactory(prefix, i);
            
            ThreadPoolExecutor executor = new ThreadPoolExecutor(
                    1, 1,
                    keepAlive, TimeUnit.MILLISECONDS,
                    new LinkedBlockingQueue<>(queueCapacityPerShard),
                    factory,
                    new ThreadPoolExecutor.AbortPolicy()
            );
            
            executor.allowCoreThreadTimeOut(true);
            executorShards.add(executor);
        }
    }

    private ThreadFactory createThreadFactory(String prefix, int shardIndex) {
        String threadName = prefix + shardIndex + "-";

        if (useVirtualThreads) {
            try {
                var lookup = MethodHandles.publicLookup();
                Class<?> builderClass = Class.forName("java.lang.Thread$Builder$OfVirtual");
                
                MethodHandle ofVirtual = lookup.findStatic(Thread.class, "ofVirtual", MethodType.methodType(builderClass));
                Object builder = ofVirtual.invoke();

                MethodHandle nameMethod = lookup.findVirtual(builderClass, "name", MethodType.methodType(builderClass, String.class, long.class));
                builder = nameMethod.invoke(builder, threadName, 0L);

                MethodHandle factoryMethod = lookup.findVirtual(builderClass, "factory", MethodType.methodType(ThreadFactory.class));
                return (ThreadFactory) factoryMethod.invoke(builder);

            } catch (Throwable e) {
                log.warn("Failed to init Virtual Threads factory, falling back to Platform threads.", e);
            }
        }

        return new PlatformThreadFactory(threadName);
    }

    @Override
    public void execute(Runnable command) {
        execute(null, command);
    }
    
    /**
     * Выполняет задачу в конкретном шарде на основе ключа.
     * <p>
     * Гарантирует, что задачи с одинаковым {@code shardKey} попадут в одну очередь исполнения,
     * обеспечивая последовательность.
     * </p>
     *
     * @param shardKey Ключ шардирования (обычно User ID). Если null, выбирается наименее загруженный шард.
     * @param task Задача для выполнения.
     */
    public void execute(Long shardKey, Runnable task) {
        if (isShutdown()) {
            log.warn("Executor is shutting down, task rejected.");
            return;
        }

        int shardIndex = resolveShardIndex(shardKey);
        Runnable wrappedTask = wrapWithContext(task, shardKey, shardIndex);

        try {
            executorShards.get(shardIndex).execute(wrappedTask);
        } catch (RejectedExecutionException e) {
            log.error("Task rejected in shard {}", shardIndex, e);
        }
    }

    private int resolveShardIndex(Long shardKey) {
        if (shardKey == null) {
            return getLeastLoadedShardIndex();
        }

        synchronized (userAffinity) {
            return userAffinity.computeIfAbsent(shardKey, k -> getLeastLoadedShardIndex());
        }
    }

    private int getLeastLoadedShardIndex() {
        int minIndex = 0;
        int minSize = Integer.MAX_VALUE;

        for (int i = 0; i < executorShards.size(); i++) {
            int queueSize = executorShards.get(i).getQueue().size();
            if (queueSize == 0) return i;
            
            if (queueSize < minSize) {
                minSize = queueSize;
                minIndex = i;
            }
        }
        return minIndex;
    }

    private Runnable wrapWithContext(Runnable task, Long shardKey, int shardIndex) {
        var context = BotContextHolder.getContext();
        var mdcMap = MDC.getCopyOfContextMap();

        return () -> {
            if (context != null) BotContextHolder.setContext(context);
            if (mdcMap != null) MDC.setContextMap(mdcMap);

            try {
                task.run();
            } catch (Exception e) {
                log.error("Execution error in shard-{} for key {}", shardIndex, shardKey, e);
            } finally {
                BotContextHolder.clear();
                MDC.clear();
                
            }
        };
    }
    
    /**
     * Формирует строку со статистикой состояния экзекьютора.
     * <p>
     * Включает информацию о типе потоков, количестве шардов, общем размере очередей
     * и загруженности отдельных шардов.
     * </p>
     *
     * @return Диагностическая строка.
     */
    public String getExecutorStats() {
        StringBuilder sb = new StringBuilder();
        int totalQueue = 0;
        int activeShards = 0;

        for (int i = 0; i < executorShards.size(); i++) {
            int size = executorShards.get(i).getQueue().size();
            totalQueue += size;
            if (size > 0) {
                activeShards++;
                if (activeShards <= 10) {
                    sb.append(String.format(" S%d:%d", i, size));
                }
            }
        }

        return String.format("Pool: %s | Threads: %d | Queued: %d | Affinity: %d | Busy Shards: %s",
                useVirtualThreads ? "Virtual" : "Platform",
                executorShards.size(),
                totalQueue,
                userAffinity.size(),
                sb.length() > 0 ? sb.toString() : "None");
    }

    private boolean isShutdown() {
        return executorShards.isEmpty() || executorShards.get(0).isShutdown();
    }
    
    /**
     * Останавливает работу экзекьютора и всех его шардов.
     * <p>
     * Пытается корректно завершить выполнение текущих задач. Если задачи не завершаются
     * за отведенное время, вызывает принудительную остановку.
     * </p>
     */
    @PreDestroy
    public void shutdown() {
        log.info("Shutting down NyagramExecutor...");
        executorShards.forEach(ExecutorService::shutdown);
        try {
            boolean clean = true;
            for (ExecutorService es : executorShards) {
                if (!es.awaitTermination(2, TimeUnit.SECONDS)) {
                    es.shutdownNow();
                    clean = false;
                }
            }
            if (!clean) log.warn("Some shards were forced to shutdown.");
        } catch (InterruptedException e) {
            executorShards.forEach(ExecutorService::shutdownNow);
            Thread.currentThread().interrupt();
        }
    }
    
    /**
     * Фабрика для создания стандартных платформенных потоков (OS Threads).
     * Используется, если Виртуальные Потоки недоступны.
     */
    private static class PlatformThreadFactory implements ThreadFactory {
        private final String namePrefix;
        private final AtomicInteger threadNumber = new AtomicInteger(1);

        PlatformThreadFactory(String namePrefix) {
            this.namePrefix = namePrefix;
        }

        @Override
        public Thread newThread(Runnable r) {
            Thread t = new Thread(r, namePrefix + threadNumber.getAndIncrement());
            t.setDaemon(true);
            return t;
        }
    }
}