package com.kaleert.nyagram.api.objects.polls;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.kaleert.nyagram.api.meta.BotApiObject;

/**
 * Вариант ответа в существующем опросе.
 *
 * @param text Текст ответа.
 * @param voterCount Количество проголосовавших за этот вариант.
 *
 * @since 1.0.0
 */
@JsonIgnoreProperties(ignoreUnknown = true)
public record PollOption(
    @JsonProperty("text") String text,
    @JsonProperty("voter_count") Integer voterCount
) implements BotApiObject {}