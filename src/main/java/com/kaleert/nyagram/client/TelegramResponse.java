package com.kaleert.nyagram.client;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

/**
 * Обобщенный объект ответа от Telegram Bot API.
 * <p>
 * Представляет собой стандартную структуру JSON-ответа:
 * {@code { "ok": true, "result": ... }} или {@code { "ok": false, "description": ... }}
 * </p>
 *
 * @param <T> Тип ожидаемого результата (User, Message, Boolean и т.д.).
 * @since 1.0.0
 */
@Data
@JsonIgnoreProperties(ignoreUnknown = true)
public class TelegramResponse<T> {
    
    /**
     * Статус выполнения запроса.
     */
    private boolean ok;
    
    /**
     * Результат выполнения (если ok=true).
     */
    private T result;
    
    /**
     * Описание ошибки (если ok=false).
     */
    private String description;
    
    /**
     * Код ошибки (например, 400, 404, 429).
     */
    @JsonProperty("error_code")
    private Integer errorCode;
    
    /**
     * Количество секунд для ожидания перед повтором (при ошибке 429).
     */
    @JsonProperty("retry_after")
    private Integer retryAfter;
}
