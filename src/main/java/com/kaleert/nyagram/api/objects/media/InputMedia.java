package com.kaleert.nyagram.api.objects.media;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonSubTypes;
import com.fasterxml.jackson.annotation.JsonTypeInfo;
import com.kaleert.nyagram.api.meta.BotApiObject;
import com.kaleert.nyagram.api.meta.Validable;
import com.kaleert.nyagram.api.objects.InputFile;
import com.kaleert.nyagram.api.objects.message.MessageEntity;

import java.util.List;

/**
 * Базовый интерфейс, представляющий содержимое медиа-сообщения для отправки в альбоме (Media Group).
 *
 * @see com.kaleert.nyagram.api.methods.send.SendMediaGroup
 * @since 1.0.0
 */
@JsonTypeInfo(
    use = JsonTypeInfo.Id.NAME,
    include = JsonTypeInfo.As.PROPERTY,
    property = "type"
)
@JsonSubTypes({
    @JsonSubTypes.Type(value = InputMediaPhoto.class, name = "photo"),
    @JsonSubTypes.Type(value = InputMediaVideo.class, name = "video"),
    @JsonSubTypes.Type(value = InputMediaAudio.class, name = "audio"),
    @JsonSubTypes.Type(value = InputMediaDocument.class, name = "document")
})
@JsonInclude(JsonInclude.Include.NON_NULL)
public sealed interface InputMedia extends BotApiObject, Validable 
    permits InputMediaPhoto, InputMediaVideo, InputMediaAudio, InputMediaDocument {

    String getType();
    String getMedia();
    String getCaption();
    String getParseMode();
    List<MessageEntity> getCaptionEntities();

    @JsonIgnore
    InputFile getMediaFile();
    
    @JsonIgnore
    InputFile getThumbnail();

    InputMedia withMedia(String newMediaString);
}