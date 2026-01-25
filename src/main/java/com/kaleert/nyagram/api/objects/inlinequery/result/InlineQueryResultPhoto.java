package com.kaleert.nyagram.api.objects.inlinequery.result;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.kaleert.nyagram.api.objects.inlinequery.inputmessagecontent.InputMessageContent;
import com.kaleert.nyagram.api.objects.replykeyboard.InlineKeyboardMarkup;
import lombok.Builder;

/**
 * Представляет ссылку на фотографию.
 *
 * @param id Уникальный идентификатор результата.
 * @param photoUrl Действительный URL фото.
 * @param thumbnailUrl URL миниатюры.
 * @param photoWidth Ширина фото.
 * @param photoHeight Высота фото.
 * @param title Заголовок результата.
 * @param description Краткое описание.
 * @param caption Подпись к фото.
 * @param parseMode Режим парсинга подписи.
 * @param replyMarkup Inline-клавиатура.
 * @param inputMessageContent Содержимое сообщения (если отличается от фото).
 *
 * @since 1.0.0
 */
@Builder
public record InlineQueryResultPhoto(
    @JsonProperty("id") String id,
    @JsonProperty("photo_url") String photoUrl,
    @JsonProperty("thumbnail_url") String thumbnailUrl,
    @JsonProperty("photo_width") Integer photoWidth,
    @JsonProperty("photo_height") Integer photoHeight,
    @JsonProperty("title") String title,
    @JsonProperty("description") String description,
    @JsonProperty("caption") String caption,
    @JsonProperty("parse_mode") String parseMode,
    @JsonProperty("reply_markup") InlineKeyboardMarkup replyMarkup,
    @JsonProperty("input_message_content") InputMessageContent inputMessageContent
) implements InlineQueryResult {

    @Override
    public String getType() {
        return "photo";
    }
    
    @Override public String getId() { return id; }
    @Override public InlineKeyboardMarkup getReplyMarkup() { return replyMarkup; }
}