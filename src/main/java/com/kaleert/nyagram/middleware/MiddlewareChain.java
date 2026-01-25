package com.kaleert.nyagram.middleware;

import com.kaleert.nyagram.command.CommandContext;
import com.kaleert.nyagram.meta.CommandMeta;

import java.util.Iterator;
import java.util.List;
import java.util.concurrent.CompletableFuture;

/**
 * Управляет цепочкой выполнения Middleware.
 * <p>
 * Позволяет передавать управление следующему обработчику через метод {@link #proceed()}.
 * Если цепочка закончилась, инициирует выполнение основной бизнес-логики команды.
 * </p>
 *
 * @since 1.0.0
 */
public class MiddlewareChain {
    
    private final Iterator<Middleware> iterator;
    private final CommandContext context;
    private final CommandMeta meta;
    
    /**
     * Создает новую цепочку выполнения Middleware.
     *
     * @param middlewares Список всех middleware, которые нужно выполнить.
     * @param context Контекст текущей команды.
     * @param meta Метаданные команды.
     */
    public MiddlewareChain(List<Middleware> middlewares, CommandContext context, CommandMeta meta) {
        this.iterator = middlewares.iterator();
        this.context = context;
        this.meta = meta;
    }
    
    /**
     * Передает управление следующему middleware в цепочке.
     *
     * @return Future с результатом обработки.
     */
    public CompletableFuture<MiddlewareResult> proceed() {
        if (iterator.hasNext()) {
            Middleware middleware = iterator.next();
            return middleware.handle(context, meta, this);
        }
        
        return executeCommand();
    }
    
    private CompletableFuture<MiddlewareResult> executeCommand() {
        return CompletableFuture.supplyAsync(() -> {
            try {
                return MiddlewareResult.continueResult();
            } catch (Exception e) {
                return MiddlewareResult.errorResult(e);
            }
        });
    }
}