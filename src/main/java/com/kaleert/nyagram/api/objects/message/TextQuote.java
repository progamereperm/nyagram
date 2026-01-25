package com.kaleert.nyagram.api.objects.message;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.kaleert.nyagram.api.meta.BotApiObject;
import java.util.List;

/**
 * Содержит информацию о цитируемой части сообщения, на которое дается ответ.
 *
 * @param text Текст цитаты.
 * @param entities Специальные сущности в цитате.
 * @param position Приблизительная позиция цитаты в оригинальном сообщении (в символах).
 * @param isManual True, если цитата была выбрана пользователем вручную.
 *
 * @since 1.0.0
 */
@JsonIgnoreProperties(ignoreUnknown = true)
public record TextQuote(
    @JsonProperty("text") String text,
    @JsonProperty("entities") List<MessageEntity> entities,
    @JsonProperty("position") Integer position,
    @JsonProperty("is_manual") Boolean isManual
) implements BotApiObject {}