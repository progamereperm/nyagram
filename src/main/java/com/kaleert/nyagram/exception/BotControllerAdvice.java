package com.kaleert.nyagram.exception;

import org.springframework.stereotype.Component;

import java.lang.annotation.*;

/**
 * Аннотация, помечающая класс как глобальный обработчик исключений для бота.
 * <p>
 * Аналог {@code @ControllerAdvice} в Spring Web. Классы с этой аннотацией
 * сканируются на наличие методов {@link BotExceptionHandler}.
 * </p>
 *
 * @since 1.0.0
 */
@Target(ElementType.TYPE)
@Retention(RetentionPolicy.RUNTIME)
@Documented
@Component
public @interface BotControllerAdvice {
}