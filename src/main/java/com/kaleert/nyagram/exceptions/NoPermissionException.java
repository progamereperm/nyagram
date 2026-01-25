package com.kaleert.nyagram.exception;

/**
 * Исключение, выбрасываемое системой безопасности бота, когда у пользователя
 * недостаточно прав для выполнения действия.
 * <p>
 * Возникает при проверке аннотации {@link com.kaleert.nyagram.security.RequiresPermission}.
 * </p>
 *
 * @since 1.0.0
 */
public class NoPermissionException extends RuntimeException {
    
    /**
     * Создает исключение, указывающее на отсутствие конкретного права.
     *
     * @param permission Строковый идентификатор отсутствующего права (например, "admin.ban").
     */
    public NoPermissionException(String permission) {
        super("Required permission: " + permission);
    }
}