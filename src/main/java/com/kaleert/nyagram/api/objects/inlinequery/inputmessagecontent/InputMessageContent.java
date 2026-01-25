package com.kaleert.nyagram.api.objects.inlinequery.inputmessagecontent;

import com.fasterxml.jackson.annotation.JsonSubTypes;
import com.fasterxml.jackson.annotation.JsonTypeInfo;
import com.kaleert.nyagram.api.meta.BotApiObject;
import com.kaleert.nyagram.api.meta.Validable;

/**
 * Интерфейс, представляющий содержимое сообщения, которое будет отправлено
 * при выборе пользователем результата inline-запроса.
 *
 * @since 1.0.0
 */
@JsonTypeInfo(use = JsonTypeInfo.Id.DEDUCTION)
@JsonSubTypes({
    @JsonSubTypes.Type(value = InputTextMessageContent.class),
    @JsonSubTypes.Type(value = InputLocationMessageContent.class)
})
public sealed interface InputMessageContent extends BotApiObject, Validable 
    permits InputTextMessageContent, InputLocationMessageContent {
}