package com.kaleert.nyagram.api.methods.groupadministration;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.kaleert.nyagram.api.exception.TelegramApiValidationException;
import com.kaleert.nyagram.api.meta.BotApiMethodBoolean;
import lombok.*;

/**
 * Используйте этот метод для изменения названия чата.
 * <p>
 * Названия меняются только для групп, супергрупп и каналов.
 * Бот должен иметь право {@code can_change_info}.
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
public class SetChatTitle extends BotApiMethodBoolean {
    
    /** Имя метода в Telegram Bot API. */
    public static final String PATH = "setChatTitle";

    /**
     * Уникальный идентификатор чата.
     */
    @JsonProperty("chat_id")
    private String chatId;

    /**
     * Новое название чата (1-128 символов).
     */
    @JsonProperty("title")
    private String title;

    @Override
    public String getMethod() {
        return PATH;
    }

    @Override
    public void validate() throws TelegramApiValidationException {
        if (chatId == null || chatId.isEmpty()) {
            throw new TelegramApiValidationException("ChatId cannot be empty", PATH, "chat_id");
        }
        if (title == null || title.isEmpty()) {
            throw new TelegramApiValidationException("Title cannot be empty", PATH, "title");
        }
        if (title.length() > 128) {
            throw new TelegramApiValidationException("Title cannot exceed 128 characters", PATH, "title");
        }
    }
    
    /**
     * Создает запрос на изменение названия чата.
     *
     * @param chatId ID чата (Long).
     * @param title Новое название.
     * @return готовый объект запроса.
     */
    public static SetChatTitle of(Long chatId, String title) {
        return SetChatTitle.builder()
                .chatId(chatId.toString())
                .title(title)
                .build();
    }
    
    /**
     * Создает запрос на изменение названия чата.
     *
     * @param chatId ID чата или username канала.
     * @param title Новое название.
     * @return готовый объект запроса.
     */
    public static SetChatTitle of(String chatId, String title) {
        return SetChatTitle.builder()
                .chatId(chatId)
                .title(title)
                .build();
    }
}