package com.kaleert.nyagram.pipeline;

import com.kaleert.nyagram.core.CommandResult;
import com.kaleert.nyagram.command.CommandContext;

/**
 * Интерфейс пост-процессора команд.
 * <p>
 * Выполняется ПОСЛЕ того, как команда (и все middleware) отработали.
 * Используется для сбора метрик, финального логирования, очистки ресурсов или
 * выполнения действий, которые не влияют на ответ пользователю.
 * </p>
 *
 * @since 1.0.0
 */
public interface CommandPostProcessor {
    
    /**
     * Выполняет пост-обработку.
     *
     * @param context Контекст выполненной команды.
     * @param result Результат выполнения (успех/ошибка).
     * @param executionTimeMs Время выполнения в миллисекундах.
     */
    void process(CommandContext context, CommandResult result, long executionTimeMs);
}
