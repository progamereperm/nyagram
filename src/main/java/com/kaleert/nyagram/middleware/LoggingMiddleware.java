package com.kaleert.nyagram.middleware;

import com.kaleert.nyagram.command.CommandContext;
import com.kaleert.nyagram.meta.CommandMeta;
import lombok.extern.slf4j.Slf4j;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;

import java.util.concurrent.CompletableFuture;

/**
 * Middleware для логирования выполнения команд.
 * <p>
 * Записывает в лог информацию о начале и завершении обработки команды,
 * включая пользователя, время выполнения и статус (успех/ошибка).
 * </p>
 *
 * @since 1.0.0
 */
@Slf4j
@Component
@Order(100)
public class LoggingMiddleware implements Middleware {

    @Override
    public CompletableFuture<MiddlewareResult> handle(CommandContext context, 
                                                      CommandMeta meta, 
                                                      MiddlewareChain next) {
        
        long startTime = System.currentTimeMillis();
        
        return next.proceed().whenComplete((result, throwable) -> {
            long duration = System.currentTimeMillis() - startTime;
            String cmd = meta.getFullCommandPath();
            Long userId = context.getUserId();
            
            if (throwable != null) {
                log.error("[CMD] {} | User: {} | Status: FAILED | Time: {}ms | Error: {}", 
                        cmd, userId, duration, throwable.getMessage());
            } else if (!result.shouldContinue()) {
                log.info("[CMD] {} | User: {} | Status: STOPPED | Time: {}ms | Reason: {}", 
                        cmd, userId, duration, result.getMessage());
            } else {
                log.info("[CMD] {} | User: {} | Status: OK | Time: {}ms", 
                        cmd, userId, duration);
            }
        });
    }
}