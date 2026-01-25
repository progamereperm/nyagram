package com.kaleert.nyagram.api.objects.inlinequery.result;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.kaleert.nyagram.api.objects.inlinequery.inputmessagecontent.InputMessageContent;
import com.kaleert.nyagram.api.objects.replykeyboard.InlineKeyboardMarkup;
import lombok.Builder;

/**
 * Представляет ссылку на статью или веб-страницу.
 *
 * @param id Уникальный идентификатор результата.
 * @param title Заголовок результата.
 * @param inputMessageContent Содержимое сообщения, которое будет отправлено при выборе.
 * @param replyMarkup Inline-клавиатура.
 * @param url URL результата.
 * @param description Краткое описание.
 * @param thumbnailUrl URL миниатюры.
 * @param thumbnailWidth Ширина миниатюры.
 * @param thumbnailHeight Высота миниатюры.
 *
 * @since 1.0.0
 */
@Builder
public record InlineQueryResultArticle(
    @JsonProperty("id") String id,
    @JsonProperty("title") String title,
    @JsonProperty("input_message_content") InputMessageContent inputMessageContent,
    @JsonProperty("reply_markup") InlineKeyboardMarkup replyMarkup,
    @JsonProperty("url") String url,
    @JsonProperty("description") String description,
    @JsonProperty("thumbnail_url") String thumbnailUrl,
    @JsonProperty("thumbnail_width") Integer thumbnailWidth,
    @JsonProperty("thumbnail_height") Integer thumbnailHeight
) implements InlineQueryResult {

    @Override
    public String getType() {
        return "article";
    }

    @Override public String getId() { return id; }
    @Override public InlineKeyboardMarkup getReplyMarkup() { return replyMarkup; }
    
    /**
     * Создает простой текстовый результат (статью).
     * @param id ID результата.
     * @param title Заголовок.
     * @param messageText Текст отправляемого сообщения.
     * @return объект InlineQueryResultArticle.
     */
    public static InlineQueryResultArticle simple(String id, String title, String messageText) {
        return InlineQueryResultArticle.builder()
                .id(id)
                .title(title)
                .inputMessageContent(com.kaleert.nyagram.api.objects.inlinequery.inputmessagecontent.InputTextMessageContent.of(messageText))
                .build();
    }
}