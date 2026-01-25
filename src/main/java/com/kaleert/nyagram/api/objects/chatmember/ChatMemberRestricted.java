package com.kaleert.nyagram.api.objects.chatmember;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.kaleert.nyagram.api.objects.User;

/**
 * Представляет участника чата с ограничениями (в муте или с урезанными правами).
 *
 * @param user Информация о пользователе.
 * @param isMember True, если пользователь является участником чата (не был удален, просто ограничен).
 * @param canSendMessages True, если может отправлять текстовые сообщения.
 * @param canSendAudios True, если может отправлять аудио.
 * @param canSendDocuments True, если может отправлять документы.
 * @param canSendPhotos True, если может отправлять фото.
 * @param canSendVideos True, если может отправлять видео.
 * @param canSendVideoNotes True, если может отправлять видеосообщения.
 * @param canSendVoiceNotes True, если может отправлять голосовые.
 * @param canSendPolls True, если может отправлять опросы.
 * @param canSendOtherMessages True, если может отправлять другое (стикеры, игры).
 * @param canAddWebPagePreviews True, если может добавлять превью ссылок.
 * @param canChangeInfo True, если может менять информацию о чате.
 * @param canInviteUsers True, если может приглашать пользователей.
 * @param canPinMessages True, если может закреплять сообщения.
 * @param canManageTopics True, если может управлять топиками.
 * @param untilDate Дата окончания ограничений (Unix timestamp). 0 - навсегда.
 *
 * @since 1.0.0
 */
@JsonIgnoreProperties(ignoreUnknown = true)
public record ChatMemberRestricted(
    @JsonProperty("user") User user,
    @JsonProperty("is_member") Boolean isMember,
    @JsonProperty("can_send_messages") Boolean canSendMessages,
    @JsonProperty("can_send_audios") Boolean canSendAudios,
    @JsonProperty("can_send_documents") Boolean canSendDocuments,
    @JsonProperty("can_send_photos") Boolean canSendPhotos,
    @JsonProperty("can_send_videos") Boolean canSendVideos,
    @JsonProperty("can_send_video_notes") Boolean canSendVideoNotes,
    @JsonProperty("can_send_voice_notes") Boolean canSendVoiceNotes,
    @JsonProperty("can_send_polls") Boolean canSendPolls,
    @JsonProperty("can_send_other_messages") Boolean canSendOtherMessages,
    @JsonProperty("can_add_web_page_previews") Boolean canAddWebPagePreviews,
    @JsonProperty("can_change_info") Boolean canChangeInfo,
    @JsonProperty("can_invite_users") Boolean canInviteUsers,
    @JsonProperty("can_pin_messages") Boolean canPinMessages,
    @JsonProperty("can_manage_topics") Boolean canManageTopics,
    @JsonProperty("until_date") Integer untilDate
) implements ChatMember {

    @Override
    public String getStatus() {
        return "restricted";
    }
    
    /**
     * Проверяет, являются ли ограничения бессрочными.
     * @return true, если untilDate == 0 или null.
     */
    @JsonIgnore
    public boolean isForever() {
        return untilDate == null || untilDate == 0;
    }
    
    /**
     * Проверяет, находится ли пользователь в полном Read-Only режиме.
     * @return true, если запрещена отправка любых типов сообщений.
     */
    @JsonIgnore
    public boolean isReadOnly() {
        return Boolean.FALSE.equals(canSendMessages) &&
               Boolean.FALSE.equals(canSendAudios) &&
               Boolean.FALSE.equals(canSendDocuments) &&
               Boolean.FALSE.equals(canSendPhotos) &&
               Boolean.FALSE.equals(canSendVideos) &&
               Boolean.FALSE.equals(canSendVideoNotes) &&
               Boolean.FALSE.equals(canSendVoiceNotes) &&
               Boolean.FALSE.equals(canSendPolls) &&
               Boolean.FALSE.equals(canSendOtherMessages);
    }
    
    @Override
    public User getUser() {
        return user;
    }
}