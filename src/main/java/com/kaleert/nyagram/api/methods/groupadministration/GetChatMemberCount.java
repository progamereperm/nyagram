package com.kaleert.nyagram.api.methods.groupadministration;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.kaleert.nyagram.api.exception.TelegramApiRequestException;
import com.kaleert.nyagram.api.exception.TelegramApiValidationException;
import com.kaleert.nyagram.api.meta.BotApiMethod;
import lombok.*;

/**
 * Используйте этот метод для получения количества участников в чате.
 * <p>
 * Возвращает {@code Integer}.
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
public class GetChatMemberCount extends BotApiMethod<Integer> {
    
    /** Имя метода в Telegram Bot API. */
    public static final String PATH = "getChatMemberCount";

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
    public Integer deserializeResponse(String answer) throws TelegramApiRequestException {
        return deserializeResponse(answer, Integer.class);
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
    public static GetChatMemberCount of(Long chatId) {
        return new GetChatMemberCount(chatId.toString());
    }
    
    /**
     * Создает запрос на получение количества участников.
     *
     * @param chatId ID чата или username канала.
     * @return готовый объект запроса.
     */
    public static GetChatMemberCount of(String chatId) {
        return new GetChatMemberCount(chatId);
    }
}