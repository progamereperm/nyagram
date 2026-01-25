package com.kaleert.nyagram.api.objects;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.kaleert.nyagram.api.meta.BotApiObject;

/**
 * Описывает дату рождения пользователя.
 *
 * @param day День рождения (1-31).
 * @param month Месяц рождения (1-12).
 * @param year Год рождения (опционально).
 * 
 * @since 1.0.0
 */
@JsonIgnoreProperties(ignoreUnknown = true)
public record Birthdate(
    @JsonProperty("day") Integer day,
    @JsonProperty("month") Integer month,
    @JsonProperty("year") Integer year
) implements BotApiObject {}