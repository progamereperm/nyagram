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
import java.util.*;
/**
 * Используйте этот метод для отправки видеофайлов.
 * <p>
 * Клиенты Telegram поддерживают видео в формате mp4 (другие форматы могут быть отправлены как {@code Document}).
 * Боты могут отправлять видеофайлы размером до 50 МБ.
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
public class SendVideo extends BotApiMethod<Message> implements MultipartRequest {
    
    /** Имя метода в Telegram Bot API. */
    public static final String PATH = "sendVideo";

    /**
     * Уникальный идентификатор чата или username канала.
     */
    @JsonProperty("chat_id")
    private String chatId;

    /**
     * Идентификатор топика.
     */
    @JsonProperty("message_thread_id")
    private Integer messageThreadId;

    /**
     * Видео для отправки (file_id, url или новый файл).
     */
    @JsonProperty("video")
    private InputFile video;

    /**
     * Длительность видео в секундах.
     */
    @JsonProperty("duration")
    private Integer duration;

    /**
     * Ширина видео.
     */
    @JsonProperty("width")
    private Integer width;

    /**
     * Высота видео.
     */
    @JsonProperty("height")
    private Integer height;

    /**
     * Обложка (превью) для видео.
     */
    @JsonProperty("thumbnail")
    private InputFile thumbnail;

    /**
     * Подпись к видео (0-1024 символов).
     */
    @JsonProperty("caption")
    private String caption;

    /**
     * Режим парсинга подписи (HTML/Markdown).
     */
    @JsonProperty("parse_mode")
    private String parseMode;

    /**
     * Список специальных сущностей в подписи.
     */
    @JsonProperty("caption_entities")
    private List<MessageEntity> captionEntities;

    /**
     * Если true, видео будет оптимизировано для потоковой передачи (streaming).
     */
    @JsonProperty("supports_streaming")
    private Boolean supportsStreaming;

    /**
     * Добавляет эффект спойлера (мерцание).
     */
    @JsonProperty("has_spoiler")
    private Boolean hasSpoiler;

    /**
     * Отключить уведомление.
     */
    @JsonProperty("disable_notification")
    private Boolean disableNotification;

    /**
     * Защитить контент от пересылки и сохранения.
     */
    @JsonProperty("protect_content")
    private Boolean protectContent;

    /**
     * ID сообщения, на которое нужно ответить.
     */
    @JsonProperty("reply_to_message_id")
    private Integer replyToMessageId;

    /**
     * Разрешить отправку, даже если сообщение для ответа не найдено.
     */
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
        if (video == null) {
            throw new TelegramApiValidationException("Video cannot be null", PATH, "video");
        }
        video.validate();
        
        if (thumbnail != null) {
            thumbnail.validate();
        }
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
     * Включает режим потокового воспроизведения.
     * <p>
     * Видео можно будет смотреть сразу, не дожидаясь полной загрузки.
     * </p>
     *
     * @return текущий билдер.
     */
    public SendVideo asStreaming() {
        this.supportsStreaming = true;
        return this;
    }
    
    /**
     * Добавляет эффект "спойлер" (мерцающая пелена) на видео.
     * Видео будет скрыто, пока пользователь не нажмет на него.
     *
     * @return текущий билдер.
     */
    public SendVideo spoiler() {
        this.hasSpoiler = true;
        return this;
    }
    
    /**
     * Устанавливает размеры видео (ширину и высоту).
     *
     * @param width Ширина в пикселях.
     * @param height Высота в пикселях.
     * @return текущий билдер.
     */
    public SendVideo withDimensions(int width, int height) {
        this.width = width;
        this.height = height;
        return this;
    }
    
    /**
     * Устанавливает длительность видео.
     *
     * @param seconds Длительность в секундах.
     * @return текущий билдер.
     */
    public SendVideo withDuration(int seconds) {
        this.duration = seconds;
        return this;
    }
    
    /**
     * Создает запрос на отправку видео с диска.
     *
     * @param chatId ID чата.
     * @param file Локальный файл видео (обычно mp4).
     * @return готовый объект запроса.
     */
    public static SendVideo withFile(Long chatId, File file) {
        return SendVideo.builder()
                .chatId(chatId.toString())
                .video(new InputFile(file))
                .build();
    }
    
    /**
     * Создает запрос на отправку видео по его идентификатору (file_id).
     *
     * @param chatId ID чата.
     * @param fileId Идентификатор файла.
     * @return готовый объект запроса.
     */
    public static SendVideo withFileId(Long chatId, String fileId) {
        return SendVideo.builder()
                .chatId(chatId.toString())
                .video(new InputFile(fileId))
                .build();
    }
    
    @Override
    public Map<String, InputFile> getFiles() {
        Map<String, InputFile> files = new HashMap<>();
        if (video != null) files.put("video", video);
        if (thumbnail != null) files.put("thumbnail", thumbnail);
        return files;
    }
}