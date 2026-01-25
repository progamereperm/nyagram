package com.kaleert.nyagram.security;

import java.lang.annotation.*;

/**
 * Ограничивает доступ к команде на основе числового уровня доступа пользователя.
 * <p>
 * Требует реализации {@link com.kaleert.nyagram.security.spi.UserLevelProvider} в контексте Spring.
 * </p>
 *
 * @example
 * <pre>
 * // Доступно только пользователям с уровнем 10 и выше (например, модераторы)
 * {@code @LevelRequired(min = 10)}
 * public void ban(CommandContext ctx) { ... }
 * </pre>
 *
 * @see AccessDeniedAction
 * @since 1.0.0
 */
@Target(ElementType.METHOD)
@Retention(RetentionPolicy.RUNTIME)
@Documented
public @interface LevelRequired {
    
    /**
     * Минимальный требуемый уровень для выполнения команды.
     *
     * @return минимальный уровень (включительно).
     */
    int min() default 0;
    
    /**
     * Максимальный допустимый уровень для выполнения команды.
     * <p>
     * Полезно, если вы хотите сделать команду доступной только новичкам.
     * </p>
     *
     * @return максимальный уровень (включительно).
     */
    int max() default Integer.MAX_VALUE;

    /**
     * Действие, которое необходимо выполнить при отказе в доступе.
     *
     * @return {@link AccessDeniedAction}. По умолчанию NOTIFY.
     */
    AccessDeniedAction deniedAction() default AccessDeniedAction.NOTIFY;
}