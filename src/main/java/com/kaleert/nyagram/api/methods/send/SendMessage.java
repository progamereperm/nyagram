package com.kaleert.nyagram.api.methods.send;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.kaleert.nyagram.api.exception.TelegramApiRequestException;
import com.kaleert.nyagram.api.exception.TelegramApiValidationException;
import com.kaleert.nyagram.api.meta.BotApiMethod;
import com.kaleert.nyagram.api.methods.ParseMode;
import com.kaleert.nyagram.api.objects.LinkPreviewOptions;
import com.kaleert.nyagram.api.objects.message.MessageEntity;
import com.kaleert.nyagram.api.objects.message.Message;
import com.kaleert.nyagram.api.objects.replykeyboard.ReplyKeyboard;
import lombok.*;

import java.util.List;

/**
 * Используйте этот метод для отправки текстовых сообщений.
 * <p>
 * Поддерживает форматирование (HTML, Markdown), отключение предпросмотра ссылок
 * и прикрепление клавиатур.
 * </p>
 *
 * @see com.kaleert.nyagram.api.methods.ParseMode
 * @since 1.0.0
 */
@EqualsAndHashCode(callSuper = false)
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class SendMessage extends BotApiMethod<Message> {
    
    /** Имя метода в Telegram Bot API. */
    public static final String PATH = "sendMessage";
    
    /**
     * Уникальный идентификатор целевого чата или username целевого канала (в формате {@code @channelusername}).
     */
    @JsonProperty("chat_id")
    private String chatId;
    
    /**
     * Уникальный идентификатор топика (Message Thread ID).
     * Обязателен, если сообщение отправляется в конкретный топик супергруппы-форума.
     */
    @JsonProperty("message_thread_id")
    private Integer messageThreadId;
    
    /**
     * Текст отправляемого сообщения (1-4096 символов).
     */
    @JsonProperty("text")
    private String text;
    
    /**
     * Режим парсинга сущностей в тексте сообщения.
     * <p>
     * Возможные значения: "MarkdownV2", "HTML", "Markdown".
     * Рекомендуется использовать {@link com.kaleert.nyagram.api.methods.ParseMode}.
     * </p>
     */
    @JsonProperty("parse_mode")
    private String parseMode;

    @JsonProperty("entities")
    private List<MessageEntity> entities;

    @JsonProperty("link_preview_options")
    private LinkPreviewOptions linkPreviewOptions;
    
    /**
     * Отключает уведомление о сообщении.
     * Пользователи получат уведомление без звука.
     */
    @JsonProperty("disable_notification")
    private Boolean disableNotification;
    
    /**
     * Защищает содержимое отправленного сообщения от пересылки и сохранения.
     */
    @JsonProperty("protect_content")
    private Boolean protectContent;
    
    /**
     * Если сообщение является ответом, ID исходного сообщения.
     */
    @JsonProperty("reply_to_message_id")
    private Integer replyToMessageId;

    @JsonProperty("allow_sending_without_reply")
    private Boolean allowSendingWithoutReply;
    
    /**
     * Дополнительные возможности интерфейса.
     * Объект, представляющий Inline-клавиатуру, обычную клавиатуру (ReplyKeyboard) или удаление клавиатуры.
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
        if (text == null || text.isEmpty()) {
            throw new TelegramApiValidationException("Text cannot be empty", PATH, "text");
        }
        if (text.length() > 4096) {
            throw new TelegramApiValidationException("Text is too long (max 4096 chars)", PATH, "text");
        }
        if (replyMarkup != null) {
            replyMarkup.validate();
        }
        if (linkPreviewOptions != null) {
            linkPreviewOptions.validate();
        }
    }
    
    /**
     * Устанавливает уникальный идентификатор чата.
     *
     * @param chatId ID чата (Long).
     */
    public void setChatId(@NonNull Long chatId) {
        this.chatId = chatId.toString();
    }
    
    /**
     * Включает HTML-разметку.
     * Позволяет использовать теги {@code <b>}, {@code <i>}, {@code <a>} и другие.
     *
     * @return текущий билдер.
     */
    public SendMessage enableHtml() {
        this.parseMode = ParseMode.HTML;
        return this;
    }
    
    /**
     * Включает MarkdownV2-разметку.
     * Позволяет использовать расширенный синтаксис Markdown (спойлеры, подчеркивание).
     *
     * @return текущий билдер.
     */
    public SendMessage enableMarkdown() {
        this.parseMode = ParseMode.MARKDOWNV2;
        return this;
    }
    
    /**
     * Отключает генерацию превью ссылки в сообщении.
     * <p>
     * Если в тексте есть ссылки, Telegram обычно создает для них предпросмотр (картинку и заголовок).
     * Этот метод предотвращает это поведение.
     * </p>
     *
     * @return текущий билдер.
     */
    public SendMessage disableWebPagePreview() {
        if (this.linkPreviewOptions == null) {
            this.linkPreviewOptions = new LinkPreviewOptions();
        }
        this.linkPreviewOptions.setIsDisabled(true);
        return this;
    }
    
    /**
     * Устанавливает ID сообщения, на которое нужно ответить (Reply).
     *
     * @param messageId ID исходного сообщения.
     * @return текущий билдер.
     */
    public SendMessage replyTo(Integer messageId) {
        this.replyToMessageId = messageId;
        return this;
    }
}