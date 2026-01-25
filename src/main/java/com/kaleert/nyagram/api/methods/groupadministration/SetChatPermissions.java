package com.kaleert.nyagram.api.methods.groupadministration;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.kaleert.nyagram.api.exception.TelegramApiValidationException;
import com.kaleert.nyagram.api.meta.BotApiMethodBoolean;
import com.kaleert.nyagram.api.objects.ChatPermissions;
import lombok.*;

/**
 * Используйте этот метод для установки прав по умолчанию для всех участников чата.
 * <p>
 * Бот должен быть администратором в группе или супергруппе с правом {@code can_restrict_members}.
 * </p>
 *
 * @since 1.0.0
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode(callSuper = false)
@JsonIgnoreProperties(ignoreUnknown = true)
@JsonInclude(JsonInclude.Include.NON_NULL)
public class SetChatPermissions extends BotApiMethodBoolean {
    
    /** Имя метода в Telegram Bot API. */
    public static final String PATH = "setChatPermissions";

    /**
     * Уникальный идентификатор чата.
     */
    @JsonProperty("chat_id")
    private String chatId;

    /**
     * Объект с новыми правами по умолчанию.
     */
    @JsonProperty("permissions")
    private ChatPermissions permissions;

    /**
     * Передайте True, если права должны применяться независимо от настроек чата по умолчанию.
     */
    @JsonProperty("use_independent_chat_permissions")
    private Boolean useIndependentChatPermissions;

    @Override
    public String getMethod() {
        return PATH;
    }

    @Override
    public void validate() throws TelegramApiValidationException {
        if (chatId == null || chatId.isEmpty()) {
            throw new TelegramApiValidationException("ChatId cannot be empty", PATH, "chat_id");
        }
        if (permissions == null) {
            throw new TelegramApiValidationException("Permissions cannot be null", PATH, "permissions");
        }
    }

    /**
     * Устанавливает права по умолчанию, запрещающие отправку любых сообщений (закрытие чата).
     * Администраторы по-прежнему смогут писать.
     *
     * @param chatId ID чата.
     * @return готовый объект запроса.
     */
    public static SetChatPermissions muteChat(Long chatId) {
        return SetChatPermissions.builder()
                .chatId(chatId.toString())
                .permissions(ChatPermissions.muteAll())
                .build();
    }
    
    /**
     * Создает запрос на установку конкретных прав для всех участников чата.
     *
     * @param chatId ID чата.
     * @param permissions Объект с правами.
     * @return готовый объект запроса.
     */
    public static SetChatPermissions of(Long chatId, ChatPermissions permissions) {
        return SetChatPermissions.builder()
                .chatId(chatId.toString())
                .permissions(permissions)
                .build();
    }
}