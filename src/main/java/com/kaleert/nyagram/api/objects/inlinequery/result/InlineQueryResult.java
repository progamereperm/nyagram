package com.kaleert.nyagram.api.objects.inlinequery.result;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonSubTypes;
import com.fasterxml.jackson.annotation.JsonTypeInfo;
import com.kaleert.nyagram.api.meta.BotApiObject;
import com.kaleert.nyagram.api.objects.replykeyboard.InlineKeyboardMarkup;

/**
 * Базовый интерфейс для результатов inline-запроса.
 *
 * @since 1.0.0
 */
@JsonTypeInfo(
    use = JsonTypeInfo.Id.NAME,
    include = JsonTypeInfo.As.PROPERTY,
    property = "type"
)
@JsonSubTypes({
    @JsonSubTypes.Type(value = InlineQueryResultArticle.class, name = "article"),
    @JsonSubTypes.Type(value = InlineQueryResultPhoto.class, name = "photo"),
    @JsonSubTypes.Type(value = InlineQueryResultVideo.class, name = "video"),
    @JsonSubTypes.Type(value = InlineQueryResultGif.class, name = "gif")
})
@JsonInclude(JsonInclude.Include.NON_NULL)
public sealed interface InlineQueryResult extends BotApiObject 
    permits InlineQueryResultArticle, InlineQueryResultPhoto, InlineQueryResultVideo, InlineQueryResultGif {
    
    /**
     * Тип результата (article, photo, video, gif и т.д.).
     */
    String getType();
    
    /**
     * Уникальный идентификатор результата (1-64 байта).
     */
    String getId();
    
    /**
     * Inline-клавиатура, прикрепленная к сообщению.
     */
    InlineKeyboardMarkup getReplyMarkup();
}