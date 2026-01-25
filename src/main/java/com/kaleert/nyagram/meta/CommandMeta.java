package com.kaleert.nyagram.meta;

import com.kaleert.nyagram.security.AccessDeniedAction;
import com.kaleert.nyagram.security.RateLimit;
import com.kaleert.nyagram.core.ArgumentResolver;
import com.kaleert.nyagram.core.AsyncMode;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;
import lombok.RequiredArgsConstructor;
import lombok.AllArgsConstructor;
import lombok.ToString;

import java.lang.invoke.MethodHandle;
import java.lang.reflect.Method;
import java.lang.reflect.Parameter;
import java.util.Collections;
import java.util.List;
import java.util.Set;

/**
 * Метаданные зарегистрированной команды.
 * <p>
 * Хранит всю информацию, необходимую для выполнения команды: ссылку на метод,
 * требования безопасности (права, уровни), ограничения скорости (rate limits)
 * и описание аргументов. Собирается {@link com.kaleert.nyagram.core.registry.CommandRegistry}
 * при старте приложения.
 * </p>
 *
 * @since 1.0.0
 */
@Getter
@Builder
@ToString(exclude = {"handlerInstance", "method", "methodParameters"})
@RequiredArgsConstructor
@AllArgsConstructor
public class CommandMeta {

    private final String fullCommandPath;
    private final Object handlerInstance;
    private final Method method;
    private final MethodHandle methodHandle;
    private final String description;
    private final AsyncMode.Mode asyncMode;
    
    @Setter
    private ArgumentResolver<?>[] preCalculatedResolvers;
    private final List<Parameter> methodParameters;
    private final Set<String> requiredPermissions;
    private final LevelRequirement levelRequirement;
    private final RateLimitMeta rateLimitMeta;
    private final int minArgs;
    private final int maxArgs;
    private final String usageSyntax;
    
    /**
     * Описание требований к уровню доступа пользователя для этой команды.
     *
     * @param min Минимальный уровень.
     * @param max Максимальный уровень.
     * @param deniedAction Действие при отказе в доступе.
     */
    public record LevelRequirement(int min, int max, AccessDeniedAction deniedAction) {}
    
    /**
     * Описание ограничений скорости (Rate Limit) для этой команды.
     *
     * @param calls Количество разрешенных вызовов.
     * @param windowSeconds Окно времени в секундах.
     * @param type Тип ограничения (на пользователя или на чат).
     */
    public record RateLimitMeta(int calls, int windowSeconds, RateLimit.LimitType type) {}
    
    /**
     * Возвращает набор прав, необходимых для выполнения команды.
     * <p>
     * Гарантирует возврат пустого набора (вместо null), если права не требуются.
     * </p>
     *
     * @return Set строк с правами.
     */
    public Set<String> getRequiredPermissions() {
        return requiredPermissions == null ? Collections.emptySet() : requiredPermissions;
    }
}