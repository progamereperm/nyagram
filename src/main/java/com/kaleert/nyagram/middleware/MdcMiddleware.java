package com.kaleert.nyagram.middleware;

import com.kaleert.nyagram.command.CommandContext;
import com.kaleert.nyagram.meta.CommandMeta;
import org.slf4j.MDC;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;

import java.util.UUID;
import java.util.concurrent.CompletableFuture;

/**
 * Системный Middleware для настройки контекста логирования (MDC).
 * <p>
 * Добавляет в логи уникальный {@code traceId}, а также ID пользователя, чата и название команды.
 * Это позволяет фильтровать логи по конкретному запросу или пользователю в системах агрегации логов (ELK, Graylog).
 * Имеет минимальный приоритет (запускается одним из первых).
 * </p>
 *
 * @since 1.0.0
 */
@Component
@Order(Integer.MIN_VALUE)
public class MdcMiddleware implements Middleware {
    
    /**
     * Ключ, используемый для хранения идентификатора трассировки (Trace ID) в MDC.
     * <p>
     * Значение по этому ключу можно использовать в шаблонах логгера (например, {@code %X{traceId}}).
     * </p>
     */
    public static final String TRACE_ID_KEY = "traceId";

    @Override
    public CompletableFuture<MiddlewareResult> handle(CommandContext context, CommandMeta meta, MiddlewareChain next) {
        String traceId = UUID.randomUUID().toString().substring(0, 8);
        
        MDC.put(TRACE_ID_KEY, traceId);
        MDC.put("userId", String.valueOf(context.getUserId()));
        MDC.put("chatId", String.valueOf(context.getChatId()));
        MDC.put("cmd", meta.getFullCommandPath());
        
        return next.proceed().whenComplete((res, ex) -> {
            MDC.clear();
        });
    }
}