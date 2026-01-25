package com.kaleert.nyagram.exception;

/**
 * Исключение, выбрасываемое при ошибке преобразования аргумента команды.
 * <p>
 * Например, если метод ожидает {@code Integer}, а пользователь ввел "abc".
 * Обычно обрабатывается {@link com.kaleert.nyagram.core.spi.MissingArgumentHandler}
 * для отправки понятного сообщения пользователю.
 * </p>
 *
 * @since 1.0.0
 */
public class ArgumentParseException extends RuntimeException {
    
    /**
     * Создает исключение с сообщением об ошибке парсинга.
     *
     * @param message Описание ошибки (например, "Неверный формат числа").
     */
    public ArgumentParseException(String message) {
        super(message);
    }
}