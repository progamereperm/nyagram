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
 * Используйте этот метод для отправки файлов общего назначения (документов).
 * <p>
 * Поддерживаются файлы любого типа. Размер файла до 50 МБ.
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
public class SendDocument extends BotApiMethod<Message> implements MultipartRequest {
    
    /** Имя метода в Telegram Bot API. */
    public static final String PATH = "sendDocument";

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
     * Файл для отправки.
     */
    @JsonProperty("document")
    private InputFile document;

    /**
     * Превью (обложка) документа.
     */
    @JsonProperty("thumbnail")
    private InputFile thumbnail;

    /**
     * Подпись к документу.
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
     * Отключает автоматическое определение типа контента на стороне сервера.
     */
    @JsonProperty("disable_content_type_detection")
    private Boolean disableContentTypeDetection;

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
            throw new TelegramApiValidationException("ChatId обязателен", PATH, "chat_id");
        }
        if (document == null) {
            throw new TelegramApiValidationException("Document обязателен", PATH, "document");
        }
        document.validate();
        
        if (thumbnail != null) {
            thumbnail.validate();
        }
    }
    
    /**
     * Устанавливает уникальный идентификатор чата.
     *
     * @param chatId ID чата.
     */
    public void setChatId(Long chatId) {
        this.chatId = chatId.toString();
    }
    
    /**
     * Включает HTML-разметку для подписи.
     *
     * @return текущий билдер.
     */
    public SendDocument enableHtml() {
        this.parseMode = ParseMode.HTML;
        return this;
    }
    
    /**
     * Включает MarkdownV2-разметку для подписи.
     *
     * @return текущий билдер.
     */
    public SendDocument enableMarkdown() {
        this.parseMode = ParseMode.MARKDOWNV2;
        return this;
    }
    
    /**
     * Отключает автоматическое определение типа контента на стороне сервера.
     *
     * @return текущий билдер.
     */
    public SendDocument disableDetection() {
        this.disableContentTypeDetection = true;
        return this;
    }

    /**
     * Создает запрос на отправку документа по идентификатору (file_id).
     *
     * @param chatId ID чата.
     * @param fileId Идентификатор файла.
     * @return готовый объект запроса.
     */
    public static SendDocument withFileId(Long chatId, String fileId) {
        return SendDocument.builder()
                .chatId(chatId.toString())
                .document(new InputFile(fileId))
                .build();
    }
    
    /**
     * Создает запрос на отправку документа с диска.
     *
     * @param chatId ID чата.
     * @param file Локальный файл.
     * @return готовый объект запроса.
     */
    public static SendDocument withFile(Long chatId, File file) {
        return SendDocument.builder()
                .chatId(chatId.toString())
                .document(new InputFile(file))
                .build();
    }

    /**
     * Создает запрос на отправку документа из потока (InputStream).
     *
     * @param chatId ID чата.
     * @param stream Поток данных.
     * @param fileName Имя файла (обязательно для потока).
     * @return готовый объект запроса.
     */
    public static SendDocument withStream(Long chatId, InputStream stream, String fileName) {
        return SendDocument.builder()
                .chatId(chatId.toString())
                .document(new InputFile(stream, fileName))
                .build();
    }
    
    @Override
    public Map<String, InputFile> getFiles() {
        Map<String, InputFile> files = new HashMap<>();
        if (document != null) {
            files.put("document", document);
        }
        if (thumbnail != null) {
            files.put("thumbnail", thumbnail);
        }
        return files;
    }
}