package com.kaleert.nyagram.middleware;

import lombok.Getter;

/**
 * Результат выполнения шага Middleware.
 * <p>
 * Определяет, что делать дальше: продолжить выполнение, остановить обработку с сообщением
 * пользователю, перенаправить на другую команду или выбросить ошибку.
 * </p>
 *
 * @since 1.0.0
 */
@Getter
public class MiddlewareResult {
    
    /**
     * Тип результата выполнения Middleware.
     */
    public enum Type {
        /** Продолжить выполнение цепочки. */
        CONTINUE,
        /** Остановить обработку (команда не будет вызвана). */
        STOP,
        /** Произошла ошибка. */
        ERROR,
        /** Перенаправить выполнение на другую команду (внутренний редирект). */
        REDIRECT
    }
    
    private final Type type;
    private final String message;
    private final Exception exception;
    private final String redirectCommand;
    
    private MiddlewareResult(Type type, String message, Exception exception, String redirectCommand) {
        this.type = type;
        this.message = message;
        this.exception = exception;
        this.redirectCommand = redirectCommand;
    }
    
    /**
     * Создает результат, указывающий на успешное завершение шага middleware
     * и необходимость продолжить выполнение цепочки.
     *
     * @return объект результата с типом {@link Type#CONTINUE}.
     */
    public static MiddlewareResult continueResult() {
        return new MiddlewareResult(Type.CONTINUE, null, null, null);
    }
    
    /**
     * Создает результат для немедленной остановки обработки.
     * <p>
     * Команда не будет вызвана. Если передано сообщение {@code message}, оно будет отправлено пользователю
     * (или залогировано, в зависимости от реализации диспетчера).
     * </p>
     *
     * @param message Сообщение о причине остановки (опционально).
     * @return объект результата с типом {@link Type#STOP}.
     */
    public static MiddlewareResult stopResult(String message) {
        return new MiddlewareResult(Type.STOP, message, null, null);
    }
    
    /**
     * Создает результат, указывающий на ошибку при выполнении middleware.
     * <p>
     * Цепочка будет прервана, и исключение будет передано в глобальный обработчик ошибок.
     * </p>
     *
     * @param e Исключение, возникшее в процессе.
     * @return объект результата с типом {@link Type#ERROR}.
     */
    public static MiddlewareResult errorResult(Exception e) {
        return new MiddlewareResult(Type.ERROR, e.getMessage(), e, null);
    }
    
    /**
     * Создает результат для перенаправления выполнения на другую команду (Internal Redirect).
     * <p>
     * Текущая команда не будет выполнена, вместо нее диспетчер попытается найти и выполнить
     * команду, указанную в {@code command}. Полезно для алиасов или сложной логики маршрутизации.
     * </p>
     *
     * @param command Текст новой команды (например, "/help").
     * @return объект результата с типом {@link Type#REDIRECT}.
     */
    public static MiddlewareResult redirectResult(String command) {
        return new MiddlewareResult(Type.REDIRECT, null, null, command);
    }
    
    /**
     * Проверяет, нужно ли продолжать выполнение цепочки middleware.
     *
     * @return true, если тип результата {@link Type#CONTINUE}.
     */
    public boolean shouldContinue() {
        return type == Type.CONTINUE;
    }
}