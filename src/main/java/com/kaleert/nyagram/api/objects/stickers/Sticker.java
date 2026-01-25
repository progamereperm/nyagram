package com.kaleert.nyagram.api.objects.stickers;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.kaleert.nyagram.api.meta.BotApiObject;
import com.kaleert.nyagram.api.objects.File;
import com.kaleert.nyagram.api.objects.PhotoSize;

/**
 * Представляет стикер.
 *
 * @param fileId Идентификатор файла стикера.
 * @param fileUniqueId Уникальный идентификатор файла.
 * @param type Тип стикера ("regular", "mask", "custom_emoji").
 * @param width Ширина стикера.
 * @param height Высота стикера.
 * @param isAnimated True, если это анимированный стикер (.tgs).
 * @param isVideo True, если это видео-стикер (.webm).
 * @param thumbnail Обложка (превью) стикера.
 * @param emoji Эмодзи, соответствующий стикеру.
 * @param setName Название набора, к которому принадлежит стикер.
 * @param premiumAnimation Премиум анимация (для premium стикеров).
 * @param maskPosition Позиция маски (если type="mask").
 * @param customEmojiId ID кастомного эмодзи (если type="custom_emoji").
 * @param needsRepainting True, если цвет стикера должен меняться под цвет текста.
 * @param fileSize Размер файла в байтах.
 *
 * @since 1.0.0
 */
@JsonIgnoreProperties(ignoreUnknown = true)
public record Sticker(
    @JsonProperty("file_id") String fileId,
    @JsonProperty("file_unique_id") String fileUniqueId,
    @JsonProperty("type") String type,
    @JsonProperty("width") Integer width,
    @JsonProperty("height") Integer height,
    @JsonProperty("is_animated") Boolean isAnimated,
    @JsonProperty("is_video") Boolean isVideo,
    @JsonProperty("thumbnail") PhotoSize thumbnail,
    @JsonProperty("emoji") String emoji,
    @JsonProperty("set_name") String setName,
    @JsonProperty("premium_animation") File premiumAnimation,
    @JsonProperty("mask_position") MaskPosition maskPosition,
    @JsonProperty("custom_emoji_id") String customEmojiId,
    @JsonProperty("needs_repainting") Boolean needsRepainting,
    @JsonProperty("file_size") Long fileSize
) implements BotApiObject {
    
    /**
     * Проверяет, является ли стикер обычным (не маской и не кастомным эмодзи).
     * @return true, если type="regular".
     */
    @JsonIgnore
    public boolean isRegular() {
        return "regular".equals(type);
    }
    
    /**
     * Проверяет, является ли стикер маской.
     * @return true, если type="mask".
     */
    @JsonIgnore
    public boolean isMask() {
        return "mask".equals(type);
    }

    /**
     * Проверяет, является ли стикер кастомным эмодзи.
     * Такие стикеры можно использовать внутри текста сообщений (для Premium).
     * @return true, если type="custom_emoji".
     */
    @JsonIgnore
    public boolean isCustomEmoji() {
        return "custom_emoji".equals(type);
    }
    
    /**
     * Проверяет, является ли стикер движущимся (анимированным или видео).
     * @return true, если стикер не статичный.
     */
    @JsonIgnore
    public boolean isMoving() {
        return Boolean.TRUE.equals(isAnimated) || Boolean.TRUE.equals(isVideo);
    }
    
    /**
     * Возвращает ссылку на набор стикеров.
     * @return URL или null, если setName отсутствует.
     */
    @JsonIgnore
    public String getStickerLink() {
        if (setName != null) {
            return "https://t.me/addstickers/" + setName;
        }
        return null;
    }
}