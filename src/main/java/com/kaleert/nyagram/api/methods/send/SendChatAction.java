package com.kaleert.nyagram.api.methods.send;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.kaleert.nyagram.api.exception.TelegramApiValidationException;
import com.kaleert.nyagram.api.meta.BotApiMethodBoolean;
import lombok.*;

/**
 * Используйте этот метод, чтобы сообщить пользователю, что на стороне бота что-то происходит.
 * <p>
 * Статус отображается в заголовке чата (например, "печатает...", "загружает фото...").
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
public class SendChatAction extends BotApiMethodBoolean {
    
    /** Имя метода в Telegram Bot API. */
    public static final String PATH = "sendChatAction";

    /**
     * Уникальный идентификатор чата.
     */
    @JsonProperty("chat_id")
    private String chatId;

    /**
     * Тип действия.
     * <p>
     * Примеры: "typing" (текст), "upload_photo" (фото), "record_video" (запись видео),
     * "upload_video" (загрузка видео), "record_voice" (запись голосового), "upload_voice" (загрузка голосового),
     * "upload_document" (файл), "choose_sticker" (стикер), "find_location" (локация).
     * </p>
     */
    @JsonProperty("action")
    private String action;

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
        if (chatId == null || chatId.isEmpty()) {
            throw new TelegramApiValidationException("ChatId обязателен", PATH, "chat_id");
        }
        if (action == null || action.isEmpty()) {
            throw new TelegramApiValidationException("Action обязателен", PATH, "action");
        }
    }
    
    /**
     * Создает действие "печатает..." (typing).
     * Показывает пользователю, что бот набирает текстовое сообщение.
     *
     * @param chatId ID чата.
     * @return готовый объект действия.
     */
    public static SendChatAction typing(Long chatId) {
        return new SendChatAction(chatId.toString(), "typing", null);
    }
    
    /**
     * Создает действие "отправляет фото..." (upload_photo).
     *
     * @param chatId ID чата.
     * @return готовый объект действия.
     */
    public static SendChatAction uploadPhoto(Long chatId) {
        return new SendChatAction(chatId.toString(), "upload_photo", null);
    }
}