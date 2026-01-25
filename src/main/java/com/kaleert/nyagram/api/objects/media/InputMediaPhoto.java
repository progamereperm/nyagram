package com.kaleert.nyagram.api.objects.media;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.kaleert.nyagram.api.exception.TelegramApiValidationException;
import com.kaleert.nyagram.api.objects.InputFile;
import com.kaleert.nyagram.api.objects.message.MessageEntity;
import lombok.Builder;

import java.util.List;

/**
 * Представляет фотографию, которая будет отправлена как часть альбома.
 *
 * @param media Файл для отправки.
 * @param caption Подпись к фото.
 * @param parseMode Режим парсинга.
 * @param captionEntities Сущности в подписи.
 * @param hasSpoiler Добавить эффект спойлера.
 * @param mediaFile Локальный файл.
 *
 * @since 1.0.0
 */
@Builder(toBuilder = true)
public record InputMediaPhoto(
    @JsonProperty("media") String media,
    @JsonProperty("caption") String caption,
    @JsonProperty("parse_mode") String parseMode,
    @JsonProperty("caption_entities") List<MessageEntity> captionEntities,
    @JsonProperty("has_spoiler") Boolean hasSpoiler, 
    @JsonIgnore InputFile mediaFile
) implements InputMedia {

    @Override
    public String getType() {
        return "photo";
    }

    @Override
    public String getMedia() {
        return media;
    }
    
    @Override
    public String getCaption() {
        return caption;
    }

    @Override
    public String getParseMode() {
        return parseMode;
    }

    @Override
    public List<MessageEntity> getCaptionEntities() {
        return captionEntities;
    }

    @Override
    public InputFile getMediaFile() {
        return mediaFile;
    }
    
    @Override
    public InputFile getThumbnail() {
        return null;
    }

    @Override
    public void validate() throws TelegramApiValidationException {
        if ((media == null || media.isEmpty()) && mediaFile == null) {
            throw new TelegramApiValidationException("Media must be provided (url, file_id or InputFile)", "InputMediaPhoto");
        }
    }

    @Override
    public InputMediaPhoto withMedia(String newMediaString) {
        return this.toBuilder().media(newMediaString).build();
    }
}