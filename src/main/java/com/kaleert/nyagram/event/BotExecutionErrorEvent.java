package com.kaleert.nyagram.event;

import com.kaleert.nyagram.command.CommandContext;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

/**
 * Событие Spring ApplicationEvent, публикуемое при возникновении необработанного исключения
 * во время выполнения команды бота.
 * <p>
 * Может быть использовано для централизованного логирования ошибок, отправки алертов
 * разработчикам или сбора статистики сбоев.
 * </p>
 *
 * @since 1.0.0
 */
@Getter
@RequiredArgsConstructor
public class BotExecutionErrorEvent {
    
    /** Контекст команды, при обработке которой произошла ошибка. */
    private final CommandContext context;
    
    /** Исключение, которое было выброшено. */
    private final Throwable exception;
    
    /** Путь команды (например, "/start"), вызвавшей ошибку. */
    private final String commandPath;
    
    /** Уникальный идентификатор трассировки (Trace ID) для корреляции логов. */
    private final String traceId;
}