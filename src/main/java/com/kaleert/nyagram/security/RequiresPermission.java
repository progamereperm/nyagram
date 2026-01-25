package com.kaleert.nyagram.security;

import java.lang.annotation.*;

/**
 * Ограничивает доступ к команде на основе строкового разрешения (Permission).
 * <p>
 * Требует реализации {@link com.kaleert.nyagram.security.spi.UserPermissionProvider}.
 * Поддерживает систему прав в стиле "domain.resource.action" (например, "admin.users.ban").
 * </p>
 *
 * @example
 * <pre>
 * {@code @RequiresPermission("report.export")}
 * public void exportReport(CommandContext ctx) { ... }
 * </pre>
 *
 * @since 1.0.0
 */
@Target(ElementType.METHOD)
@Retention(RetentionPolicy.RUNTIME)
@Documented
public @interface RequiresPermission {
    
    /**
     * Ключ разрешения, необходимый для выполнения.
     *
     * @return строка разрешения.
     */
    String value();
}