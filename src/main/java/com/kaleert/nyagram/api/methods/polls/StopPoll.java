package com.kaleert.nyagram.api.methods.polls;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.kaleert.nyagram.api.exception.TelegramApiRequestException;
import com.kaleert.nyagram.api.exception.TelegramApiValidationException;
import com.kaleert.nyagram.api.meta.BotApiMethod;
import com.kaleert.nyagram.api.objects.polls.Poll;
import com.kaleert.nyagram.api.objects.replykeyboard.InlineKeyboardMarkup;
import lombok.*;

/**
 * Используйте этот метод для остановки опроса, отправленного ботом.
 * <p>
 * При остановке опрос закрывается, и пользователям показываются финальные результаты.
 * Возвращает остановленный объект {@link Poll}.
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
public class StopPoll extends BotApiMethod<Poll> {
    
    /** Имя метода в Telegram Bot API. */
    public static final String PATH = "stopPoll";

    /**
     * Уникальный идентификатор чата.
     */
    @JsonProperty("chat_id")
    private String chatId;

    /**
     * Идентификатор сообщения с опросом.
     */
    @JsonProperty("message_id")
    private Integer messageId;

    /**
     * Новая Inline-клавиатура (если нужно заменить).
     */
    @JsonProperty("reply_markup")
    private InlineKeyboardMarkup replyMarkup;

    @Override
    public String getMethod() {
        return PATH;
    }

    @Override
    public Poll deserializeResponse(String answer) throws TelegramApiRequestException {
        return deserializeResponse(answer, Poll.class);
    }

    @Override
    public void validate() throws TelegramApiValidationException {
        if (chatId == null) throw new TelegramApiValidationException("ChatId обязателен", PATH, "chat_id");
        if (messageId == null) throw new TelegramApiValidationException("MessageId обязателен", PATH, "message_id");
    }
}