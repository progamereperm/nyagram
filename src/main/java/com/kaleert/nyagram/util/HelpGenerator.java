package com.kaleert.nyagram.util;

import com.kaleert.nyagram.api.objects.User;
import com.kaleert.nyagram.core.registry.CommandRegistry;
import com.kaleert.nyagram.meta.CommandMeta;
import com.kaleert.nyagram.security.spi.UserLevelProvider;
import com.kaleert.nyagram.security.spi.UserPermissionProvider;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.lang.reflect.Method;
import java.util.*;
import java.util.stream.Collectors;

/**
 * Сервис для автоматической генерации справки (/help).
 * <p>
 * Анализирует зарегистрированные команды в {@link com.kaleert.nyagram.core.registry.CommandRegistry}.
 * Фильтрует их в зависимости от прав и уровня текущего пользователя, чтобы не показывать
 * недоступные админские команды обычным пользователям.
 * </p>
 *
 * @since 1.1.0
 */
@Service
@RequiredArgsConstructor
public class HelpGenerator {

    private final CommandRegistry registry;
    private final Optional<UserLevelProvider> levelProvider;
    private final Optional<UserPermissionProvider> permissionProvider;
    
    /**
     * Генерирует текст справки (/help) персонально для пользователя.
     * <p>
     * Метод фильтрует команды, доступные пользователю, на основе его уровня доступа
     * и прав (Permissions). Скрытые команды ({@code hidden=true}) не отображаются.
     * Команды группируются, алиасы отображаются в скобках.
     * </p>
     *
     * @param user Пользователь, запрашивающий помощь.
     * @return Отформатированный текст справки (HTML).
     */
    public String generate(User user) {
        StringBuilder sb = new StringBuilder(" <b>  </b>\n\n");

        int userLevel = levelProvider.map(p -> p.getUserLevel(user)).orElse(0);
        Set<String> userPerms = permissionProvider.map(p -> p.getUserPermissions(user)).orElse(Collections.emptySet());
        boolean isSuperAdmin = permissionProvider.map(p -> p.isSuperAdmin(user)).orElse(false);

        List<CommandMeta> visibleCommands = registry.getAllCommands().stream()
                .filter(cmd -> isVisible(cmd, userLevel, userPerms, isSuperAdmin))
                .toList();

        Map<Method, List<CommandMeta>> grouped = visibleCommands.stream()
                .collect(Collectors.groupingBy(CommandMeta::getMethod));

        List<Map.Entry<Method, List<CommandMeta>>> sortedEntries = new ArrayList<>(grouped.entrySet());
        sortedEntries.sort(Comparator.comparing(entry -> findPrimary(entry.getValue()).getFullCommandPath()));

        for (Map.Entry<Method, List<CommandMeta>> entry : sortedEntries) {
            List<CommandMeta> variants = entry.getValue();
            
            CommandMeta primary = findPrimary(variants);

            if (primary.getDescription().isEmpty()) continue;

            List<String> aliases = variants.stream()
                    .map(CommandMeta::getFullCommandPath)
                    .filter(path -> !path.equals(primary.getFullCommandPath()))
                    .sorted()
                    .collect(Collectors.toList());

            sb.append(" <b>").append(primary.getFullCommandPath()).append("</b>");

            if (!primary.getUsageSyntax().equals(primary.getFullCommandPath())) {
                 String args = primary.getUsageSyntax().substring(primary.getFullCommandPath().length());
                 sb.append(" ").append(escapeHtml(args));
            }
            
            if (!aliases.isEmpty()) {
                sb.append(" <i>(").append(String.join(", ", aliases)).append(")</i>");
            }

            sb.append("\n    ").append(escapeHtml(primary.getDescription())).append("\n\n");
        }

        return sb.toString();
    }

    private CommandMeta findPrimary(List<CommandMeta> variants) {
        return variants.stream()
                .sorted((c1, c2) -> {
                    String p1 = c1.getFullCommandPath();
                    String p2 = c2.getFullCommandPath();

                    boolean s1 = p1.startsWith("/");
                    boolean s2 = p2.startsWith("/");
                    if (s1 && !s2) return -1;
                    if (!s1 && s2) return 1;

                    int len = Integer.compare(p1.length(), p2.length());
                    if (len != 0) return len;

                    return p1.compareTo(p2);
                })
                .findFirst()
                .orElse(variants.get(0));
    }

    private boolean isVisible(CommandMeta cmd, int userLevel, Set<String> perms, boolean isSuperAdmin) {
        if (isSuperAdmin) return true;
        if (cmd.getLevelRequirement() != null) {
            if (userLevel < cmd.getLevelRequirement().min()) return false;
        }
        if (!cmd.getRequiredPermissions().isEmpty()) {
            for (String req : cmd.getRequiredPermissions()) {
                if (!perms.contains(req) && !perms.contains("*")) return false;
            }
        }
        return true;
    }
    
    private String escapeHtml(String text) {
        if (text == null) return "";
        return text.replace("&", "&amp;")
                   .replace("<", "&lt;")
                   .replace(">", "&gt;")
                   .replace("\"", "&quot;");
    }
}