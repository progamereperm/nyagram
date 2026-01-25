package com.kaleert.nyagram.api.methods.forum;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.kaleert.nyagram.api.exception.TelegramApiValidationException;
import com.kaleert.nyagram.api.meta.BotApiMethodBoolean;
import lombok.*;

/**
 * Используйте этот метод для открепления всех закрепленных сообщений в конкретном топике.
 * <p>
 * Бот должен иметь право {@code can_pin_messages}.
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
public class UnpinAllForumTopicMessages extends BotApiMethodBoolean {

    /** Имя метода в Telegram Bot API. */
    public static final String PATH = "unpinAllForumTopicMessages";
    
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

    @Override
    public String getMethod() {
        return PATH;
    }

    @Override
    public void validate() throws TelegramApiValidationException {
        if (chatId == null || chatId.isEmpty()) throw new TelegramApiValidationException("ChatId required", PATH);
        if (messageThreadId == null) throw new TelegramApiValidationException("ThreadId required", PATH);
    }
    
    /**
     * Создает запрос на открепление всех сообщений в топике.
     *
     * @param chatId ID чата.
     * @param messageThreadId ID топика.
     * @return готовый объект запроса.
     */
    public static UnpinAllForumTopicMessages of(Long chatId, Integer messageThreadId) {
        return UnpinAllForumTopicMessages.builder()
                .chatId(chatId.toString())
                .messageThreadId(messageThreadId)
                .build();
    }
}