package com.kaleert.nyagram.api.objects.inlinequery.result;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.kaleert.nyagram.api.objects.inlinequery.inputmessagecontent.InputMessageContent;
import com.kaleert.nyagram.api.objects.replykeyboard.InlineKeyboardMarkup;
import lombok.Builder;

/**
 * Представляет ссылку на анимированный GIF файл.
 *
 * @param id Уникальный идентификатор результата.
 * @param gifUrl Действительный URL GIF-файла.
 * @param gifWidth Ширина GIF.
 * @param gifHeight Высота GIF.
 * @param thumbnailUrl URL статической миниатюры (JPEG или PNG).
 * @param title Заголовок (опционально).
 * @param caption Подпись к GIF (0-1024 символов).
 * @param parseMode Режим парсинга подписи.
 * @param replyMarkup Inline-клавиатура.
 * @param inputMessageContent Содержимое сообщения (если отличается от самого GIF).
 *
 * @since 1.0.0
 */
@Builder
public record InlineQueryResultGif(
    @JsonProperty("id") String id,
    @JsonProperty("gif_url") String gifUrl,
    @JsonProperty("gif_width") Integer gifWidth,
    @JsonProperty("gif_height") Integer gifHeight,
    @JsonProperty("thumbnail_url") String thumbnailUrl,
    @JsonProperty("title") String title,
    @JsonProperty("caption") String caption,
    @JsonProperty("parse_mode") String parseMode,
    @JsonProperty("reply_markup") InlineKeyboardMarkup replyMarkup,
    @JsonProperty("input_message_content") InputMessageContent inputMessageContent
) implements InlineQueryResult {
    @Override public String getType() { return "gif"; }
    @Override public String getId() { return id; }
    @Override public InlineKeyboardMarkup getReplyMarkup() { return replyMarkup; }
}