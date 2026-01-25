package com.kaleert.nyagram.fsm.annotation;

import java.lang.annotation.*;

/**
 * Внедряет значение из данных сессии (FSM) в аргумент метода.
 * <p>
 * Позволяет избежать ручного вызова {@code session.getData(...)}.
 * </p>
 *
 * @example
 * <pre>
 * public void finishOrder(
 *     CommandContext ctx,
 *     {@code @SessionData("pizzaName")} String pizza,
 *     {@code @SessionData("count")} int count
 * ) { ... }
 * </pre>
 *
 * @since 1.0.0
 */
@Target(ElementType.PARAMETER)
@Retention(RetentionPolicy.RUNTIME)
@Documented
public @interface SessionData {
    
    /**
     * Ключ, по которому данные хранятся в сессии.
     *
     * @return ключ данных.
     */
    String value();
}