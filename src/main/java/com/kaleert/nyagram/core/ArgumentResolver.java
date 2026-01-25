package com.kaleert.nyagram.core;

import com.kaleert.nyagram.exception.ArgumentParseException;
import com.kaleert.nyagram.command.CommandContext;
import java.lang.reflect.Parameter;

/**
 * Интерфейс для преобразования строковых аргументов команды в Java-объекты.
 * <p>
 * Используется диспетчером команд для инъекции параметров в методы, помеченные {@code @CommandHandler}.
 * Например, преобразование строки "123" в {@code Integer}.
 * </p>
 *
 * @param <T> Тип целевого объекта.
 * @since 1.0.0
 */
public interface ArgumentResolver<T> {
    
    /**
     * Проверяет, может ли этот резолвер обработать данный параметр метода.
     *
     * @param parameter Параметр метода.
     * @return true, если поддерживается.
     */
    default boolean supports(Parameter parameter) { return false; }
    
    /**
     * Преобразует строковое значение в объект.
     *
     * @param context Контекст команды.
     * @param parameter Параметр метода.
     * @param rawValue "Сырое" строковое значение (токен) из сообщения.
     * @return Преобразованный объект.
     * @throws ArgumentParseException Если преобразование невозможно.
     */
    T resolve(CommandContext context, Parameter parameter, String rawValue) throws ArgumentParseException;
    
    /**
     * Указывает, требуется ли резолверу входной токен из сообщения.
     * <p>
     * Если возвращает {@code false}, резолвер работает только с контекстом (например, инъекция User или Service),
     * не "съедая" слова из сообщения пользователя.
     * </p>
     *
     * @return true, если аргумент потребляет часть текста команды.
     */
    default boolean isTokenRequired() { return true; }
}