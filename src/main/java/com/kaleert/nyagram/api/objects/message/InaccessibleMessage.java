package com.kaleert.nyagram.api.objects.message;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Builder;
import com.kaleert.nyagram.api.objects.chat.Chat;

/**
 * Описывает сообщение, которое было удалено или недоступно боту по другим причинам.
 * <p>
 * Обычно встречается в объекте {@link MaybeInaccessibleMessage}, когда поле {@code date} равно 0.
 * </p>
 *
 * @param chat Чат, в котором было сообщение.
 * @param messageId ID сообщения.
 * @param date Всегда 0.
 *
 * @since 1.0.0
 */
@Builder
@JsonIgnoreProperties(ignoreUnknown = true)
public record InaccessibleMessage(
    @JsonProperty("chat") Chat chat,
    @JsonProperty("message_id") Long messageId,
    @JsonProperty("date") Integer date
) implements MaybeInaccessibleMessage {}