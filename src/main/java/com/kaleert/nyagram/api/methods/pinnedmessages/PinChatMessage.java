package com.kaleert.nyagram.api.methods.pinnedmessages;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.kaleert.nyagram.api.exception.TelegramApiValidationException;
import com.kaleert.nyagram.api.meta.BotApiMethodBoolean;
import lombok.*;

/**
 * Используйте этот метод для закрепления сообщения в чате.
 * <p>
 * Если чат не является личным, бот должен быть администратором с правом {@code can_pin_messages}.
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
public class PinChatMessage extends BotApiMethodBoolean {
    
    /** Имя метода в Telegram Bot API. */
    public static final String PATH = "pinChatMessage";

    /**
     * Уникальный идентификатор чата или username канала.
     */
    @JsonProperty("chat_id")
    private String chatId;

    /**
     * Идентификатор сообщения, которое нужно закрепить.
     */
    @JsonProperty("message_id")
    private Integer messageId;

    /**
     * Передайте true, чтобы закрепить сообщение без оповещения участников чата (тихий закреп).
     */
    @JsonProperty("disable_notification")
    private Boolean disableNotification;

    /**
     * Уникальный идентификатор бизнес-соединения (для бизнес-ботов).
     */
    @JsonProperty("business_connection_id")
    private String businessConnectionId;

    @Override
    public String getMethod() {
        return PATH;
    }

    @Override
    public void validate() throws TelegramApiValidationException {
        if (chatId == null || chatId.isEmpty()) {
            throw new TelegramApiValidationException("ChatId обязателен", PATH, "chat_id");
        }
        if (messageId == null) {
            throw new TelegramApiValidationException("MessageId обязателен", PATH, "message_id");
        }
    }
    
    /**
     * Устанавливает ID чата из Long.
     *
     * @param chatId ID чата.
     */
    public void setChatId(Long chatId) {
        this.chatId = chatId.toString();
    }
    
    /**
     * Закрепляет сообщение в "тихом" режиме.
     * Пользователи не получат уведомления о закрепе (если у них включены уведомления чата).
     *
     * @return текущий билдер.
     */
    public PinChatMessage silent() {
        this.disableNotification = true;
        return this;
    }
    
    /**
     * Создает запрос на закрепление сообщения.
     *
     * @param chatId ID чата (Long).
     * @param messageId ID сообщения.
     * @return готовый объект запроса.
     */
    public static PinChatMessage of(Long chatId, Integer messageId) {
        return PinChatMessage.builder()
                .chatId(chatId.toString())
                .messageId(messageId)
                .build();
    }
    
    /**
     * Создает запрос на закрепление сообщения.
     *
     * @param chatId ID чата или username канала.
     * @param messageId ID сообщения.
     * @return готовый объект запроса.
     */
    public static PinChatMessage of(String chatId, Integer messageId) {
        return PinChatMessage.builder()
                .chatId(chatId)
                .messageId(messageId)
                .build();
    }
}