package com.kaleert.nyagram.middleware;

import com.kaleert.nyagram.command.CommandContext;
import com.kaleert.nyagram.meta.CommandMeta;
import org.springframework.core.Ordered;

import java.util.Optional;
import java.util.concurrent.CompletableFuture;

/**
 * Интерфейс промежуточного обработчика (Middleware).
 * <p>
 * Реализует паттерн "Chain of Responsibility" (Цепочка обязанностей).
 * Middleware может выполнить код до или после основной команды, изменить результат,
 * или прервать цепочку выполнения.
 * </p>
 *
 * @see MiddlewareChain
 * @since 1.0.0
 */
public interface Middleware extends Ordered {
    
    /**
     * Обрабатывает запрос.
     *
     * @param context Контекст команды.
     * @param meta Метаданные вызываемой команды.
     * @param next Следующее звено в цепочке.
     * @return Future с результатом выполнения.
     */
    CompletableFuture<MiddlewareResult> handle(
            CommandContext context, 
            CommandMeta meta, 
            MiddlewareChain next
    );

    @Override
    default int getOrder() {
        return 0;
    }
}