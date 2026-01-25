package com.kaleert.nyagram.api.meta;

import com.kaleert.nyagram.api.exception.TelegramApiValidationException;

/**
 * Интерфейс для объектов, требующих валидации перед отправкой.
 * <p>
 * Позволяет проверить корректность данных (например, наличие обязательных полей)
 * на стороне клиента, не делая лишний запрос к серверу Telegram.
 * </p>
 *
 * @since 1.0.0
 */
public interface Validable {
    
    /**
     * Выполняет проверку корректности полей объекта.
     *
     * @throws TelegramApiValidationException если валидация не прошла.
     */
    void validate() throws TelegramApiValidationException;
}