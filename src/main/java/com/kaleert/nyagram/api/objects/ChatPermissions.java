package com.kaleert.nyagram.api.objects;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.kaleert.nyagram.api.meta.BotApiObject;
import lombok.Builder;

/**
 * Описывает действия, которые разрешены пользователю (не администратору) в чате.
 *
 * @param canSendMessages True, если пользователь может отправлять текстовые сообщения.
 * @param canSendAudios True, если пользователь может отправлять аудиофайлы.
 * @param canSendDocuments True, если пользователь может отправлять документы.
 * @param canSendPhotos True, если пользователь может отправлять фото.
 * @param canSendVideos True, если пользователь может отправлять видео.
 * @param canSendVideoNotes True, если пользователь может отправлять видеосообщения ("кружочки").
 * @param canSendVoiceNotes True, если пользователь может отправлять голосовые сообщения.
 * @param canSendPolls True, если пользователь может отправлять опросы.
 * @param canSendOtherMessages True, если пользователь может отправлять анимации, игры, стикеры.
 * @param canAddWebPagePreviews True, если пользователь может добавлять превью ссылок к сообщениям.
 * @param canChangeInfo True, если пользователь может менять информацию о чате.
 * @param canInviteUsers True, если пользователь может приглашать других пользователей.
 * @param canPinMessages True, если пользователь может закреплять сообщения.
 * @param canManageTopics True, если пользователь может управлять темами форума.
 *
 * @since 1.0.0
 */
@Builder(toBuilder = true)
@JsonInclude(JsonInclude.Include.NON_NULL)
@JsonIgnoreProperties(ignoreUnknown = true)
public record ChatPermissions(
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
    @JsonProperty("can_manage_topics") Boolean canManageTopics
) implements BotApiObject {
    
    /**
     * Создает объект прав, где всё запрещено (Read-Only режим).
     * @return объект ChatPermissions.
     */
    public static ChatPermissions muteAll() {
        return ChatPermissions.builder()
                .canSendMessages(false)
                .canSendAudios(false)
                .canSendDocuments(false)
                .canSendPhotos(false)
                .canSendVideos(false)
                .canSendVideoNotes(false)
                .canSendVoiceNotes(false)
                .canSendPolls(false)
                .canSendOtherMessages(false)
                .canAddWebPagePreviews(false)
                .canChangeInfo(false)
                .canInviteUsers(false)
                .canPinMessages(false)
                .canManageTopics(false)
                .build();
    }
    
    /**
     * Создает объект с полными правами (всё разрешено).
     * @return объект ChatPermissions.
     */
    public static ChatPermissions full() {
        return ChatPermissions.builder()
                .canSendMessages(true)
                .canSendAudios(true)
                .canSendDocuments(true)
                .canSendPhotos(true)
                .canSendVideos(true)
                .canSendVideoNotes(true)
                .canSendVoiceNotes(true)
                .canSendPolls(true)
                .canSendOtherMessages(true)
                .canAddWebPagePreviews(true)
                .canChangeInfo(true)
                .canInviteUsers(true)
                .canPinMessages(true)
                .canManageTopics(true)
                .build();
    }

    /**
     * Создает объект прав, где разрешены только текстовые сообщения.
     * @return объект ChatPermissions.
     */
    public static ChatPermissions textOnly() {
        return muteAll().toBuilder()
                .canSendMessages(true)
                .build();
    }
    
    /**
     * Проверяет, разрешена ли отправка любых медиафайлов.
     * @return true, если разрешено хотя бы что-то из: аудио, док, фото, видео, кружочки, войсы.
     */
    public boolean canSendMediaMessages() {
        return Boolean.TRUE.equals(canSendAudios) ||
               Boolean.TRUE.equals(canSendDocuments) ||
               Boolean.TRUE.equals(canSendPhotos) ||
               Boolean.TRUE.equals(canSendVideos) ||
               Boolean.TRUE.equals(canSendVideoNotes) ||
               Boolean.TRUE.equals(canSendVoiceNotes);
    }
}