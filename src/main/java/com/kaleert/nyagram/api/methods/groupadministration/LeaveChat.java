package com.kaleert.nyagram.api.methods.groupadministration;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.kaleert.nyagram.api.exception.TelegramApiValidationException;
import com.kaleert.nyagram.api.meta.BotApiMethodBoolean;
import lombok.*;

/**
 * Используйте этот метод, чтобы бот покинул группу, супергруппу или канал.
 * <p>
 * Возвращает {@code True} при успехе.
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
public class LeaveChat extends BotApiMethodBoolean {
    
    /** Имя метода в Telegram Bot API. */
    public static final String PATH = "leaveChat";
    
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
            throw new TelegramApiValidationException("ChatId cannot be empty", PATH, "chat_id");
        }
    }
    
    /**
     * Создает запрос на получение количества участников.
     *
     * @param chatId ID чата (Long).
     * @return готовый объект запроса.
     */
    public static LeaveChat of(Long chatId) {
        return LeaveChat.builder().chatId(chatId.toString()).build();
    }
    
    /**
     * Создает запрос на получение количества участников.
     *
     * @param chatId ID чата или username канала.
     * @return готовый объект запроса.
     */
    public static LeaveChat of(String chatId) {
        return LeaveChat.builder().chatId(chatId).build();
    }
}