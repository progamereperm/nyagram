package com.kaleert.nyagram.api.objects.media;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.kaleert.nyagram.api.exception.TelegramApiValidationException;
import com.kaleert.nyagram.api.objects.InputFile;
import lombok.Builder;

/**
 * Представляет платную фотографию.
 *
 * @param media Фотография для отправки. Передайте file_id, URL или "attach://<file_attach_name>".
 * @param mediaFile Файл для загрузки (если используется multipart).
 *
 * @since 1.0.0
 */
@Builder
record InputPaidMediaPhoto(
    @JsonProperty("media") String media,
    @JsonIgnore InputFile mediaFile
) implements InputPaidMedia {
    @Override public String getType() { return "photo"; }
    @Override public String getMedia() { return media; }
    @Override public InputFile getMediaFile() { return mediaFile; }
    
    @Override 
    public InputPaidMedia withMedia(String media) {
        return new InputPaidMediaPhoto(media, this.mediaFile);
    }

    @Override public void validate() throws TelegramApiValidationException {
        if (media == null && mediaFile == null) throw new TelegramApiValidationException("Media required", "InputPaidMediaPhoto");
    }
}

/**
 * Представляет платное видео.
 *
 * @param media Видео для отправки. Передайте file_id, URL или "attach://<file_attach_name>".
 * @param thumbnail Обложка видео (опционально).
 * @param width Ширина видео.
 * @param height Высота видео.
 * @param duration Длительность видео в секундах.
 * @param supportsStreaming True, если видео подходит для потоковой передачи.
 * @param mediaFile Файл для загрузки (если используется multipart).
 *
 * @since 1.0.0
 */
@Builder
record InputPaidMediaVideo(
    @JsonProperty("media") String media,
    @JsonProperty("thumbnail") InputFile thumbnail,
    @JsonProperty("width") Integer width,
    @JsonProperty("height") Integer height,
    @JsonProperty("duration") Integer duration,
    @JsonProperty("supports_streaming") Boolean supportsStreaming,
    @JsonIgnore InputFile mediaFile
) implements InputPaidMedia {
    @Override public String getType() { return "video"; }
    @Override public String getMedia() { return media; }
    @Override public InputFile getMediaFile() { return mediaFile; }

    @Override
    public InputPaidMedia withMedia(String media) {
        return new InputPaidMediaVideo(media, this.thumbnail, this.width, this.height, this.duration, this.supportsStreaming, this.mediaFile);
    }

    @Override public void validate() throws TelegramApiValidationException {
        if (media == null && mediaFile == null) throw new TelegramApiValidationException("Media required", "InputPaidMediaVideo");
    }
}