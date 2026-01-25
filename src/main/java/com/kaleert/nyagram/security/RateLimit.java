package com.kaleert.nyagram.security;

import java.lang.annotation.*;

/**
 * Ограничивает частоту вызова команды для пользователя или чата.
 * <p>
 * Используется для защиты от спама и злоупотребления ресурсоемкими командами.
 * Реализовано через алгоритм Token Bucket.
 * </p>
 *
 * @example
 * <pre>
 * // Не более 5 вызовов за 60 секунд
 * {@code @RateLimit(calls = 5, timeWindowSec = 60)}
 * public void heavyTask(CommandContext ctx) { ... }
 * </pre>
 *
 * @since 1.0.0
 */
@Target(ElementType.METHOD)
@Retention(RetentionPolicy.RUNTIME)
@Documented
public @interface RateLimit {
    
    /**
     * Максимальное количество разрешенных вызовов за указанное окно времени.
     *
     * @return количество вызовов.
     */
    int calls();
    
    /**
     * Окно времени в секундах, за которое считаются вызовы.
     *
     * @return секунды.
     */
    int timeWindowSec();
    
    /**
     * Тип ограничения (область действия).
     *
     * @return {@link LimitType}. По умолчанию PER_USER.
     */
    LimitType type() default LimitType.PER_USER;
    
    /**
     * Область действия ограничения.
     */
    enum LimitType {
        /** Лимит применяется к конкретному пользователю (User ID), независимо от чата. */
        PER_USER,
        
        /** Лимит применяется ко всему чату (Chat ID), независимо от того, кто пишет. */
        PER_CHAT
    }
}