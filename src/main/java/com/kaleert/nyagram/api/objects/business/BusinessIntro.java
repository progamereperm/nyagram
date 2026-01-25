package com.kaleert.nyagram.api.objects.business;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.kaleert.nyagram.api.meta.BotApiObject;

/**
 * Содержит информацию о приветствии бизнес-аккаунта.
 *
 * @param title Заголовок приветствия.
 * @param message Текст сообщения приветствия.
 * @param sticker Стикер приветствия (если есть).
 *
 * @since 1.0.0
 */
@JsonIgnoreProperties(ignoreUnknown = true)
public record BusinessIntro(
    @JsonProperty("title") String title,
    @JsonProperty("message") String message,
    @JsonProperty("sticker") Object sticker
) implements BotApiObject {}