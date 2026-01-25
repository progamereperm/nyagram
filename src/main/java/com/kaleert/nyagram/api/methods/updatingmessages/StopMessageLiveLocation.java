package com.kaleert.nyagram.api.methods.updatingmessages;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.kaleert.nyagram.api.exception.TelegramApiRequestException;
import com.kaleert.nyagram.api.exception.TelegramApiValidationException;
import com.kaleert.nyagram.api.meta.BotApiMethod;
import com.kaleert.nyagram.api.objects.message.Message;
import com.kaleert.nyagram.api.objects.replykeyboard.InlineKeyboardMarkup;
import lombok.*;
import java.io.Serializable;

/**
 * Используйте этот метод для остановки трансляции геопозиции до истечения срока {@code live_period}.
 * <p>
 * После остановки сообщение станет обычным сообщением с локацией.
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
public class StopMessageLiveLocation extends BotApiMethod<Serializable> {
    
    /** Имя метода в Telegram Bot API. */
    public static final String PATH = "stopMessageLiveLocation";

    /**
     * Идентификатор чата.
     */
    @JsonProperty("chat_id")
    private String chatId;

    /**
     * Идентификатор сообщения.
     */
    @JsonProperty("message_id")
    private Integer messageId;
    
    /**
     * Идентификатор inline-сообщения.
     */
    @JsonProperty("inline_message_id")
    private String inlineMessageId;
    
    /**
     * Новая клавиатура.
     */
    @JsonProperty("reply_markup")
    private InlineKeyboardMarkup replyMarkup;

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
        if (chatId == null && inlineMessageId == null) {
             throw new TelegramApiValidationException("ChatId+MessageId OR InlineMessageId required", PATH);
        }
    }
}