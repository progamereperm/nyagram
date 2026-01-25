package com.kaleert.nyagram.api.objects;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.kaleert.nyagram.api.meta.BotApiObject;

/**
 * Представляет голосовое сообщение.
 *
 * @param fileId Идентификатор файла.
 * @param fileUniqueId Уникальный идентификатор файла.
 * @param duration Длительность аудио в секундах.
 * @param mimeType MIME-тип файла (обычно audio/ogg).
 * @param fileSize Размер файла в байтах.
 *
 * @since 1.0.0
 */
@JsonIgnoreProperties(ignoreUnknown = true)
public record Voice(
    @JsonProperty("file_id") String fileId,
    @JsonProperty("file_unique_id") String fileUniqueId,
    @JsonProperty("duration") Integer duration,
    @JsonProperty("mime_type") String mimeType,
    @JsonProperty("file_size") Long fileSize
) implements BotApiObject {}