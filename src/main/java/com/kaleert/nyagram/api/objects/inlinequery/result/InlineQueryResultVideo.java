package com.kaleert.nyagram.api.objects.inlinequery.result;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.kaleert.nyagram.api.objects.inlinequery.inputmessagecontent.InputMessageContent;
import com.kaleert.nyagram.api.objects.replykeyboard.InlineKeyboardMarkup;
import lombok.Builder;

/**
 * Представляет ссылку на видеофайл или страницу с видеоплеером.
 *
 * @param id Уникальный идентификатор результата.
 * @param videoUrl Действительный URL видео или страницы.
 * @param mimeType MIME-тип контента ("text/html" или "video/mp4").
 * @param thumbnailUrl URL миниатюры (обязательно).
 * @param title Заголовок видео.
 * @param caption Подпись к видео.
 * @param description Краткое описание.
 * @param replyMarkup Inline-клавиатура.
 * @param inputMessageContent Содержимое сообщения (если отличается от видео).
 *
 * @since 1.0.0
 */
@Builder
public record InlineQueryResultVideo(
    @JsonProperty("id") String id,
    @JsonProperty("video_url") String videoUrl,
    @JsonProperty("mime_type") String mimeType, // "text/html" or "video/mp4"
    @JsonProperty("thumbnail_url") String thumbnailUrl,
    @JsonProperty("title") String title,
    @JsonProperty("caption") String caption,
    @JsonProperty("description") String description,
    @JsonProperty("reply_markup") InlineKeyboardMarkup replyMarkup,
    @JsonProperty("input_message_content") InputMessageContent inputMessageContent
) implements InlineQueryResult {

    @Override
    public String getType() {
        return "video";
    }
    
    @Override public String getId() { return id; }
    @Override public InlineKeyboardMarkup getReplyMarkup() { return replyMarkup; }
}