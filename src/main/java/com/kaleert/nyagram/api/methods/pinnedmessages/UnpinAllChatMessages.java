package com.kaleert.nyagram.api.methods.pinnedmessages;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.kaleert.nyagram.api.exception.TelegramApiValidationException;
import com.kaleert.nyagram.api.meta.BotApiMethodBoolean;
import lombok.*;

/**
 * Используйте этот метод, чтобы открепить ВСЕ сообщения в чате.
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
public class UnpinAllChatMessages extends BotApiMethodBoolean {
    
    /** Имя метода в Telegram Bot API. */
    public static final String PATH = "unpinAllChatMessages";

    /**
     * Уникальный идентификатор чата.
     */
    @JsonProperty("chat_id")
    private String chatId;

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
     * Создает запрос на открепление всех сообщений в чате.
     *
     * @param chatId ID чата (Long).
     * @return готовый объект запроса.
     */
    public static UnpinAllChatMessages of(Long chatId) {
        return UnpinAllChatMessages.builder().chatId(chatId.toString()).build();
    }
    
    /**
     * Создает запрос на открепление всех сообщений в чате.
     *
     * @param chatId ID чата или username канала.
     * @return готовый объект запроса.
     */
    public static UnpinAllChatMessages of(String chatId) {
        return UnpinAllChatMessages.builder().chatId(chatId).build();
    }
}