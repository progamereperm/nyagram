package com.kaleert.nyagram.pipeline;

import com.kaleert.nyagram.command.CommandContext;
import com.kaleert.nyagram.core.CommandResult;
import com.kaleert.nyagram.meta.CommandMeta;
import com.kaleert.nyagram.security.RateLimit;
import lombok.extern.slf4j.Slf4j;
import org.springframework.core.annotation.Order;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.util.Map;
import java.util.Optional;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicLong;
import java.util.concurrent.locks.ReentrantLock;

/**
 * Пре-процессор для ограничения частоты вызовов команд (Rate Limiting).
 * <p>
 * Обрабатывает аннотацию {@link com.kaleert.nyagram.security.RateLimit}.
 * Использует алгоритм Token Bucket для контроля количества запросов от пользователя или чата
 * за определенный промежуток времени.
 * </p>
 *
 * @since 1.0.0
 */
@Slf4j
@Component
@Order(0)
public class RateLimitPreProcessor implements CommandPreProcessor {

    private final Map<String, Map<Long, RateLimitBucket>> limits = new ConcurrentHashMap<>();
    private final ReentrantLock cleanupLock = new ReentrantLock();

    @Override
    public Optional<CommandResult> process(CommandContext context, CommandMeta commandMeta) {
        CommandMeta.RateLimitMeta limitMeta = commandMeta.getRateLimitMeta();
        if (limitMeta == null) {
            return Optional.empty();
        }

        String commandKey = commandMeta.getFullCommandPath();
        long entityId = (limitMeta.type() == RateLimit.LimitType.PER_CHAT) 
                ? context.getChatId() 
                : context.getUserId();

        Map<Long, RateLimitBucket> commandBuckets = limits.computeIfAbsent(
                commandKey, k -> new ConcurrentHashMap<>()
        );
        
        RateLimitBucket bucket = commandBuckets.computeIfAbsent(entityId, 
                id -> new RateLimitBucket(limitMeta.calls(), limitMeta.windowSeconds()));

        if (!bucket.tryConsume()) {
            long retryAfter = bucket.getRetryAfterSeconds();
            return Optional.of(CommandResult.error(
                String.format("⏳ <b>Слишком часто!</b>\nПодождите %d сек.", retryAfter)
            ));
        }

        return Optional.empty();
    }
    
    /**
     * Периодическая задача очистки неактивных бакетов (buckets) для освобождения памяти.
     * <p>
     * Запускается каждые 5 минут. Удаляет записи о пользователях, которые давно не
     * вызывали команды с ограничением частоты.
     * </p>
     */
    @Scheduled(fixedRate = 300000)
    public void cleanup() {
        if (!cleanupLock.tryLock()) {
            return;
        }
        
        try {
            long now = System.currentTimeMillis();
            int removed = 0;
            
            for (Map<Long, RateLimitBucket> commandMap : limits.values()) {
                commandMap.entrySet().removeIf(entry -> {
                    RateLimitBucket bucket = entry.getValue();
                    boolean expired = bucket.isExpired(now);
                    if (expired) {
                        log.debug("Removed expired rate limit bucket for entity {}", entry.getKey());
                    }
                    return expired;
                });
                removed++;
            }
            
            limits.entrySet().removeIf(entry -> entry.getValue().isEmpty());
            
            if (removed > 0) {
                log.debug("Cleaned up {} rate limit maps", removed);
            }
        } finally {
            cleanupLock.unlock();
        }
    }
    
    /**
     * Реализация алгоритма Token Bucket для одного пользователя/чата.
     * <p>
     * Хранит количество доступных токенов и время последнего пополнения.
     * Потокобезопасен.
     * </p>
     */
    private static class RateLimitBucket {
        private final int maxTokens;
        private final long windowMs;
        
        private final AtomicLong tokens;
        private final AtomicLong lastRefillTimestamp;
        
        /**
         * Создает новый бакет (корзину) для алгоритма Token Bucket.
         *
         * @param maxTokens Максимальная емкость корзины (максимальный всплеск запросов).
         * @param windowSeconds Время в секундах, за которое корзина полностью наполняется (или период обновления).
         */
        public RateLimitBucket(int maxTokens, int windowSeconds) {
            this.maxTokens = maxTokens;
            this.windowMs = TimeUnit.SECONDS.toMillis(windowSeconds);
            this.tokens = new AtomicLong(maxTokens);
            this.lastRefillTimestamp = new AtomicLong(System.currentTimeMillis());
        }
        
        /**
         * Пытается потребить токен из корзины.
         * <p>
         * Сначала вызывает {@code refill()} для пополнения токенов по времени.
         * Затем атомарно уменьшает счетчик.
         * </p>
         *
         * @return true, если токен успешно потреблен (запрос разрешен).
         *         false, если токенов нет (лимит превышен).
         */
        public boolean tryConsume() {
            refill();
            
            while (true) {
                long current = tokens.get();
                if (current <= 0) {
                    return false;
                }
                if (tokens.compareAndSet(current, current - 1)) {
                    return true;
                }
            }
        }
        
        /**
         * Вычисляет время ожидания (в секундах) до появления следующего доступного токена.
         *
         * @return Количество секунд или 0, если токены есть.
         */
        public long getRetryAfterSeconds() {
            refill();
            long current = tokens.get();
            if (current > 0) {
                return 0;
            }
            
            long now = System.currentTimeMillis();
            long lastRefill = lastRefillTimestamp.get();
            long timeSinceLastRefill = now - lastRefill;
            
            if (timeSinceLastRefill >= windowMs) {
                return 0;
            }
            
            long timeToNextToken = windowMs - timeSinceLastRefill;
            return TimeUnit.MILLISECONDS.toSeconds(timeToNextToken);
        }
        
        /**
         * Проверяет, является ли бакет устаревшим (не использовался длительное время).
         *
         * @param currentTime Текущее время в мс.
         * @return true, если бакет можно удалить.
         */
        public boolean isExpired(long currentTime) {
            long lastRefill = lastRefillTimestamp.get();
            return (currentTime - lastRefill) > (windowMs * 2); // Устарело если не использовалось 2 окна
        }

        private void refill() {
            long now = System.currentTimeMillis();
            long lastRefill = lastRefillTimestamp.get();
            long timePassed = now - lastRefill;
            
            if (timePassed <= 0) {
                return;
            }
            
            if (timePassed >= windowMs) {
                if (lastRefillTimestamp.compareAndSet(lastRefill, now)) {
                    tokens.set(maxTokens);
                }
                return;
            }
            
            double refillRate = (double) maxTokens / windowMs;
            long tokensToAdd = (long) (timePassed * refillRate);
            
            if (tokensToAdd > 0) {
                long oldLastRefill = lastRefillTimestamp.getAndSet(now);
                if (oldLastRefill == lastRefill) { // Убедимся, что никто не обновил параллельно
                    tokens.updateAndGet(current -> {
                        long newTokens = current + tokensToAdd;
                        return Math.min(newTokens, maxTokens);
                    });
                }
            }
        }
    }
}