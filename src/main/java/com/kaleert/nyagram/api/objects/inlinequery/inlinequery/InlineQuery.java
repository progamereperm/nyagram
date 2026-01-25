package com.kaleert.nyagram.api.objects.inlinequery;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.kaleert.nyagram.api.meta.BotApiObject;
import com.kaleert.nyagram.api.objects.User;
import com.kaleert.nyagram.api.objects.Location;

/**
 * Представляет входящий Inline-запрос.
 * <p>
 * Генерируется, когда пользователь вводит имя бота в поле ввода текста в любом чате.
 * </p>
 *
 * @param id Уникальный идентификатор запроса.
 * @param from Пользователь, отправивший запрос.
 * @param query Текст запроса (до 256 символов).
 * @param offset Смещение для пагинации (пустое для первого запроса).
 * @param chatType Тип чата, откуда пришел запрос: "sender" (личный), "private", "group", "supergroup", "channel".
 * @param location Местоположение пользователя (если бот запрашивает геоданные в inline-режиме).
 * 
 * @since 1.0.0
 */
@JsonIgnoreProperties(ignoreUnknown = true)
public record InlineQuery(
    @JsonProperty("id") String id,
    @JsonProperty("from") User from,
    @JsonProperty("query") String query,
    @JsonProperty("offset") String offset,
    @JsonProperty("chat_type") String chatType,
    @JsonProperty("location") Location location
) implements BotApiObject {
}