package com.kaleert.nyagram.api.methods.send;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.kaleert.nyagram.api.exception.TelegramApiRequestException;
import com.kaleert.nyagram.api.exception.TelegramApiValidationException;
import com.kaleert.nyagram.api.meta.BotApiMethod;
import com.kaleert.nyagram.api.meta.MultipartRequest;
import com.kaleert.nyagram.api.objects.InputFile;
import com.kaleert.nyagram.api.objects.message.Message;
import com.kaleert.nyagram.api.objects.message.MessageEntity;
import com.kaleert.nyagram.api.objects.replykeyboard.ReplyKeyboard;
import lombok.*;

import java.io.File;
import java.util.List;
import java.util.Map;

/**
 * Используйте этот метод для отправки аудиофайлов, если вы хотите, чтобы они отображались
 * как воспроизводимые голосовые сообщения.
 * <p>
 * Для этого файл должен быть в формате .ogg с кодеком OPUS.
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
public class SendVoice extends BotApiMethod<Message> implements MultipartRequest {
    
    /** Имя метода в Telegram Bot API. */
    public static final String PATH = "sendVoice";

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
    @JsonProperty("voice")
    private InputFile voice;

    /**
     * Подпись к голосовому сообщению.
     */
    @JsonProperty("caption")
    private String caption;

    /**
     * Режим парсинга подписи.
     */
    @JsonProperty("parse_mode")
    private String parseMode;

    /**
     * Список сущностей.
     */
    @JsonProperty("caption_entities")
    private List<MessageEntity> captionEntities;

    /**
     * Длительность записи в секундах.
     */
    @JsonProperty("duration")
    private Integer duration;

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
     * Разрешить отправку без ответа.
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
        if (voice == null) {
            throw new TelegramApiValidationException("Voice cannot be null", PATH, "voice");
        }
        voice.validate();
    }

    @Override
    public Map<String, InputFile> getFiles() {
        return voice != null ? Map.of("voice", voice) : Map.of();
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
     * Создает запрос на отправку голосового сообщения по его идентификатору (file_id).
     *
     * @param chatId ID чата.
     * @param fileId Идентификатор файла.
     * @return готовый объект запроса.
     */
    public static SendVoice withFileId(Long chatId, String fileId) {
        return SendVoice.builder()
                .chatId(chatId.toString())
                .voice(new InputFile(fileId))
                .build();
    }
    
    /**
     * Создает запрос на отправку голосового сообщения с диска.
     * <p>
     * Файл должен быть в формате .ogg с кодеком OPUS.
     * </p>
     *
     * @param chatId ID чата.
     * @param file Локальный файл.
     * @return готовый объект запроса.
     */
    public static SendVoice withFile(Long chatId, File file) {
        return SendVoice.builder()
                .chatId(chatId.toString())
                .voice(new InputFile(file))
                .build();
    }
}