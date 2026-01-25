package com.kaleert.nyagram.exception;

import lombok.Getter;

/**
 * Базовое исключение для всех ошибок, возникающих при взаимодействии с Telegram Bot API.
 * <p>
 * Может быть выброшено как при сетевых проблемах (обернуто в наследников),
 * так и при логических ошибках API (например, неверный токен или чат не найден).
 * </p>
 *
 * @since 1.0.0
 */
@Getter
public class TelegramApiException extends RuntimeException {
    
    /**
     * Код ошибки HTTP или API (например, 400, 404, 429).
     * Может быть null, если ошибка произошла до отправки запроса.
     */
    private final Integer errorCode;
    
    /**
     * Описание ошибки от Telegram.
     */
    private final String description;
    
    /**
     * Название метода API, при вызове которого произошла ошибка.
     */
    private final String apiMethod;
    
    /**
     * Конструктор для общих ошибок с описанием.
     * @param message Сообщение об ошибке.
     * @param apiMethod Имя метода API.
     * @param errorCode Код ошибки.
     * @param description Описание от сервера.
     */
    public TelegramApiException(String message, String apiMethod, Integer errorCode, String description) {
        super(String.format("[%s] Telegram API Error %d: %s", apiMethod, errorCode, description));
        this.apiMethod = apiMethod;
        this.errorCode = errorCode;
        this.description = description;
    }
}