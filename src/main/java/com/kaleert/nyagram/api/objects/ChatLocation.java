package com.kaleert.nyagram.api.objects;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.kaleert.nyagram.api.meta.BotApiObject;

/**
 * Представляет местоположение, к которому привязан чат.
 * Обычно используется для супергрупп и каналов, связанных с геолокацией.
 *
 * @param location Местоположение.
 * @param address Текстовый адрес.
 *
 * @since 1.0.0
 */
@JsonIgnoreProperties(ignoreUnknown = true)
public record ChatLocation(
    @JsonProperty("location") Location location,
    @JsonProperty("address") String address
) implements BotApiObject {}