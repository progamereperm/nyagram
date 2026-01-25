package com.kaleert.nyagram.api.objects.media;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonSubTypes;
import com.fasterxml.jackson.annotation.JsonTypeInfo;
import com.kaleert.nyagram.api.meta.BotApiObject;
import com.kaleert.nyagram.api.meta.Validable;
import com.kaleert.nyagram.api.objects.InputFile;

/**
 * Базовый интерфейс для платных медиафайлов (Paid Media).
 * <p>
 * Используется в методе {@code sendPaidMedia}.
 * </p>
 *
 * @since 1.0.0
 */
@JsonTypeInfo(use = JsonTypeInfo.Id.NAME, property = "type")
@JsonSubTypes({
    @JsonSubTypes.Type(value = InputPaidMediaPhoto.class, name = "photo"),
    @JsonSubTypes.Type(value = InputPaidMediaVideo.class, name = "video")
})
@JsonInclude(JsonInclude.Include.NON_NULL)
public sealed interface InputPaidMedia extends BotApiObject, Validable 
    permits InputPaidMediaPhoto, InputPaidMediaVideo {
    
    String getType();
    String getMedia();
    InputFile getMediaFile();
    InputPaidMedia withMedia(String media);
}