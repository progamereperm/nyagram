package com.kaleert.nyagram.security.spi;

import com.kaleert.nyagram.api.objects.User;
import java.util.Set;

/**
 * Интерфейс (SPI) для определения конкретных прав (Permissions) пользователя.
 * <p>
 * Реализуйте этот интерфейс для поддержки аннотации {@link com.kaleert.nyagram.security.RequiresPermission}.
 * Позволяет гибко настраивать доступ к отдельным командам (например, "reports.view", "users.ban").
 * </p>
 *
 * @since 1.0.0
 */
public interface UserPermissionProvider {
    /**
     * Возвращает набор прав пользователя.
     *
     * @param user Пользователь Telegram.
     * @return Набор строк-ключей прав (например, Set.of("admin", "editor")).
     */
    Set<String> getUserPermissions(User user);
    
    /**
     * Проверяет, является ли пользователь супер-админом.
     * <p>
     * Супер-админы игнорируют любые проверки прав {@code @RequiresPermission}.
     * </p>
     *
     * @param user Пользователь Telegram.
     * @return true, если пользователь имеет абсолютный доступ.
     */
    default boolean isSuperAdmin(User user) {
        return false;
    }
}