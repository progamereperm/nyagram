package com.kaleert.nyagram.api.meta;

import com.fasterxml.jackson.annotation.JsonIgnore;

/**
 * Функциональный интерфейс, определяющий контракт метода Telegram API.
 *
 * @param <T> Тип возвращаемого значения.
 * @since 1.0.0
 */
public interface TelegramMethod<T extends BotApiObject> {
    
    /**
     * Возвращает имя метода API.
     * @return строка (например, "getMe").
     */
    @JsonIgnore
    String getMethod();
    
    /**
     * Возвращает класс типа ответа.
     * @return класс T.
     */
    @JsonIgnore
    Class<T> getResponseType();
}