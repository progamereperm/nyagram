package com.kaleert.nyagram.api.objects.webapp;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.kaleert.nyagram.api.meta.BotApiObject;

/**
 * Содержит данные, отправленные из Web App в бота.
 * <p>
 * Данные отправляются методом JavaScript {@code Telegram.WebApp.sendData}.
 * </p>
 *
 * @param data Данные. Убедитесь, что вы доверяете этим данным, так как они не подписаны.
 * @param buttonText Текст кнопки на клавиатуре, с которой было открыто Web App.
 *
 * @since 1.0.0
 */
@JsonIgnoreProperties(ignoreUnknown = true)
public record WebAppData(
    @JsonProperty("data") String data,
    @JsonProperty("button_text") String buttonText
) implements BotApiObject {}