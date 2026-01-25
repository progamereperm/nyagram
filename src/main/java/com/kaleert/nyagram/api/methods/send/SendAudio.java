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
import java.util.*;

/**
 * Используйте этот метод для отправки аудиофайлов.
 * <p>
 * Клиенты Telegram будут отображать их во встроенном музыкальном плеере.
 * Поддерживаются файлы .mp3 и .m4a.
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
public class SendAudio extends BotApiMethod<Message> implements MultipartRequest {
    
    /** Имя метода в Telegram Bot API. */
    public static final String PATH = "sendAudio";

    /**
     * Уникальный идентификатор чата.
     */
    @JsonProperty("chat_id")
    private String chatId;

    /**
     * Идентификатор топика.
     */
    @JsonProperty("message_thread_id")
    private Integer messageThreadId;

    /**
     * Аудиофайл для отправки (file_id или новый файл).
     */
    @JsonProperty("audio")
    private InputFile audio;

    /**
     * Подпись к аудио (0-1024 символов).
     */
    @JsonProperty("caption")
    private String caption;

    /**
     * Режим парсинга подписи.
     */
    @JsonProperty("parse_mode")
    private String parseMode;

    /**
     * Список сущностей (для форматирования подписи).
     */
    @JsonProperty("caption_entities")
    private List<MessageEntity> captionEntities;

    /**
     * Длительность аудио в секундах.
     */
    @JsonProperty("duration")
    private Integer duration;

    /**
     * Исполнитель.
     */
    @JsonProperty("performer")
    private String performer;

    /**
     * Название трека.
     */
    @JsonProperty("title")
    private String title;

    /**
     * Обложка альбома (превью).
     */
    @JsonProperty("thumbnail")
    private InputFile thumbnail;

    /**
     * Отключить уведомление.
     */
    @JsonProperty("disable_notification")
    private Boolean disableNotification;

    /**
     * Защитить контент.
     */
    @JsonProperty("protect_content")
    private Boolean protectContent;

    /**
     * ID сообщения для ответа.
     */
    @JsonProperty("reply_to_message_id")
    private Integer replyToMessageId;

    /**
     * Разрешить отправку, если сообщение для ответа не найдено.
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
            throw new TelegramApiValidationException("ChatId обязателен", PATH, "chat_id");
        }
        if (audio == null) {
            throw new TelegramApiValidationException("Audio обязателен", PATH, "audio");
        }
        audio.validate();
        
        if (thumbnail != null) {
            thumbnail.validate();
        }
    }
    
    /**
     * Создает запрос на отправку аудиофайла по его идентификатору (file_id).
     *
     * @param chatId ID чата.
     * @param fileId Идентификатор файла на серверах Telegram.
     * @return готовый объект запроса.
     */
    public static SendAudio withFileId(Long chatId, String fileId) {
        return SendAudio.builder()
                .chatId(chatId.toString())
                .audio(new InputFile(fileId))
                .build();
    }
    
    /**
     * Создает запрос на отправку аудиофайла с диска.
     *
     * @param chatId ID чата.
     * @param file Локальный файл.
     * @return готовый объект запроса.
     */
    public static SendAudio withFile(Long chatId, File file) {
        return SendAudio.builder()
                .chatId(chatId.toString())
                .audio(new InputFile(file))
                .build();
    }
    
    @Override
    public Map<String, InputFile> getFiles() {
        Map<String, InputFile> files = new HashMap<>();
        if (audio != null) files.put("audio", audio);
        if (thumbnail != null) files.put("thumbnail", thumbnail);
        return files;
    }
}