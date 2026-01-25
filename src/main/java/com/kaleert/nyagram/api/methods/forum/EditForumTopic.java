package com.kaleert.nyagram.api.methods.forum;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.kaleert.nyagram.api.exception.TelegramApiValidationException;
import com.kaleert.nyagram.api.meta.BotApiMethodBoolean;
import lombok.*;

/**
 * Используйте этот метод для редактирования названия и иконки топика.
 * <p>
 * Бот должен иметь право {@code can_manage_topics}.
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
public class EditForumTopic extends BotApiMethodBoolean {

    /** Имя метода в Telegram Bot API. */
    public static final String PATH = "editForumTopic";
    
    /**
     * Уникальный идентификатор чата.
     */
    @JsonProperty("chat_id")
    private String chatId;
    
    /**
     * Уникальный идентификатор топика.
     */
    @JsonProperty("message_thread_id")
    private Integer messageThreadId;
    
    /**
     * Новое название топика (0-128 символов).
     * Если не указано, название не изменится.
     */
    @JsonProperty("name")
    private String name;
    
    /**
     * Новый ID кастомного эмодзи для иконки.
     * Передайте пустую строку, чтобы удалить иконку.
     */
    @JsonProperty("icon_custom_emoji_id")
    private String iconCustomEmojiId;

    @Override
    public String getMethod() {
        return PATH;
    }

    @Override
    public void validate() throws TelegramApiValidationException {
        if (chatId == null || chatId.isEmpty()) {
            throw new TelegramApiValidationException("ChatId cannot be empty", PATH, "chat_id");
        }
        if (messageThreadId == null) {
            throw new TelegramApiValidationException("MessageThreadId cannot be empty", PATH, "message_thread_id");
        }
        if (name != null && name.length() > 128) {
             throw new TelegramApiValidationException("Name length must be <= 128 chars", PATH, "name");
        }
    }
    
    /**
     * Создает запрос на переименование топика.
     *
     * @param chatId ID чата.
     * @param messageThreadId ID топика.
     * @param newName Новое название.
     * @return готовый объект запроса.
     */
    public static EditForumTopic rename(Long chatId, Integer messageThreadId, String newName) {
        return EditForumTopic.builder()
                .chatId(chatId.toString())
                .messageThreadId(messageThreadId)
                .name(newName)
                .build();
    }
}