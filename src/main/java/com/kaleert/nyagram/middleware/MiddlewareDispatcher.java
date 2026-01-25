package com.kaleert.nyagram.middleware;

import com.kaleert.nyagram.command.CommandContext;
import com.kaleert.nyagram.meta.CommandMeta;
import jakarta.annotation.PostConstruct;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.util.Comparator;
import java.util.List;
import java.util.concurrent.CompletableFuture;

/**
 * Диспетчер Middleware.
 * <p>
 * Собирает все бины {@link Middleware} из контекста Spring, сортирует их согласно {@link org.springframework.core.Ordered}
 * и запускает выполнение цепочки для каждой входящей команды.
 * </p>
 *
 * @since 1.0.0
 */
@Slf4j
@Component
@RequiredArgsConstructor
public class MiddlewareDispatcher {

    private final List<Middleware> middlewares;
    private List<Middleware> sortedMiddlewares;
    
    /**
     * Инициализирует диспетчер.
     * <p>
     * Сортирует внедренный список middleware в соответствии с приоритетом {@code @Order}.
     * </p>
     */
    @PostConstruct
    public void init() {
        sortedMiddlewares = middlewares.stream()
                .sorted(Comparator.comparing(Middleware::getOrder))
                .toList();
        
        log.info("Initialized MiddlewareDispatcher with {} middlewares", sortedMiddlewares.size());
    }
    
    /**
     * Запускает выполнение цепочки Middleware для команды.
     *
     * @param context Контекст команды.
     * @param meta Метаданные команды.
     * @return Future с результатом выполнения цепочки (Continue, Stop или Error).
     */
    public CompletableFuture<MiddlewareResult> dispatch(CommandContext context, CommandMeta meta) {
        if (sortedMiddlewares.isEmpty()) {
            return new MiddlewareChain(sortedMiddlewares, context, meta).proceed();
        }
        
        MiddlewareChain chain = new MiddlewareChain(sortedMiddlewares, context, meta);
        return chain.proceed();
    }
}