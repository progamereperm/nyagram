package com.kaleert.nyagram.api.methods.groupadministration;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.kaleert.nyagram.api.exception.TelegramApiValidationException;
import com.kaleert.nyagram.api.meta.BotApiMethodBoolean;
import lombok.*;

/**
 * Используйте этот метод для установки нового набора стикеров для супергруппы.
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
public class SetChatStickerSet extends BotApiMethodBoolean {
    
    /** Имя метода в Telegram Bot API. */
    public static final String PATH = "setChatStickerSet";

    /**
     * Уникальный идентификатор чата.
     */
    @JsonProperty("chat_id")
    private String chatId;

    /**
     * Название набора стикеров (short name), который нужно установить.
     */
    @JsonProperty("sticker_set_name")
    private String stickerSetName;

    @Override
    public String getMethod() {
        return PATH;
    }

    @Override
    public void validate() throws TelegramApiValidationException {
        if (chatId == null || chatId.isEmpty()) {
            throw new TelegramApiValidationException("ChatId cannot be empty", PATH, "chat_id");
        }
        if (stickerSetName == null || stickerSetName.isEmpty()) {
            throw new TelegramApiValidationException("StickerSetName cannot be empty", PATH, "sticker_set_name");
        }
    }
    
    /**
     * Создает запрос на установку набора стикеров для супергруппы.
     *
     * @param chatId ID чата.
     * @param stickerSetName Короткое имя набора стикеров.
     * @return готовый объект запроса.
     */
    public static SetChatStickerSet of(Long chatId, String stickerSetName) {
        return SetChatStickerSet.builder()
                .chatId(chatId.toString())
                .stickerSetName(stickerSetName)
                .build();
    }
}