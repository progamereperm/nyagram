package com.kaleert.nyagram.exception;

/**
 * Общее исключение, возникающее при фатальных ошибках выполнения команды.
 * <p>
 * Оборачивает системные ошибки или непредвиденные состояния внутри диспетчера.
 * </p>
 *
 * @since 1.0.0
 */
public class CommandExecutionException extends RuntimeException {
    
    /**
     * Создает исключение, оборачивающее системную ошибку выполнения команды.
     *
     * @param message Сообщение об ошибке.
     * @param cause Исходное исключение.
     */
    public CommandExecutionException(String message, Throwable cause) {
        super(message, cause);
    }
}