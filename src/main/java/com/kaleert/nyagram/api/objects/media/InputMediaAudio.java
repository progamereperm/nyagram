package com.kaleert.nyagram.api.objects.media;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.kaleert.nyagram.api.exception.TelegramApiValidationException;
import com.kaleert.nyagram.api.objects.InputFile;
import com.kaleert.nyagram.api.objects.message.MessageEntity;
import lombok.Builder;

import java.util.List;

/**
 * Представляет аудиофайл, который будет отправлен как часть альбома.
 *
 * @param media Файл для отправки (file_id, URL или attach://).
 * @param caption Подпись к аудио.
 * @param parseMode Режим парсинга подписи.
 * @param captionEntities Сущности в подписи.
 * @param duration Длительность в секундах.
 * @param performer Исполнитель.
 * @param title Название трека.
 * @param thumbnail Обложка альбома.
 * @param mediaFile Локальный файл для загрузки (не сериализуется в JSON).
 *
 * @since 1.0.0
 */
@Builder(toBuilder = true)
public record InputMediaAudio(
    @JsonProperty("media") String media,
    @JsonProperty("caption") String caption,
    @JsonProperty("parse_mode") String parseMode,
    @JsonProperty("caption_entities") List<MessageEntity> captionEntities,
    @JsonProperty("duration") Integer duration,
    @JsonProperty("performer") String performer,
    @JsonProperty("title") String title,
    @JsonProperty("thumbnail") InputFile thumbnail,
    @JsonIgnore InputFile mediaFile
) implements InputMedia {

    @Override public String getType() { return "audio"; }
    @Override public String getMedia() { return media; }
    @Override public String getCaption() { return caption; }
    @Override public String getParseMode() { return parseMode; }
    @Override public List<MessageEntity> getCaptionEntities() { return captionEntities; }
    @Override public InputFile getMediaFile() { return mediaFile; }
    @Override public InputFile getThumbnail() { return thumbnail; }

    @Override
    public void validate() throws TelegramApiValidationException {
        if ((media == null || media.isEmpty()) && mediaFile == null) {
            throw new TelegramApiValidationException("Media required", "InputMediaAudio");
        }
    }

    @Override
    public InputMedia withMedia(String newMediaString) {
        return this.toBuilder().media(newMediaString).build();
    }
}