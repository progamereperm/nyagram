package com.kaleert.nyagram.api.objects.media;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.kaleert.nyagram.api.exception.TelegramApiValidationException;
import com.kaleert.nyagram.api.objects.InputFile;
import com.kaleert.nyagram.api.objects.message.MessageEntity;
import lombok.Builder;

import java.util.List;

/**
 * Представляет файл общего назначения, который будет отправлен как часть альбома.
 *
 * @param media Файл для отправки (file_id, URL или attach://).
 * @param caption Подпись к документу.
 * @param parseMode Режим парсинга подписи.
 * @param captionEntities Сущности в подписи.
 * @param disableContentTypeDetection Отключить автоматическое определение типа контента.
 * @param thumbnail Превью (обложка) документа.
 * @param mediaFile Локальный файл для загрузки.
 *
 * @since 1.0.0
 */
@Builder(toBuilder = true)
public record InputMediaDocument(
    @JsonProperty("media") String media,
    @JsonProperty("caption") String caption,
    @JsonProperty("parse_mode") String parseMode,
    @JsonProperty("caption_entities") List<MessageEntity> captionEntities,
    @JsonProperty("disable_content_type_detection") Boolean disableContentTypeDetection,
    @JsonProperty("thumbnail") InputFile thumbnail,
    @JsonIgnore InputFile mediaFile
) implements InputMedia {

    @Override public String getType() { return "document"; }
    @Override public String getMedia() { return media; }
    @Override public String getCaption() { return caption; }
    @Override public String getParseMode() { return parseMode; }
    @Override public List<MessageEntity> getCaptionEntities() { return captionEntities; }
    @Override public InputFile getMediaFile() { return mediaFile; }
    @Override public InputFile getThumbnail() { return thumbnail; }

    @Override
    public void validate() throws TelegramApiValidationException {
        if ((media == null || media.isEmpty()) && mediaFile == null) {
            throw new TelegramApiValidationException("Media required", "InputMediaDocument");
        }
    }

    @Override
    public InputMedia withMedia(String newMediaString) {
        return this.toBuilder().media(newMediaString).build();
    }
}