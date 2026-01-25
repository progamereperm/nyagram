package com.kaleert.nyagram.feature.broadcast;

import com.kaleert.nyagram.api.exception.TelegramApiException;
import com.kaleert.nyagram.api.methods.send.SendMessage;
import com.kaleert.nyagram.client.NyagramClient;
import com.kaleert.nyagram.core.concurrency.NyagramExecutor;
import com.kaleert.nyagram.feature.broadcast.event.BroadcastEvents.BroadcastCompleteEvent;
import com.kaleert.nyagram.feature.broadcast.event.BroadcastEvents.UserBlockedEvent;
import com.kaleert.nyagram.feature.broadcast.spi.BroadcastTargetProvider;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.stereotype.Service;

import java.util.concurrent.Phaser;
import java.util.concurrent.atomic.AtomicLong;
import java.util.stream.Stream;

/**
 * Сервис для управления массовыми рассылками сообщений.
 * <p>
 * Обеспечивает:
 * <ul>
 *     <li>Многопоточную отправку (использует {@link NyagramExecutor}).</li>
 *     <li>Соблюдение лимитов API (Throttling) для предотвращения ошибок 429.</li>
 *     <li>Обработку ошибок (например, блокировка бота пользователем).</li>
 *     <li>Публикацию событий о завершении рассылки.</li>
 * </ul>
 * </p>
 *
 * @since 1.0.0
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class BroadcastManager {

    private final NyagramClient client;
    private final NyagramExecutor taskExecutor;
    private final BroadcastTargetProvider targetProvider;
    private final ApplicationEventPublisher eventPublisher;
    
    /**
     * Запускает процесс рассылки в фоновом режиме.
     * <p>
     * Метод возвращает управление немедленно. Статус рассылки можно отслеживать через события.
     * </p>
     *
     * @param text Текст сообщения (поддерживает HTML).
     * @param senderAdminId ID администратора, запустившего рассылку (для логов).
     */
    public void broadcast(String text, Integer senderAdminId) {
        log.info("Starting broadcast initiated by admin {}", senderAdminId);
        new Thread(() -> runBroadcastLoop(text), "broadcast-manager").start();
    }
    
    /**
     * Внутренний цикл рассылки.
     * Перебирает получателей из {@link BroadcastTargetProvider} и ставит задачи в пул потоков.
     */
    private void runBroadcastLoop(String text) {
        long startTime = System.currentTimeMillis();
        
        AtomicLong total = new AtomicLong();
        AtomicLong success = new AtomicLong();
        AtomicLong failed = new AtomicLong();
        
        Phaser phaser = new Phaser(1); 
    
        try (Stream<Long> targets = targetProvider.getTargetChatIds()) {
            
            targets.forEach(chatId -> {
                total.incrementAndGet();
                phaser.register();
    
                try {
                    taskExecutor.execute(chatId, () -> {
                        try {
                            client.execute(SendMessage.builder()
                                    .chatId(chatId.toString())
                                    .text(text)
                                    .parseMode("HTML")
                                    .build());
                            success.incrementAndGet();
                        } catch (TelegramApiException e) {
                            handleError(e, chatId);
                            failed.incrementAndGet();
                        } catch (Exception e) {
                            log.error("Generic error sending to {}", chatId, e);
                            failed.incrementAndGet();
                        } finally {
                            phaser.arriveAndDeregister();
                        }
                    });
                } catch (Exception e) {
                    phaser.arriveAndDeregister();
                    failed.incrementAndGet();
                    log.error("Failed to submit broadcast task for {}", chatId, e);
                }
    
                throttle(total.get());
            });
            
        } catch (Exception e) {
            log.error("Error during broadcast iteration", e);
        }
    
        log.info("All broadcast tasks submitted. Waiting for completion...");
    
        phaser.arriveAndAwaitAdvance();
    
        long duration = System.currentTimeMillis() - startTime;
        log.info("Broadcast finished. Duration: {}ms. Total: {}, Success: {}, Failed: {}", 
                duration, total.get(), success.get(), failed.get());
        
        eventPublisher.publishEvent(new BroadcastCompleteEvent(
                total.get(), success.get(), failed.get(), duration
        ));
    }

    private void handleError(TelegramApiException e, Long chatId) {
        if (e.getErrorCode() != null && e.getErrorCode() == 403) {
            eventPublisher.publishEvent(new UserBlockedEvent(chatId));
        } else {
            log.warn("Failed to send broadcast to {}: {}", chatId, e.getMessage());
        }
    }

    private void throttle(long count) {
        if (count % 100 == 0) {
            try { Thread.sleep(50); } catch (InterruptedException ignored) {}
        }
    }
}