package com.kaleert.nyagram.api.methods.groupadministration;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.kaleert.nyagram.api.exception.TelegramApiRequestException;
import com.kaleert.nyagram.api.exception.TelegramApiValidationException;
import com.kaleert.nyagram.api.meta.BotApiMethod;
import com.kaleert.nyagram.api.objects.chatmember.ChatMember;
import lombok.*;

/**
 * Используйте этот метод для получения информации об участнике чата.
 * <p>
 * Возвращает объект {@link ChatMember}, который содержит информацию о статусе пользователя
 * (администратор, участник, забанен и т.д.) и его правах.
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
public class GetChatMember extends BotApiMethod<ChatMember> {
    
    /** Имя метода в Telegram Bot API. */
    public static final String PATH = "getChatMember";

    /**
     * Уникальный идентификатор чата или username канала.
     */
    @JsonProperty("chat_id")
    private String chatId;

    /**
     * Уникальный идентификатор целевого пользователя.
     */
    @JsonProperty("user_id")
    private Long userId;

    @Override
    public String getMethod() {
        return PATH;
    }

    @Override
    public ChatMember deserializeResponse(String answer) throws TelegramApiRequestException {
        return deserializeResponse(answer, ChatMember.class);
    }

    @Override
    public void validate() throws TelegramApiValidationException {
        if (chatId == null || chatId.isEmpty()) {
            throw new TelegramApiValidationException("ChatId cannot be empty", PATH, "chat_id");
        }
        if (userId == null || userId == 0) {
            throw new TelegramApiValidationException("UserId cannot be empty", PATH, "user_id");
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
     * Создает запрос на получение информации об участнике.
     *
     * @param chatId ID чата или username канала.
     * @param userId ID пользователя.
     * @return готовый объект запроса.
     */
    public static GetChatMember of(String chatId, Long userId) {
        return GetChatMember.builder()
                .chatId(chatId)
                .userId(userId)
                .build();
    }
        
    /**
     * Создает запрос на получение информации об участнике.
     *
     * @param chatId ID чата.
     * @param userId ID пользователя.
     * @return готовый объект запроса.
     */
    public static GetChatMember of(Long chatId, Long userId) {
        return GetChatMember.builder()
                .chatId(chatId.toString())
                .userId(userId)
                .build();
    }
}