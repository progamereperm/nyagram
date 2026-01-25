package com.kaleert.nyagram.api.methods.groupadministration;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.kaleert.nyagram.api.exception.TelegramApiValidationException;
import com.kaleert.nyagram.api.meta.BotApiMethodBoolean;
import lombok.*;

/**
 * Используйте этот метод для установки кастомного титула (должности) администратора
 * в супергруппе, продвигаемой ботом.
 * <p>
 * Бот должен быть администратором с правом {@code can_promote_members}.
 * </p>
 *
 * @since 1.0.0
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode(callSuper = false)
public class SetChatAdministratorCustomTitle extends BotApiMethodBoolean {
        
    /** Имя метода в Telegram Bot API. */
    public static final String PATH = "setChatAdministratorCustomTitle";
    
    /**
     * Уникальный идентификатор чата.
     */
    @JsonProperty("chat_id")
    private String chatId;
    
    /**
     * Уникальный идентификатор пользователя.
     */
    @JsonProperty("user_id")
    private Long userId;
    
    /**
     * Новый кастомный титул (0-16 символов). Эмодзи не поддерживаются.
     */
    @JsonProperty("custom_title")
    private String customTitle;

    @Override public String getMethod() { return PATH; }

    @Override
    public void validate() throws TelegramApiValidationException {
        if (chatId == null) throw new TelegramApiValidationException("ChatId required", PATH);
        if (userId == null) throw new TelegramApiValidationException("UserId required", PATH);
        if (customTitle != null && customTitle.length() > 16) 
            throw new TelegramApiValidationException("Title too long (max 16)", PATH);
    }
}