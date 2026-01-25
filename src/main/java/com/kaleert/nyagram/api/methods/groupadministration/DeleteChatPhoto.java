package com.kaleert.nyagram.api.methods.groupadministration;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.kaleert.nyagram.api.exception.TelegramApiValidationException;
import com.kaleert.nyagram.api.meta.BotApiMethodBoolean;
import lombok.*;

/**
 * Используйте этот метод для удаления фотографии чата.
 * <p>
 * Фотографии удаляются только для групп, супергрупп и каналов.
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
public class DeleteChatPhoto extends BotApiMethodBoolean {
    
    /** Имя метода в Telegram Bot API. */
    public static final String PATH = "deleteChatPhoto";
    
    /**
     * Уникальный идентификатор чата или username канала.
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
     * Создает запрос на удаление фото чата.
     *
     * @param chatId ID чата (Long).
     * @return готовый объект запроса.
     */
    public static DeleteChatPhoto of(Long chatId) {
        return DeleteChatPhoto.builder()
                .chatId(chatId.toString())
                .build();
    }

    /**
     * Создает запрос на удаление фото чата.
     *
     * @param chatId ID чата или username канала.
     * @return готовый объект запроса.
     */
    public static DeleteChatPhoto of(String chatId) {
        return DeleteChatPhoto.builder()
                .chatId(chatId)
                .build();
    }
}