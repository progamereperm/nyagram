package com.kaleert.nyagram.api.methods.pinnedmessages;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.kaleert.nyagram.api.exception.TelegramApiValidationException;
import com.kaleert.nyagram.api.meta.BotApiMethodBoolean;
import lombok.*;

/**
 * Используйте этот метод для открепления конкретного сообщения.
 * <p>
 * Если {@code message_id} не указан, будет откреплено самое последнее закрепленное сообщение.
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
public class UnpinChatMessage extends BotApiMethodBoolean {
    
    /** Имя метода в Telegram Bot API. */
    public static final String PATH = "unpinChatMessage";

    /**
     * Уникальный идентификатор чата.
     */
    @JsonProperty("chat_id")
    private String chatId;

    /**
     * Идентификатор сообщения для открепления. Если не указан, открепляется последнее.
     */
    @JsonProperty("message_id")
    private Integer messageId;

    /**
     * Идентификатор бизнес-соединения.
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
    }
    
    /**
     * Устанавливает уникальный идентификатор чата.
     *
     * @param chatId ID чата.
     */
    public void setChatId(Long chatId) {
        this.chatId = chatId.toString();
    }
    
    /**
     * Создает запрос на открепление конкретного сообщения.
     *
     * @param chatId ID чата (Long).
     * @param messageId ID сообщения.
     * @return готовый объект запроса.
     */
    public static UnpinChatMessage of(Long chatId, Integer messageId) {
        return UnpinChatMessage.builder()
                .chatId(chatId.toString())
                .messageId(messageId)
                .build();
    }
    
    /**
     * Создает запрос на открепление последнего закрепленного сообщения.
     *
     * @param chatId ID чата (Long).
     * @return готовый объект запроса.
     */
    public static UnpinChatMessage latest(Long chatId) {
        return UnpinChatMessage.builder()
                .chatId(chatId.toString())
                .build();
    }
    
    /**
     * Создает запрос на открепление последнего закрепленного сообщения.
     *
     * @param chatId ID чата или username канала.
     * @return готовый объект запроса.
     */
    public static UnpinChatMessage latest(String chatId) {
        return UnpinChatMessage.builder()
                .chatId(chatId)
                .build();
    }
}