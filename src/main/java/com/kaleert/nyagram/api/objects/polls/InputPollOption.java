package com.kaleert.nyagram.api.objects.polls;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.kaleert.nyagram.api.meta.BotApiObject;
import com.kaleert.nyagram.api.objects.message.MessageEntity;
import java.util.List;

/**
 * Описывает вариант ответа при создании нового опроса.
 *
 * @param text Текст варианта ответа (1-100 символов).
 * @param textParseMode Режим парсинга для текста (HTML, MarkdownV2).
 * @param textEntities Список сущностей, если парсинг делается вручную.
 *
 * @see com.kaleert.nyagram.api.methods.polls.SendPoll
 * @since 1.0.0
 */
public record InputPollOption(
    @JsonProperty("text") String text,
    @JsonProperty("text_parse_mode") String textParseMode,
    @JsonProperty("text_entities") List<MessageEntity> textEntities
) implements BotApiObject {
    
    /**
     * Создает простой вариант ответа без форматирования.
     * @param text Текст ответа.
     * @return объект InputPollOption.
     */
    public static InputPollOption of(String text) {
        return new InputPollOption(text, null, null);
    }
}