package com.kaleert.nyagram.event;

import java.lang.annotation.*;

/**
 * Помечает метод как обработчик определенного типа событий Telegram.
 * <p>
 * Используется для обработки обновлений, которые не являются текстовыми командами
 * (например, добавление бота в чат, реакции, платежи, опросы).
 * </p>
 *
 * @example
 * <pre>
 * {@code @NyagramEventHandler(EventType.MY_CHAT_MEMBER)}
 * public void onBotJoin(Update update) {
 *     // Логика при добавлении бота в группу
 * }
 * </pre>
 *
 * @since 1.0.0
 */
@Target(ElementType.METHOD)
@Retention(RetentionPolicy.RUNTIME)
@Documented
public @interface NyagramEventHandler {
    
    /**
     * Тип события, на которое должен реагировать метод.
     *
     * @return тип события из {@link EventType}.
     */
    EventType value();
}