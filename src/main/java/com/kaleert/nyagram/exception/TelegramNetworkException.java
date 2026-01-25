package com.kaleert.nyagram.exception;

import lombok.Getter;

/**
 * Исключение, указывающее на проблемы транспортного уровня (сеть).
 * <p>
 * Выбрасывается, когда не удается установить соединение с серверами Telegram,
 * происходит тайм-аут чтения или ошибка DNS. Это не является логической ошибкой бота.
 * Обычно такие запросы можно безопасно повторить (retry).
 * </p>
 *
 * @since 1.0.0
 */
public class TelegramNetworkException extends RuntimeException {
    
    /**
     * Название метода API, который не удалось выполнить.
     */
    @Getter
    private final String apiMethod;
    
    /**
     * Создает исключение сети.
     *
     * @param message Сообщение об ошибке.
     * @param apiMethod Метод API.
     * @param cause Исходное исключение (например, IOException).
     */
    public TelegramNetworkException(String message, String apiMethod, Throwable cause) {
        super(String.format("[%s] Network Error: %s", apiMethod, message), cause);
        this.apiMethod = apiMethod;
    }
}