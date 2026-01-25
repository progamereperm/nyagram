package com.kaleert.nyagram.api.methods.send;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.kaleert.nyagram.api.exception.TelegramApiRequestException;
import com.kaleert.nyagram.api.exception.TelegramApiValidationException;
import com.kaleert.nyagram.api.meta.BotApiMethod;
import com.kaleert.nyagram.api.meta.MultipartRequest;
import com.kaleert.nyagram.api.methods.ParseMode;
import com.kaleert.nyagram.api.objects.InputFile;
import com.kaleert.nyagram.api.objects.message.MessageEntity;
import com.kaleert.nyagram.api.objects.message.Message;
import com.kaleert.nyagram.api.objects.replykeyboard.ReplyKeyboard;
import lombok.*;

import java.io.File;
import java.io.InputStream;
import java.util.List;
import java.util.Map;

/**
 * Используйте этот метод для отправки фотографий.
 * <p>
 * Фотографию можно отправить тремя способами:
 * 1. Через file_id (если файл уже загружен на сервера Telegram).
 * 2. Через URL (Telegram скачает его сам).
 * 3. Загрузкой нового файла (Multipart upload) через объект {@link InputFile}.
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
public class SendPhoto extends BotApiMethod<Message> implements MultipartRequest {
    
    /** Имя метода в Telegram Bot API. */
    public static final String PATH = "sendPhoto";
    
    /**
     * Уникальный идентификатор чата.
     */
    @JsonProperty("chat_id")
    private String chatId;

    @JsonProperty("message_thread_id")
    private Integer messageThreadId;
    
    /**
     * Фотография для отправки.
     * Передайте file_id как строку или используйте {@link InputFile} для загрузки файла.
     */
    @JsonProperty("photo")
    private InputFile photo;
    
    /**
     * Подпись к фотографии (0-1024 символов).
     */
    @JsonProperty("caption")
    private String caption;
    
    /**
     * Режим парсинга сущностей в подписи (HTML/Markdown).
     */
    @JsonProperty("parse_mode")
    private String parseMode;

    @JsonProperty("caption_entities")
    private List<MessageEntity> captionEntities;
    
    /**
     * Добавляет "спойлер" (эффект мерцания) на фото.
     */
    @JsonProperty("has_spoiler")
    private Boolean hasSpoiler;

    @JsonProperty("disable_notification")
    private Boolean disableNotification;

    @JsonProperty("protect_content")
    private Boolean protectContent;

    @JsonProperty("reply_to_message_id")
    private Integer replyToMessageId;

    @JsonProperty("allow_sending_without_reply")
    private Boolean allowSendingWithoutReply;

    /**
     * Клавиатура.
     */
    @JsonProperty("reply_markup")
    private ReplyKeyboard replyMarkup;

    @Override
    public String getMethod() {
        return PATH;
    }

    @Override
    public Message deserializeResponse(String answer) throws TelegramApiRequestException {
        return deserializeResponse(answer, Message.class);
    }

    @Override
    public void validate() throws TelegramApiValidationException {
        if (chatId == null || chatId.isEmpty()) {
            throw new TelegramApiValidationException("ChatId cannot be empty", PATH, "chat_id");
        }
        if (photo == null) {
            throw new TelegramApiValidationException("Photo cannot be null", PATH, "photo");
        }
        photo.validate();
    }
    
    /**
     * Устанавливает уникальный идентификатор чата.
     *
     * @param chatId ID чата (Long).
     */
    public void setChatId(Long chatId) {
        this.chatId = chatId.toString();
    }
    
    /**
     * Включает HTML-разметку для подписи к фото.
     *
     * @return текущий билдер.
     */
    public SendPhoto enableHtml() {
        this.parseMode = ParseMode.HTML;
        return this;
    }
    
    /**
     * Включает MarkdownV2-разметку для подписи к фото.
     *
     * @return текущий билдер.
     */
    public SendPhoto enableMarkdown() {
        this.parseMode = ParseMode.MARKDOWNV2;
        return this;
    }
    
    /**
     * Добавляет эффект "спойлер" (мерцающая пелена) на фото.
     * Пользователь должен будет нажать на фото, чтобы увидеть его.
     *
     * @return текущий билдер.
     */
    public SendPhoto spoiler() {
        this.hasSpoiler = true;
        return this;
    }
    
    /**
     * Создает запрос на отправку фото по его идентификатору (file_id).
     * Используйте это, если фото уже загружено на сервера Telegram.
     *
     * @param chatId ID чата.
     * @param fileId Идентификатор файла.
     * @return готовый объект запроса.
     */
    public static SendPhoto withFileId(Long chatId, String fileId) {
        return SendPhoto.builder()
                .chatId(chatId.toString())
                .photo(new InputFile(fileId))
                .build();
    }
    
    /**
     * Создает запрос на отправку фото с диска.
     *
     * @param chatId ID чата.
     * @param file Локальный файл.
     * @return готовый объект запроса.
     */
    public static SendPhoto withFile(Long chatId, File file) {
        return SendPhoto.builder()
                .chatId(chatId.toString())
                .photo(new InputFile(file))
                .build();
    }
    
    /**
     * Создает запрос на отправку фото из потока данных (InputStream).
     * <p>
     * Полезно, если фото генерируется в памяти (например, графиком) и не сохраняется на диск.
     * </p>
     *
     * @param chatId ID чата.
     * @param stream Поток с изображением.
     * @param fileName Имя файла (обязательно для корректной отправки).
     * @return готовый объект запроса.
     */
    public static SendPhoto withStream(Long chatId, InputStream stream, String fileName) {
        return SendPhoto.builder()
                .chatId(chatId.toString())
                .photo(new InputFile(stream, fileName))
                .build();
    }
    
    @Override
    public Map<String, InputFile> getFiles() {
        return photo != null ? Map.of("photo", photo) : Map.of();
    }
}