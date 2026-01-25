package com.kaleert.nyagram.validation;

import java.lang.annotation.*;

/**
 * Аннотация для валидации аргументов команд.
 * <p>
 * Применяется к параметрам методов, помеченных {@code @CommandHandler}.
 * Автоматически проверяет длину строки или соответствие регулярному выражению
 * до вызова метода.
 * </p>
 *
 * @since 1.0.0
 */
@Target(ElementType.PARAMETER)
@Retention(RetentionPolicy.RUNTIME)
@Documented
public @interface Validation {
    
    /**
     * Минимальная длина строки.
     * @return длина.
     */
    int minLength() default 0;
    
    /**
     * Максимальная длина строки.
     * @return длина.
     */
    int maxLength() default Integer.MAX_VALUE;
    
    /**
     * Регулярное выражение для проверки формата.
     * @return regex строка.
     */
    String pattern() default "";
}