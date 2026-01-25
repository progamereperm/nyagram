package com.kaleert.nyagram.api.objects.media;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.kaleert.nyagram.api.exception.TelegramApiValidationException;
import com.kaleert.nyagram.api.objects.InputFile;
import com.kaleert.nyagram.api.objects.message.MessageEntity;
import lombok.Builder;

import java.util.List;

/**
 * Представляет видеофайл, который будет отправлен как часть альбома.
 *
 * @param media Файл для отправки.
 * @param caption Подпись к видео.
 * @param parseMode Режим парсинга.
 * @param captionEntities Сущности в подписи.
 * @param width Ширина видео.
 * @param height Высота видео.
 * @param duration Длительность.
 * @param supportsStreaming Поддержка потокового воспроизведения.
 * @param hasSpoiler Эффект спойлера.
 * @param thumbnail Обложка видео.
 * @param mediaFile Локальный файл.
 *
 * @since 1.0.0
 */
@Builder(toBuilder = true)
public record InputMediaVideo(
    @JsonProperty("media") String media,
    @JsonProperty("caption") String caption,
    @JsonProperty("parse_mode") String parseMode,
    @JsonProperty("caption_entities") List<MessageEntity> captionEntities,
    @JsonProperty("width") Integer width,
    @JsonProperty("height") Integer height,
    @JsonProperty("duration") Integer duration,
    @JsonProperty("supports_streaming") Boolean supportsStreaming,
    @JsonProperty("has_spoiler") Boolean hasSpoiler,
    @JsonProperty("thumbnail") InputFile thumbnail,  
    @JsonIgnore InputFile mediaFile
    
) implements InputMedia {

    @Override
    public String getType() {
        return "video";
    }
    
    @Override public String getMedia() { return media; }
    @Override public String getCaption() { return caption; }
    @Override public String getParseMode() { return parseMode; }
    @Override public List<MessageEntity> getCaptionEntities() { return captionEntities; }
    @Override public InputFile getMediaFile() { return mediaFile; }
    @Override public InputFile getThumbnail() { return thumbnail; }

    @Override
    public void validate() throws TelegramApiValidationException {
        if ((media == null || media.isEmpty()) && mediaFile == null) {
            throw new TelegramApiValidationException("Media must be provided", "InputMediaVideo");
        }
    }

    @Override
    public InputMediaVideo withMedia(String newMediaString) {
        return this.toBuilder().media(newMediaString).build();
    }
    
    /**
     * Создает копию объекта с установленным превью (обложкой).
     *
     * @param newThumbnail Файл обложки.
     * @return новый экземпляр InputMediaVideo.
     */
    public InputMediaVideo withThumbnail(InputFile newThumbnail) {
        return this.toBuilder().thumbnail(newThumbnail).build();
    }
}