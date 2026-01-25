package com.kaleert.nyagram.api.objects;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.kaleert.nyagram.api.meta.BotApiObject;

/**
 * Представляет видеосообщение ("кружочек").
 * <p>
 * Видеосообщения всегда квадратные, имеют длительность до 1 минуты и размер до 640x640.
 * </p>
 *
 * @param fileId Идентификатор файла.
 * @param fileUniqueId Уникальный идентификатор файла.
 * @param length Длина стороны видео (ширина и высота равны).
 * @param duration Длительность в секундах.
 * @param thumbnail Превью (обложка).
 * @param fileSize Размер файла в байтах.
 *
 * @since 1.0.0
 */
@JsonIgnoreProperties(ignoreUnknown = true)
public record VideoNote(
    @JsonProperty("file_id") String fileId,
    @JsonProperty("file_unique_id") String fileUniqueId,
    @JsonProperty("length") Integer length,
    @JsonProperty("duration") Integer duration,
    @JsonProperty("thumbnail") PhotoSize thumbnail,
    @JsonProperty("file_size") Long fileSize
) implements BotApiObject {}