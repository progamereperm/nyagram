package com.kaleert.nyagram.api.methods.groupadministration;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.kaleert.nyagram.api.exception.TelegramApiValidationException;
import com.kaleert.nyagram.api.meta.BotApiMethodBoolean;
import lombok.*;

/**
 * Используйте этот метод для повышения или понижения пользователя в чате.
 * <p>
 * Позволяет назначать администраторов и настраивать их права.
 * Бот должен быть администратором с соответствующими правами.
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
public class PromoteChatMember extends BotApiMethodBoolean {
    
    /** Имя метода в Telegram Bot API. */
    public static final String PATH = "promoteChatMember";
    
    /**
     * Уникальный идентификатор чата.
     */
    @JsonProperty("chat_id")
    private String chatId;
    
    /**
     * Уникальный идентификатор пользователя.
     */
    @JsonProperty("user_id")
    private Long userId;
    
    /**
     * Если true, админ будет отображаться анонимно в списке и сообщениях.
     */
    @JsonProperty("is_anonymous")
    private Boolean isAnonymous;
    
    /**
     * Право изменять информацию о чате.
     */
    @JsonProperty("can_manage_chat")
    private Boolean canManageChat;
    
    /**
     * Право публиковать сообщения в каналах.
     */
    @JsonProperty("can_post_messages")
    private Boolean canPostMessages;
    
    /**
     * Право редактировать сообщения других пользователей.
     */
    @JsonProperty("can_edit_messages")
    private Boolean canEditMessages;
    
    /**
     * Право удалять сообщения других пользователей.
     */
    @JsonProperty("can_delete_messages")
    private Boolean canDeleteMessages;
    
    /**
     * Право управлять видеочатами.
     */
    @JsonProperty("can_manage_video_chats")
    private Boolean canManageVideoChats;
    
    /**
     * Право ограничивать пользователей.
     */
    @JsonProperty("can_restrict_members")
    private Boolean canRestrictMembers;
    
    /**
     * Право добавлять новых администраторов.
     */
    @JsonProperty("can_promote_members")
    private Boolean canPromoteMembers;
    
    /**
     * Право изменять информацию о группе.
     */
    @JsonProperty("can_change_info")
    private Boolean canChangeInfo;
    
    /**
     * Право приглашать пользователей.
     */
    @JsonProperty("can_invite_users")
    private Boolean canInviteUsers;
    
    /**
     * Право закреплять сообщения.
     */
    @JsonProperty("can_pin_messages")
    private Boolean canPinMessages;
    
    /**
     * Право публиковать истории от имени чата.
     */
    @JsonProperty("can_post_stories")
    private Boolean canPostStories;
    
    /**
     * Право редактировать истории.
     */
    @JsonProperty("can_edit_stories")
    private Boolean canEditStories;
    
    /**
     * Право удалять истории.
     */
    @JsonProperty("can_delete_stories")
    private Boolean canDeleteStories;
    
    /**
     * Право управлять темами форума.
     */
    @JsonProperty("can_manage_topics")
    private Boolean canManageTopics;

    @Override
    public String getMethod() {
        return PATH;
    }

    @Override
    public void validate() throws TelegramApiValidationException {
        if (chatId == null || chatId.isEmpty()) {
            throw new TelegramApiValidationException("ChatId cannot be empty", PATH, "chat_id");
        }
        if (userId == null || userId == 0) {
            throw new TelegramApiValidationException("UserId cannot be empty", PATH, "user_id");
        }
    }
    
    /**
     * Создает запрос на выдачу пользователю полных прав администратора.
     * <p>
     * Включает права на управление чатом, сообщениями, видеочатами, блокировку,
     * добавление админов и т.д. Анонимность по умолчанию выключена.
     * </p>
     *
     * @param chatId ID чата.
     * @param userId ID пользователя.
     * @return готовый объект запроса с полными правами.
     */
    public static PromoteChatMember fullRights(Long chatId, Long userId) {
        return PromoteChatMember.builder()
                .chatId(chatId.toString())
                .userId(userId)
                .canManageChat(true)
                .canPostMessages(true)
                .canEditMessages(true)
                .canDeleteMessages(true)
                .canManageVideoChats(true)
                .canRestrictMembers(true)
                .canPromoteMembers(true)
                .canChangeInfo(true)
                .canInviteUsers(true)
                .canPinMessages(true)
                .canPostStories(true)
                .canEditStories(true)
                .canDeleteStories(true)
                .canManageTopics(true)
                .isAnonymous(false)
                .build();
    }
    
    /**
     * Делает администратора анонимным.
     * В списке участников он не будет отображаться, а сообщения будут отправляться от имени группы.
     *
     * @return текущий билдер.
     */
    public PromoteChatMember anonymous() {
        this.isAnonymous = true;
        return this;
    }
}