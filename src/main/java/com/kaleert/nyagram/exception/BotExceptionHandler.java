package com.kaleert.nyagram.exception;

import java.lang.annotation.*;

/**
 * Аннотация для методов внутри классов {@link BotControllerAdvice}.
 * <p>
 * Указывает, какие типы исключений должен обрабатывать данный метод.
 * </p>
 *
 * @since 1.0.0
 */
@Target(ElementType.METHOD)
@Retention(RetentionPolicy.RUNTIME)
public @interface BotExceptionHandler {
    
    /**
     * Список классов исключений, которые обрабатывает метод.
     * @return массив классов Throwable.
     */
    Class<? extends Throwable>[] value();
}