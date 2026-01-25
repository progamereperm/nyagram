package com.kaleert.nyagram.api.methods.forum;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.kaleert.nyagram.api.exception.TelegramApiValidationException;
import com.kaleert.nyagram.api.meta.BotApiMethodBoolean;
import lombok.*;

/**
 * Используйте этот метод для удаления топика форума вместе со всей историей сообщений.
 * <p>
 * Внимание: это действие необратимо. Бот должен иметь право {@code can_manage_topics}.
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
public class DeleteForumTopic extends BotApiMethodBoolean {

    /** Имя метода в Telegram Bot API. */
    public static final String PATH = "deleteForumTopic";

    /**
     * Уникальный идентификатор чата.
     */
    @JsonProperty("chat_id")
    private String chatId;

    /**
     * Уникальный идентификатор топика (Message Thread ID).
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
     * Создает запрос на удаление топика.
     *
     * @param chatId ID чата.
     * @param messageThreadId ID топика.
     * @return готовый объект запроса.
     */
    public static DeleteForumTopic of(Long chatId, Integer messageThreadId) {
        return DeleteForumTopic.builder()
                .chatId(chatId.toString())
                .messageThreadId(messageThreadId)
                .build();
    }
}