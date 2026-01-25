package com.kaleert.nyagram.pipeline;

import com.kaleert.nyagram.command.CommandContext;
import com.kaleert.nyagram.core.CommandResult;
import com.kaleert.nyagram.exception.NoPermissionException;
import com.kaleert.nyagram.meta.CommandMeta;
import com.kaleert.nyagram.security.spi.UserPermissionProvider;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;

import java.util.Optional;
import java.util.Set;

/**
 * Пре-процессор для проверки прав (Permissions) пользователя.
 * <p>
 * Обрабатывает аннотацию {@link com.kaleert.nyagram.security.RequiresPermission}.
 * Проверяет наличие у пользователя необходимых прав через {@link com.kaleert.nyagram.security.spi.UserPermissionProvider}.
 * </p>
 *
 * @since 1.0.0
 */
@Slf4j
@Component
@Order(1)
@RequiredArgsConstructor
public class PermissionCheckPreProcessor implements CommandPreProcessor {

    private final Optional<UserPermissionProvider> permissionProvider;

    @Override
    public Optional<CommandResult> process(CommandContext context, CommandMeta commandMeta) {
        Set<String> requiredPermissions = commandMeta.getRequiredPermissions();

        if (requiredPermissions == null || requiredPermissions.isEmpty()) {
            return Optional.empty();
        }

        if (permissionProvider.isEmpty()) {
            log.warn("Command {} requires permissions {}, but no UserPermissionProvider bean found!", 
                    commandMeta.getFullCommandPath(), requiredPermissions);
            return Optional.of(CommandResult.error("❌ Ошибка конфигурации безопасности бота."));
        }

        UserPermissionProvider provider = permissionProvider.get();
        if (provider.isSuperAdmin(context.getTelegramUser())) {
            return Optional.empty();
        }

        Set<String> userPermissions = provider.getUserPermissions(context.getTelegramUser());

        for (String required : requiredPermissions) {
            if (!userPermissions.contains(required) && !userPermissions.contains("*")) {
                throw new NoPermissionException(required);
            }
        }

        return Optional.empty();
    }
}