package com.kaleert.nyagram.middleware;

import com.kaleert.nyagram.command.CommandContext;
import com.kaleert.nyagram.context.BotContextHolder;
import com.kaleert.nyagram.meta.CommandMeta;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;
import java.util.concurrent.CompletableFuture;

/**
 * Системный Middleware, инициализирующий {@link com.kaleert.nyagram.context.BotContextHolder}.
 * <p>
 * Гарантирует, что статический доступ к контексту через {@code BotContextHolder.getContext()}
 * будет доступен на протяжении всей цепочки обработки, и очищает его после завершения.
 * Имеет наивысший приоритет.
 * </p>
 *
 * @since 1.0.0
 */
@Component
@Order(Integer.MIN_VALUE)
public class ContextMiddleware implements Middleware {

    @Override
    public CompletableFuture<MiddlewareResult> handle(CommandContext context, CommandMeta meta, MiddlewareChain next) {
        BotContextHolder.setContext(context);
        
        return next.proceed().whenComplete((r, e) -> {
            BotContextHolder.clear();
        });
    }
}