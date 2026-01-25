package com.kaleert.nyagram.api.methods.groupadministration;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.kaleert.nyagram.api.exception.TelegramApiValidationException;
import com.kaleert.nyagram.api.meta.BotApiMethodBoolean;
import lombok.*;

/**
 * Используйте этот метод для удаления набора стикеров группы.
 * <p>
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
public class DeleteChatStickerSet extends BotApiMethodBoolean {
    
    /** Имя метода в Telegram Bot API. */
    public static final String PATH = "deleteChatStickerSet";
    
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
    public void validate() throws TelegramApiValidationException {
        if (chatId == null || chatId.isEmpty()) {
            throw new TelegramApiValidationException("ChatId cannot be empty", PATH, "chat_id");
        }
    }
    
    /**
     * Создает запрос на удаление набора стикеров группы.
     *
     * @param chatId ID чата.
     * @return готовый объект запроса.
     */
    public static DeleteChatStickerSet of(Long chatId) {
        return DeleteChatStickerSet.builder().chatId(chatId.toString()).build();
    }
}