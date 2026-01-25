package com.kaleert.nyagram.api.objects;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.kaleert.nyagram.api.meta.BotApiObject;

/**
 * Представляет видеофайл.
 *
 * @param fileId Идентификатор файла.
 * @param fileUniqueId Уникальный идентификатор файла.
 * @param width Ширина видео.
 * @param height Высота видео.
 * @param duration Длительность видео в секундах.
 * @param thumbnail Превью (обложка) видео.
 * @param fileName Имя оригинального файла.
 * @param mimeType MIME-тип файла.
 * @param fileSize Размер файла в байтах.
 *
 * @since 1.0.0
 */
@JsonIgnoreProperties(ignoreUnknown = true)
public record Video(
    @JsonProperty("file_id") String fileId,
    @JsonProperty("file_unique_id") String fileUniqueId,
    @JsonProperty("width") Integer width,
    @JsonProperty("height") Integer height,
    @JsonProperty("duration") Integer duration,
    @JsonProperty("thumbnail") PhotoSize thumbnail,
    @JsonProperty("file_name") String fileName,
    @JsonProperty("mime_type") String mimeType,
    @JsonProperty("file_size") Long fileSize
) implements BotApiObject {

    /**
     * Проверяет, является ли видео квадратным (1:1).
     * @return true, если ширина равна высоте.
     */
    @JsonIgnore
    public boolean isSquare() {
        return width != null && height != null && width.equals(height);
    }

    /**
     * Проверяет, является ли видео вертикальным (портретным).
     * @return true, если высота больше ширины.
     */
    @JsonIgnore
    public boolean isVertical() {
        return height != null && width != null && height > width;
    }

    /**
     * Проверяет, является ли видео горизонтальным (альбомным).
     * @return true, если ширина больше высоты.
     */
    @JsonIgnore
    public boolean isHorizontal() {
        return width != null && height != null && width > height;
    }

    /**
     * Возвращает соотношение сторон (Aspect Ratio).
     * @return коэффициент (ширина / высота) или 0, если размеры неизвестны.
     */
    @JsonIgnore
    public double getAspectRatio() {
        if (width == null || height == null || height == 0) return 0;
        return (double) width / height;
    }
}