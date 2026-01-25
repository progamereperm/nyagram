package com.kaleert.nyagram.api.objects.system;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.kaleert.nyagram.api.meta.BotApiObject;
import com.kaleert.nyagram.api.objects.chat.Chat;

/**
 * Представляет пересланную историю (Story).
 *
 * @param chat Чат, который опубликовал историю.
 * @param id Уникальный идентификатор истории в чате.
 *
 * @since 1.0.0
 */
@JsonIgnoreProperties(ignoreUnknown = true)
public record Story(
    @JsonProperty("chat") Chat chat,
    @JsonProperty("id") Integer id
) implements BotApiObject {}