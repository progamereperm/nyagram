package com.kaleert.nyagram.context;

import com.kaleert.nyagram.command.CommandContext;
import java.util.Map;

/**
 * Интерфейс для настройки контекстных переменных логирования (MDC).
 * <p>
 * Реализуйте этот интерфейс и зарегистрируйте как бин, чтобы добавить
 * свои поля (например, {@code tenantId}, {@code requestId}) в логи.
 * </p>
 *
 * @since 1.0.0
 */
public interface LogContextFormatter {
    
    /**
     * Извлекает данные из контекста команды для добавления в логи.
     *
     * @param context Текущий контекст обработки.
     * @return Карта "ключ-значение", которая будет добавлена в MDC.
     */
    Map<String, String> format(CommandContext context);
}