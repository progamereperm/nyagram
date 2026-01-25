package com.kaleert.nyagram.api.objects.games;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.kaleert.nyagram.api.meta.BotApiObject;
import com.kaleert.nyagram.api.objects.PhotoSize;
import com.kaleert.nyagram.api.objects.message.MessageEntity;
import com.kaleert.nyagram.api.objects.Animation;

import java.util.List;

/**
 * Представляет игру.
 * <p>
 * Используйте BotFather для создания и настройки игр.
 * </p>
 *
 * @param title Название игры.
 * @param description Описание игры.
 * @param photo Фотография, которая будет отображаться в чате.
 * @param text Краткое описание или инструкции (если есть).
 * @param textEntities Специальные сущности в тексте (ссылки, жирный шрифт и т.д.).
 * @param animation Анимация, которая будет отображаться в чате (опционально).
 *
 * @since 1.0.0
 */
@JsonIgnoreProperties(ignoreUnknown = true)
public record Game(
    @JsonProperty("title") String title,
    @JsonProperty("description") String description,
    @JsonProperty("photo") List<PhotoSize> photo,
    @JsonProperty("text") String text,
    @JsonProperty("text_entities") List<MessageEntity> textEntities,
    @JsonProperty("animation") com.kaleert.nyagram.api.objects.Animation animation
) implements BotApiObject {}