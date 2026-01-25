package com.kaleert.nyagram.api.methods.send;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.kaleert.nyagram.api.exception.TelegramApiRequestException;
import com.kaleert.nyagram.api.exception.TelegramApiValidationException;
import com.kaleert.nyagram.api.meta.BotApiMethod;
import com.kaleert.nyagram.api.objects.message.Message;
import lombok.*;

/**
 * Используйте этот метод для пересылки сообщений (Forward) любого типа.
 * <p>
 * В случае успеха возвращает отправленное {@link Message}.
 * </p>
 *
 * @since 1.0.0
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode(callSuper = false)
public class ForwardMessage extends BotApiMethod<Message> {
        
    /** Имя метода в Telegram Bot API. */
    public static final String PATH = "forwardMessage";

    /**
     * Уникальный идентификатор чата, КУДА пересылается сообщение.
     */
    @JsonProperty("chat_id") private String chatId;
    
    /**
     * Уникальный идентификатор чата, ОТКУДА пересылается сообщение.
     */
    @JsonProperty("from_chat_id") private String fromChatId;
    
    /**
     * Идентификатор пересылаемого сообщения.
     */
    @JsonProperty("message_id") private Integer messageId;
    
    /**
     * Уникальный идентификатор топика (для форумов).
     */
    @JsonProperty("message_thread_id") private Integer messageThreadId;
    
    /**
     * Отключить уведомление.
     */
    @JsonProperty("disable_notification") private Boolean disableNotification;
    
    /**
     * Защитить контент от сохранения.
     */
    @JsonProperty("protect_content") private Boolean protectContent;

    @Override public String getMethod() { return PATH; }

    @Override
    public Message deserializeResponse(String answer) throws TelegramApiRequestException {
        return deserializeResponse(answer, Message.class);
    }

    @Override
    public void validate() throws TelegramApiValidationException {
        if (chatId == null) throw new TelegramApiValidationException("ChatId обязателен", PATH, "chat_id");
        if (fromChatId == null) throw new TelegramApiValidationException("FromChatId обязателен", PATH, "from_chat_id");
        if (messageId == null) throw new TelegramApiValidationException("MessageId обязателен", PATH, "message_id");
    }
    
    /**
     * Создает запрос на пересылку сообщения.
     *
     * @param toChatId ID чата, куда переслать сообщение.
     * @param fromChatId ID чата, откуда пересылается сообщение.
     * @param messageId ID пересылаемого сообщения.
     * @return готовый объект запроса.
     */
    public static ForwardMessage of(Long toChatId, Long fromChatId, Integer messageId) {
        return ForwardMessage.builder()
                .chatId(toChatId.toString())
                .fromChatId(fromChatId.toString())
                .messageId(messageId)
                .build();
    }
}