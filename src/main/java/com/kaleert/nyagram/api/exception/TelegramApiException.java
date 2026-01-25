package com.kaleert.nyagram.api.exception;

import lombok.Getter;

/**
 * Базовое исключение для всех ошибок, связанных с работой Telegram Bot API.
 *
 * @since 1.0.0
 */
@Getter
public class TelegramApiException extends RuntimeException {
    private final Integer errorCode;
    private final String description;
    private final String apiMethod;
    
    /**
     * Создает исключение с сообщением об ошибке.
     * @param message Текст ошибки.
     */
    public TelegramApiException(String message) {
        super(message);
        this.errorCode = null;
        this.description = null;
        this.apiMethod = null;
    }
    
    /**
     * Создает исключение с сообщением и исходной причиной.
     * @param message Текст ошибки.
     * @param cause Исходное исключение.
     */
    public TelegramApiException(String message, Throwable cause) {
        super(message, cause);
        this.errorCode = null;
        this.description = null;
        this.apiMethod = null;
    }
    
    /**
     * Создает исключение на основе другого исключения.
     * @param cause Исходное исключение.
     */
    public TelegramApiException(Throwable cause) {
        super(cause);
        this.errorCode = null;
        this.description = null;
        this.apiMethod = null;
    }
    
    /**
     * Создает исключение с полными деталями ошибки API.
     *
     * @param message Сообщение об ошибке.
     * @param apiMethod Имя метода API, который вызвал ошибку.
     * @param errorCode Код ошибки HTTP/API.
     * @param description Описание ошибки от Telegram.
     */
    public TelegramApiException(String message, String apiMethod, Integer errorCode, String description) {
        super(String.format("[%s] Telegram API Error %d: %s. Details: %s", apiMethod, errorCode, message, description));
        this.apiMethod = apiMethod;
        this.errorCode = errorCode;
        this.description = description;
    }
}