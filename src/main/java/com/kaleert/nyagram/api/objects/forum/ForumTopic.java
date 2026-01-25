package com.kaleert.nyagram.api.objects.forum;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.kaleert.nyagram.api.meta.BotApiObject;

/**
 * Представляет топик (ветку) форума в супергруппе.
 *
 * @param messageThreadId Уникальный идентификатор топика (совпадает с message_id первого сообщения в топике).
 * @param name Название топика.
 * @param iconColor Цвет иконки топика в формате RGB.
 * @param iconCustomEmojiId Уникальный идентификатор кастомного эмодзи, используемого как иконка.
 *
 * @since 1.0.0
 */
@JsonIgnoreProperties(ignoreUnknown = true)
public record ForumTopic(
    @JsonProperty("message_thread_id") Integer messageThreadId,
    @JsonProperty("name") String name,
    @JsonProperty("icon_color") Integer iconColor,
    @JsonProperty("icon_custom_emoji_id") String iconCustomEmojiId
) implements BotApiObject {
    
    /**
     * Возвращает уникальный идентификатор топика.
     * <p>
     * Технически это {@code message_id} первого сообщения в топике (сообщения о создании).
     * </p>
     * @return ID топика.
     */
    public Integer getMessageThreadId() { return messageThreadId; }
}