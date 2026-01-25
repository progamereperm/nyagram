package com.kaleert.nyagram.api.methods.updatingmessages;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.kaleert.nyagram.api.exception.TelegramApiValidationException;
import com.kaleert.nyagram.api.meta.BotApiMethodBoolean;
import lombok.*;

/**
 * Используйте этот метод для удаления сообщения, включая служебные.
 * <p>
 * Ограничения:
 * - Сообщение можно удалить только если оно было отправлено менее 48 часов назад.
 * - Служебные сообщения о создании группы, супергруппы или канала нельзя удалить.
 * - Боты могут удалять исходящие сообщения в приватных чатах, группах и каналах.
 * </p>
 *
 * @since 1.0.0
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode(callSuper = false)
public class DeleteMessage extends BotApiMethodBoolean {
    
    /** Имя метода в Telegram Bot API. */
    public static final String PATH = "deleteMessage";

    /**
     * Уникальный идентификатор чата.
     */
    @JsonProperty("chat_id")
    private String chatId;

    /**
     * Идентификатор сообщения для удаления.
     */
    @JsonProperty("message_id")
    private Integer messageId;

    @Override
    public String getMethod() {
        return PATH;
    }

    @Override
    public void validate() throws TelegramApiValidationException {
        if (chatId == null || chatId.isEmpty()) {
            throw new TelegramApiValidationException("ChatId cannot be empty", PATH, "chat_id");
        }
        if (messageId == null) {
            throw new TelegramApiValidationException("MessageId cannot be null", PATH, "message_id");
        }
    }
    
    /**
     * Создает запрос на удаление сообщения.
     *
     * @param chatId ID чата (Long).
     * @param messageId ID сообщения.
     * @return готовый объект запроса.
     */
    public static DeleteMessage of(Long chatId, Integer messageId) {
        return new DeleteMessage(chatId.toString(), messageId);
    }
    
    /**
     * Создает запрос на удаление сообщения.
     *
     * @param chatId ID чата или username канала.
     * @param messageId ID сообщения.
     * @return готовый объект запроса.
     */
    public static DeleteMessage of(String chatId, Integer messageId) {
        return new DeleteMessage(chatId, messageId);
    }
}