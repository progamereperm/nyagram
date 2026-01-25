package com.kaleert.nyagram.api.methods.groupadministration;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.kaleert.nyagram.api.exception.TelegramApiValidationException;
import com.kaleert.nyagram.api.meta.BotApiMethodBoolean;
import com.kaleert.nyagram.api.objects.InputFile;
import lombok.*;

import java.io.File;
import java.io.InputStream;

/**
 * Используйте этот метод для установки новой фотографии профиля для чата.
 * <p>
 * Фотографии устанавливаются только для групп, супергрупп и каналов.
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
public class SetChatPhoto extends BotApiMethodBoolean {
    
    /** Имя метода в Telegram Bot API. */
    public static final String PATH = "setChatPhoto";

    /**
     * Уникальный идентификатор чата.
     */
    @JsonProperty("chat_id")
    private String chatId;

    /**
     * Новая фотография чата.
     */
    @JsonProperty("photo")
    private InputFile photo;

    @Override
    public String getMethod() {
        return PATH;
    }

    @Override
    public void validate() throws TelegramApiValidationException {
        if (chatId == null || chatId.isEmpty()) {
            throw new TelegramApiValidationException("ChatId cannot be empty", PATH, "chat_id");
        }
        if (photo == null) {
            throw new TelegramApiValidationException("Photo cannot be null", PATH, "photo");
        }
        if (!photo.isNew()) {
             throw new TelegramApiValidationException("SetChatPhoto requires uploading a new file (not file_id)", PATH, "photo");
        }
        photo.validate();
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
     * Создает запрос на установку фото из локального файла.
     *
     * @param chatId ID чата.
     * @param file Файл с изображением.
     * @return готовый объект запроса.
     */
    public static SetChatPhoto withFile(Long chatId, File file) {
        return SetChatPhoto.builder()
                .chatId(chatId.toString())
                .photo(new InputFile(file))
                .build();
    }
        
    /**
     * Создает запрос на установку фото из потока данных (InputStream).
     *
     * @param chatId ID чата.
     * @param stream Поток с изображением.
     * @param fileName Имя файла (обязательно).
     * @return готовый объект запроса.
     */
    public static SetChatPhoto withStream(Long chatId, InputStream stream, String fileName) {
        return SetChatPhoto.builder()
                .chatId(chatId.toString())
                .photo(new InputFile(stream, fileName))
                .build();
    }
}