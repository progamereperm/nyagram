package com.kaleert.nyagram.context;

import com.kaleert.nyagram.command.CommandContext;
import java.util.Map;
import org.springframework.stereotype.Component;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;

/**
 * Стандартная реализация форматтера контекста логирования.
 * <p>
 * Добавляет в MDC (Mapped Diagnostic Context) поля {@code user} и {@code chat}.
 * </p>
 *
 * @since 1.0.0
 */
@Component
@ConditionalOnMissingBean(LogContextFormatter.class)
public class DefaultLogFormatter implements LogContextFormatter {
    @Override
    public Map<String, String> format(CommandContext ctx) {
        return Map.of(
            "user",String.valueOf(ctx.getUserId()),
            "chat", String.valueOf(ctx.getChatId())
        );
    }
}