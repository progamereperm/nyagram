package com.kaleert.nyagram.api.methods.groupadministration;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.kaleert.nyagram.api.exception.TelegramApiValidationException;
import com.kaleert.nyagram.api.meta.BotApiMethodBoolean;
import lombok.*;

/**
 * Используйте этот метод для изменения описания группы, супергруппы или канала.
 * <p>
 * Бот должен быть администратором с правом {@code can_change_info}.
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
public class SetChatDescription extends BotApiMethodBoolean {
    
    /** Имя метода в Telegram Bot API. */
    public static final String PATH = "setChatDescription";

    /**
     * Уникальный идентификатор чата или username канала.
     */
    @JsonProperty("chat_id")
    private String chatId;

    /**
     * Новое описание чата (0-255 символов).
     */
    @JsonProperty("description")
    private String description;

    @Override
    public String getMethod() {
        return PATH;
    }

    @Override
    public void validate() throws TelegramApiValidationException {
        if (chatId == null || chatId.isEmpty()) {
            throw new TelegramApiValidationException("ChatId cannot be empty", PATH, "chat_id");
        }
        if (description != null && description.length() > 255) {
            throw new TelegramApiValidationException("Description cannot exceed 255 characters", PATH, "description");
        }
    }
    
    
    /**
     * Создает запрос на изменение описания чата.
     *
     * @param chatId ID чата.
     * @param description Новое описание.
     * @return готовый объект запроса.
     */
    public static SetChatDescription of(Long chatId, String description) {
        return SetChatDescription.builder()
                .chatId(chatId.toString())
                .description(description)
                .build();
    }
    
    /**
     * Создает запрос на очистку описания чата (устанавливает пустую строку).
     *
     * @param chatId ID чата.
     * @return готовый объект запроса.
     */
    public static SetChatDescription clear(Long chatId) {
        return SetChatDescription.builder()
                .chatId(chatId.toString())
                .description("")
                .build();
    }
}