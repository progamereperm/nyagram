package com.kaleert.nyagram.api.objects.chatmember;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.kaleert.nyagram.api.objects.User;

/**
 * Представляет участника чата с правами администратора.
 *
 * @param user Информация о пользователе.
 * @param canBeEdited True, если бот может редактировать права этого администратора.
 * @param isAnonymous True, если пользователь скрыт (анонимный админ).
 * @param canManageChat True, если админ может управлять чатом (менять настройки, видеть логи).
 * @param canDeleteMessages True, если админ может удалять сообщения других.
 * @param canManageVideoChats True, если админ может управлять видеочатами.
 * @param canRestrictMembers True, если админ может ограничивать (банить/мутить) пользователей.
 * @param canPromoteMembers True, если админ может назначать других админов.
 * @param canChangeInfo True, если админ может менять информацию о чате.
 * @param canInviteUsers True, если админ может приглашать пользователей.
 * @param canPostMessages True, если админ может писать сообщения (для каналов).
 * @param canEditMessages True, если админ может редактировать сообщения других.
 * @param canPinMessages True, если админ может закреплять сообщения.
 * @param canManageTopics True, если админ может управлять темами форума.
 * @param customTitle Кастомный титул (должность) администратора.
 * @param canPostStories True, если админ может постить истории от имени чата.
 * @param canEditStories True, если админ может редактировать истории.
 * @param canDeleteStories True, если админ может удалять истории.
 *
 * @since 1.0.0
 */
@JsonIgnoreProperties(ignoreUnknown = true)
public record ChatMemberAdministrator(
    @JsonProperty("user") User user,
    @JsonProperty("can_be_edited") Boolean canBeEdited,
    @JsonProperty("is_anonymous") Boolean isAnonymous,
    @JsonProperty("can_manage_chat") Boolean canManageChat,
    @JsonProperty("can_delete_messages") Boolean canDeleteMessages,
    @JsonProperty("can_manage_video_chats") Boolean canManageVideoChats,
    @JsonProperty("can_restrict_members") Boolean canRestrictMembers,
    @JsonProperty("can_promote_members") Boolean canPromoteMembers,
    @JsonProperty("can_change_info") Boolean canChangeInfo,
    @JsonProperty("can_invite_users") Boolean canInviteUsers,
    @JsonProperty("can_post_messages") Boolean canPostMessages,
    @JsonProperty("can_edit_messages") Boolean canEditMessages,
    @JsonProperty("can_pin_messages") Boolean canPinMessages,
    @JsonProperty("can_manage_topics") Boolean canManageTopics,
    @JsonProperty("custom_title") String customTitle,
    @JsonProperty("can_post_stories") Boolean canPostStories,
    @JsonProperty("can_edit_stories") Boolean canEditStories,
    @JsonProperty("can_delete_stories") Boolean canDeleteStories
) implements ChatMember {
    
    @Override
    public String getStatus() {
        return "administrator";
    }
    
    @Override
    public User getUser() {
        return user;
    }
}