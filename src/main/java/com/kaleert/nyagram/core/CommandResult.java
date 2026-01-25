package com.kaleert.nyagram.core;

import lombok.RequiredArgsConstructor;

/**
 * Результат выполнения команды или middleware.
 * <p>
 * Используется для передачи статуса обработки (успех/ошибка) и опционального сообщения,
 * которое может быть отправлено пользователю или залогировано.
 * </p>
 *
 * @since 1.0.0
 */
@RequiredArgsConstructor
public class CommandResult {
    private final boolean success;
    private final String message;
    
    /**
     * Создает успешный результат с сообщением.
     * @param message Сообщение (будет отправлено пользователю).
     */
    public static CommandResult success(String message) {
        return new CommandResult(true, message);
    }
    
    /**
     * Создает результат с ошибкой.
     * @param message Сообщение об ошибке.
     */
    public static CommandResult error(String message) {
        return new CommandResult(false, message);
    }
    
    /**
     * Создает успешный результат без отправки сообщения.
     */
    public static CommandResult noResponse() {
        return new CommandResult(true, null);
    }
    
    /**
     * Проверяет, завершилась ли операция успешно.
     * @return true, если успех.
     */
    public boolean isSuccess() {
        return success;
    }
    
    /**
     * Возвращает текстовое сообщение результата.
     * <p>
     * Это может быть сообщение об успешном выполнении или текст ошибки.
     * </p>
     *
     * @return текст сообщения или null.
     */
    public String getMessage() {
        return message;
    }
}