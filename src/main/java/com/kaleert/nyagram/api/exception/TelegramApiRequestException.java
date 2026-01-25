package com.kaleert.nyagram.api.exception;

import com.kaleert.nyagram.api.objects.ResponseParameters;
import lombok.Getter;

/**
 * Исключение, выбрасываемое, когда Telegram API возвращает ошибку (поле {@code ok: false}).
 * <p>
 * Содержит код ошибки, описание и, возможно, параметры (например, {@code retry_after}).
 * </p>
 *
 * @since 1.0.0
 */
@Getter
public class TelegramApiRequestException extends TelegramApiException {

    private String apiResponse;
    private Integer errorCode;
    private String description;
    private ResponseParameters parameters;
    
    /**
     * Создает исключение с сообщением.
     * @param message Текст ошибки.
     */
    public TelegramApiRequestException(String message) {
        super(message);
    }
    
    /**
     * Создает исключение с сообщением и причиной.
     * @param message Текст ошибки.
     * @param cause Исходное исключение.
     */
    public TelegramApiRequestException(String message, Throwable cause) {
        super(message, cause);
    }
    
    /**
     * Создает исключение с кодом и описанием ошибки.
     *
     * @param message Текст ошибки.
     * @param errorCode Код ошибки (например, 400).
     * @param description Описание ошибки.
     */
    public TelegramApiRequestException(String message, Integer errorCode, String description) {
        super(message + " (Error Code: " + errorCode + ": " + description + ")");
        this.errorCode = errorCode;
        this.description = description;
    }
    
    /**
     * Полный конструктор с сырым ответом и параметрами.
     *
     * @param message Текст ошибки.
     * @param apiResponse Сырой JSON ответ от сервера.
     * @param errorCode Код ошибки.
     * @param description Описание ошибки.
     * @param parameters Параметры ответа (например, время ожидания для Retry-After).
     */
    public TelegramApiRequestException(String message, String apiResponse, Integer errorCode, String description, ResponseParameters parameters) {
        super(message + " (Error Code: " + errorCode + ": " + description + ")");
        this.apiResponse = apiResponse;
        this.errorCode = errorCode;
        this.description = description;
        this.parameters = parameters;
    }
}