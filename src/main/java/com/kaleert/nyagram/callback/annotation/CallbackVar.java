package com.kaleert.nyagram.callback.annotation;

import java.lang.annotation.*;

/**
 * Извлекает переменную из пути Callback-запроса в аргумент метода.
 * <p>
 * Работает в связке с аннотацией {@link Callback}.
 * </p>
 *
 * @since 1.0.0
 */
@Target(ElementType.PARAMETER)
@Retention(RetentionPolicy.RUNTIME)
@Documented
public @interface CallbackVar {
    
    /**
     * Имя переменной в шаблоне {@link Callback}.
     * <p>
     * Если не указано, используется имя параметра метода.
     * </p>
     *
     * @return имя переменной.
     */
    String value() default "";
}