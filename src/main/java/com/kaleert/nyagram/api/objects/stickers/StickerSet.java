package com.kaleert.nyagram.api.objects.stickers;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.kaleert.nyagram.api.meta.BotApiObject;
import com.kaleert.nyagram.api.objects.PhotoSize;

import java.util.List;

/**
 * Представляет набор стикеров.
 *
 * @param name Название набора (short name).
 * @param title Заголовок набора.
 * @param stickerType Тип стикеров ("regular", "mask", "custom_emoji").
 * @param isAnimated True, если набор содержит анимированные стикеры.
 * @param isVideo True, если набор содержит видео-стикеры.
 * @param stickers Список всех стикеров в наборе.
 * @param thumbnail Обложка набора.
 *
 * @since 1.0.0
 */
@JsonIgnoreProperties(ignoreUnknown = true)
public record StickerSet(
    @JsonProperty("name") String name,
    @JsonProperty("title") String title,
    @JsonProperty("sticker_type") String stickerType,
    @JsonProperty("is_animated") Boolean isAnimated,
    @JsonProperty("is_video") Boolean isVideo,
    @JsonProperty("stickers") List<Sticker> stickers,
    @JsonProperty("thumbnail") PhotoSize thumbnail
) implements BotApiObject {

    /**
     * Возвращает ссылку на добавление набора.
     * @return t.me ссылка.
     */
    @JsonIgnore
    public String getUrl() {
        return "https://t.me/addstickers/" + name;
    }
    
    /**
     * Возвращает количество стикеров в наборе.
     * @return число.
     */    
    @JsonIgnore
    public int getCount() {
        return stickers != null ? stickers.size() : 0;
    }
}