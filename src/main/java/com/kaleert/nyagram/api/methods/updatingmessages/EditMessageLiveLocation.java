package com.kaleert.nyagram.api.methods.updatingmessages;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.kaleert.nyagram.api.exception.TelegramApiRequestException;
import com.kaleert.nyagram.api.exception.TelegramApiValidationException;
import com.kaleert.nyagram.api.meta.BotApiMethod;
import com.kaleert.nyagram.api.objects.message.Message;
import lombok.*;

import java.io.Serializable;

/**
 * Используйте этот метод для редактирования сообщений с геопозицией в реальном времени.
 * <p>
 * Новая локация не может быть дальше чем 100 км от предыдущей.
 * </p>
 *
 * @since 1.0.0
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode(callSuper = false)
@JsonIgnoreProperties(ignoreUnknown = true)
@JsonInclude(JsonInclude.Include.NON_NULL)
public class EditMessageLiveLocation extends BotApiMethod<Serializable> {
    
    /** Имя метода в Telegram Bot API. */
    public static final String PATH = "editMessageLiveLocation";

    /**
     * Идентификатор чата (если inline_message_id не указан).
     */
    @JsonProperty("chat_id")
    private String chatId;

    /**
     * Идентификатор сообщения (если inline_message_id не указан).
     */
    @JsonProperty("message_id")
    private Integer messageId;

    /**
     * Идентификатор inline-сообщения (если chat_id и message_id не указаны).
     */
    @JsonProperty("inline_message_id")
    private String inlineMessageId;

    /**
     * Широта.
     */
    @JsonProperty("latitude")
    private Double latitude;

    /**
     * Долгота.
     */
    @JsonProperty("longitude")
    private Double longitude;

    @Override
    public String getMethod() {
        return PATH;
    }

    @Override
    public Serializable deserializeResponse(String answer) throws TelegramApiRequestException {
        try {
            return deserializeResponse(answer, Message.class);
        } catch (TelegramApiRequestException e) {
            return deserializeResponse(answer, Boolean.class);
        }
    }

    @Override
    public void validate() throws TelegramApiValidationException {
        if (latitude == null || longitude == null) {
            throw new TelegramApiValidationException("Coordinates required", PATH);
        }
    }
}