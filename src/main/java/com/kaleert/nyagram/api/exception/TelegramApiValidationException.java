package com.kaleert.nyagram.api.exception;

import lombok.Getter;

/**
 * Исключение, выбрасываемое при локальной валидации метода перед отправкой.
 * <p>
 * Например, если не указан обязательный параметр (chat_id) или текст сообщения слишком длинный.
 * </p>
 *
 * @since 1.0.0
 */
@Getter
public class TelegramApiValidationException extends TelegramApiException {
    
    private final String method;
    private final String field;
    
    /**
     * Создает ошибку валидации для метода API.
     *
     * @param message Описание ошибки валидации.
     * @param method Имя метода API.
     */
    public TelegramApiValidationException(String message, String method) {
        super(message);
        this.method = method;
        this.field = null;
    }
    
    /**
     * Создает ошибку валидации для конкретного поля метода API.
     *
     * @param message Описание ошибки.
     * @param method Имя метода API.
     * @param field Имя поля, не прошедшего валидацию.
     */
    public TelegramApiValidationException(String message, String method, String field) {
        super(message);
        this.method = method;
        this.field = field;
    }

    @Override
    public String toString() {
        if (field != null) {
            return super.toString() + " (Method: " + method + ", Field: " + field + ")";
        }
        if (method != null) {
            return super.toString() + " (Method: " + method + ")";
        }
        return super.toString();
    }
}