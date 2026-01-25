package com.kaleert.nyagram.api.objects.commands.scope;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonSubTypes;
import com.fasterxml.jackson.annotation.JsonTypeInfo;
import com.kaleert.nyagram.api.meta.BotApiObject;
import lombok.NonNull;

/**
 * Определяет область видимости команд бота (кому они будут показаны).
 * <p>
 * Используется в методах {@code setMyCommands}, {@code deleteMyCommands} и {@code getMyCommands}.
 * </p>
 *
 * @since 1.0.0
 */
@JsonTypeInfo(
    use = JsonTypeInfo.Id.NAME,
    include = JsonTypeInfo.As.PROPERTY,
    property = "type"
)
@JsonSubTypes({
    @JsonSubTypes.Type(value = BotCommandScopeDefault.class, name = "default"),
    @JsonSubTypes.Type(value = BotCommandScopeAllPrivateChats.class, name = "all_private_chats"),
    @JsonSubTypes.Type(value = BotCommandScopeAllGroupChats.class, name = "all_group_chats"),
    @JsonSubTypes.Type(value = BotCommandScopeAllChatAdministrators.class, name = "all_chat_administrators"),
    @JsonSubTypes.Type(value = BotCommandScopeChat.class, name = "chat"),
    @JsonSubTypes.Type(value = BotCommandScopeChatAdministrators.class, name = "chat_administrators"),
    @JsonSubTypes.Type(value = BotCommandScopeChatMember.class, name = "chat_member")
})
public sealed interface BotCommandScope extends BotApiObject 
    permits BotCommandScopeDefault, BotCommandScopeAllPrivateChats, BotCommandScopeAllGroupChats, 
            BotCommandScopeAllChatAdministrators, BotCommandScopeChat, BotCommandScopeChatAdministrators, 
            BotCommandScopeChatMember {

    String getType();

    static BotCommandScopeDefault def() {
        return new BotCommandScopeDefault();
    }

    static BotCommandScopeAllPrivateChats allPrivateChats() {
        return new BotCommandScopeAllPrivateChats();
    }

    static BotCommandScopeAllGroupChats allGroupChats() {
        return new BotCommandScopeAllGroupChats();
    }

    static BotCommandScopeAllChatAdministrators allChatAdmins() {
        return new BotCommandScopeAllChatAdministrators();
    }

    static BotCommandScopeChat chat(Long chatId) {
        return new BotCommandScopeChat(chatId.toString());
    }
    
    static BotCommandScopeChatAdministrators chatAdmins(Long chatId) {
        return new BotCommandScopeChatAdministrators(chatId.toString());
    }

    static BotCommandScopeChatMember member(Long chatId, Long userId) {
        return new BotCommandScopeChatMember(chatId.toString(), userId);
    }
}

/**
 * Область по умолчанию. Команды будут показаны всем пользователям,
 * если для них не задана более специфичная область.
 */
@JsonIgnoreProperties(ignoreUnknown = true)
record BotCommandScopeDefault() implements BotCommandScope {
    @Override public String getType() { return "default"; }
}

/**
 * Область всех приватных чатов.
 */
@JsonIgnoreProperties(ignoreUnknown = true)
record BotCommandScopeAllPrivateChats() implements BotCommandScope {
    @Override public String getType() { return "all_private_chats"; }
}

/**
 * Область всех групповых чатов (групп и супергрупп).
 * <p>
 * Команды будут применены ко всем группам, в которых состоит бот.
 * </p>
 */
@JsonIgnoreProperties(ignoreUnknown = true)
record BotCommandScopeAllGroupChats() implements BotCommandScope {
    @Override public String getType() { return "all_group_chats"; }
}

/**
 * Область администраторов конкретного чата.
 */
@JsonIgnoreProperties(ignoreUnknown = true)
record BotCommandScopeAllChatAdministrators() implements BotCommandScope {
    @Override public String getType() { return "all_chat_administrators"; }
}

/**
 * Область конкретного чата.
 *
 * @param chatId Уникальный идентификатор целевого чата или username целевого суперканала (в формате {@code @supergroupusername}).
 */
@JsonIgnoreProperties(ignoreUnknown = true)
record BotCommandScopeChat(
    @JsonProperty("chat_id") @NonNull String chatId
) implements BotCommandScope {
    @Override public String getType() { return "chat"; }
}

/**
 * Область администраторов конкретного чата.
 *
 * @param chatId Уникальный идентификатор целевого чата или username целевого суперканала.
 */
@JsonIgnoreProperties(ignoreUnknown = true)
record BotCommandScopeChatAdministrators(
    @JsonProperty("chat_id") @NonNull String chatId
) implements BotCommandScope {
    @Override public String getType() { return "chat_administrators"; }
}

/**
 * Область конкретного участника в конкретном чате.
 *
 * @param chatId Уникальный идентификатор целевого чата или username целевого суперканала.
 * @param userId Уникальный идентификатор целевого пользователя.
 */
@JsonIgnoreProperties(ignoreUnknown = true)
record BotCommandScopeChatMember(
    @JsonProperty("chat_id") @NonNull String chatId,
    @JsonProperty("user_id") @NonNull Long userId
) implements BotCommandScope {
    @Override public String getType() { return "chat_member"; }
}