package com.kaleert.nyagram.api.objects;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.kaleert.nyagram.api.meta.BotApiObject;

/**
 * Этот объект представляет анимационный файл (GIF или видео без звука).
 *
 * @param fileId Идентификатор файла.
 * @param fileUniqueId Уникальный идентификатор файла.
 * @param width Ширина видео.
 * @param height Высота видео.
 * @param duration Длительность видео в секундах.
 * @param thumbnail Превью (обложка).
 * @param fileName Имя файла.
 * @param mimeType MIME-тип файла.
 * @param fileSize Размер файла.
 * 
 * @since 1.0.0
 */
@JsonIgnoreProperties(ignoreUnknown = true)
public record Animation(
    @JsonProperty("file_id") String fileId,
    @JsonProperty("file_unique_id") String fileUniqueId,
    @JsonProperty("width") Integer width,
    @JsonProperty("height") Integer height,
    @JsonProperty("duration") Integer duration,
    @JsonProperty("thumbnail") PhotoSize thumbnail,
    @JsonProperty("file_name") String fileName,
    @JsonProperty("mime_type") String mimeType,
    @JsonProperty("file_size") Long fileSize
) implements BotApiObject {}