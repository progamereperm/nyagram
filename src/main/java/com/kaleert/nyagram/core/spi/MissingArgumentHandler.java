package com.kaleert.nyagram.core.spi;

import com.kaleert.nyagram.command.CommandContext;
import com.kaleert.nyagram.exception.ArgumentParseException;
import com.kaleert.nyagram.meta.CommandMeta;

/**
 * Обработчик ситуаций, когда при вызове команды не хватает обязательных аргументов.
 * <p>
 * Позволяет кастомизировать сообщение об ошибке, которое видит пользователь,
 * например, отправить подсказку с синтаксисом команды.
 * </p>
 *
 * @since 1.0.0
 */
public interface MissingArgumentHandler {
    
    /**
     * Обрабатывает исключение отсутствия аргумента.
     *
     * @param context Текущий контекст команды.
     * @param meta Метаданные вызываемой команды (для получения синтаксиса).
     * @param exception Исключение, содержащее информацию о том, какого аргумента не хватает.
     */
    void handle(CommandContext context, CommandMeta meta, ArgumentParseException exception);
}
