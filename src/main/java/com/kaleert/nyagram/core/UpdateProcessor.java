package com.kaleert.nyagram.core;

import com.kaleert.nyagram.api.objects.Update;
import com.kaleert.nyagram.callback.CallbackDispatcher; // <-- ИМПОРТ
import com.kaleert.nyagram.client.NyagramClient;
import com.kaleert.nyagram.command.CommandContext;
import com.kaleert.nyagram.core.concurrency.BotConcurrencyStrategy;
import com.kaleert.nyagram.core.spi.RawUpdateHandler;
import com.kaleert.nyagram.core.spi.UpdateInterceptor;
import com.kaleert.nyagram.dispatcher.CommandDispatcher;
import com.kaleert.nyagram.dispatcher.EventDispatcher;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;
import java.util.concurrent.CompletableFuture;

/**
 * Центральный компонент обработки входящих обновлений.
 * <p>
 * Отвечает за:
 * 1. Запуск пре-интерсепторов.
 * 2. Маршрутизацию обновления в нужный диспетчер (Команды, События, Коллбэки).
 * 3. Управление потоками выполнения через {@link BotConcurrencyStrategy}.
 * 4. Запуск пост-интерсепторов.
 * </p>
 *
 * @since 1.0.0
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class UpdateProcessor {

    private final CommandDispatcher commandDispatcher;
    private final EventDispatcher eventDispatcher;
    private final CallbackDispatcher callbackDispatcher;
    private final BotConcurrencyStrategy concurrencyStrategy;
    private final NyagramClient nyagramClient;
    
    private final Optional<RawUpdateHandler> rawUpdateHandler;
    private final List<UpdateInterceptor> interceptors;
    
    /**
     * Асинхронно обрабатывает входящее обновление.
     * <p>
     * Метод выполняет следующие шаги:
     * 1. Вызывает pre-handle интерсепторы.
     * 2. Передает обновление в {@link BotConcurrencyStrategy} для планирования выполнения.
     * 3. Внутри стратегии вызывается логика диспетчеризации (команды, события, коллбэки).
     * 4. Вызывает post-handle интерсепторы после завершения.
     * </p>
     *
     * @param update Входящее обновление.
     */
    public void processAsync(Update update) {
        try {
            for (UpdateInterceptor interceptor : interceptors) {
                if (!interceptor.preHandle(update)) {
                    return;
                }
            }
        } catch (Exception e) {
            log.error("Error in PreHandle Interceptor", e);
            return;
        }

        concurrencyStrategy.execute(update, () -> processInternal(update));
    }

    private void processInternal(Update update) {
        CompletableFuture<Boolean> processingFuture;

        try {
            if (rawUpdateHandler.isPresent() && rawUpdateHandler.get().handle(update)) {
                processingFuture = CompletableFuture.completedFuture(true);
            } 
            
            else if (isMessage(update)) {
                processingFuture = commandDispatcher.dispatch(update)
                        .thenApply(result -> true)
                        .exceptionally(ex -> {
                            log.error("Error in command dispatch", ex);
                            return false;
                        });
            } 
            
            else if (update.hasCallbackQuery()) {
                CommandContext context = new CommandContext(update, nyagramClient);
                callbackDispatcher.dispatch(context); // <-- Вызываем диспетчер
                processingFuture = CompletableFuture.completedFuture(true);
            }
            
            else {
                eventDispatcher.dispatch(update);
                processingFuture = CompletableFuture.completedFuture(true);
            }

        } catch (Exception e) {
            processingFuture = CompletableFuture.failedFuture(e);
        }

        processingFuture.whenComplete((handled, throwable) -> {
            boolean isSuccess = throwable == null && Boolean.TRUE.equals(handled);
            for (UpdateInterceptor interceptor : interceptors) {
                try {
                    interceptor.postHandle(update, isSuccess, throwable);
                } catch (Exception ex) {
                    log.error("Error in PostHandle interceptor", ex);
                }
            }
        });
    }

    private boolean isMessage(Update update) {
        return update.hasMessage();
    }
}