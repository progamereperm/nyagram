package com.kaleert.nyagram.api.methods.updatingmessages;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.kaleert.nyagram.api.exception.TelegramApiRequestException;
import com.kaleert.nyagram.api.exception.TelegramApiValidationException;
import com.kaleert.nyagram.api.meta.BotApiMethod;
import com.kaleert.nyagram.api.methods.ParseMode;
import com.kaleert.nyagram.api.objects.LinkPreviewOptions;
import com.kaleert.nyagram.api.objects.message.MessageEntity;
import com.kaleert.nyagram.api.objects.replykeyboard.InlineKeyboardMarkup;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import lombok.NonNull;

import java.io.Serializable;
import java.util.List;

/**
 * Используйте этот метод для редактирования текста сообщений.
 * <p>
 * В случае успеха, если редактировалось обычное сообщение, возвращает {@link com.kaleert.nyagram.api.objects.message.Message}.
 * Если редактировалось inline-сообщение, возвращает {@code True}.
 * </p>
 *
 * @since 1.0.0
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode(callSuper = false)
@JsonInclude(JsonInclude.Include.NON_NULL)
public class EditMessageText extends BotApiMethod<Serializable> {
    
    /** Имя метода в Telegram Bot API. */
    public static final String PATH = "editMessageText";

    /**
     * Идентификатор чата.
     */
    @JsonProperty("chat_id")
    private String chatId;

    /**
     * Идентификатор сообщения.
     */
    @JsonProperty("message_id")
    private Integer messageId;

    /**
     * Идентификатор inline-сообщения.
     */
    @JsonProperty("inline_message_id")
    private String inlineMessageId;

    /**
     * Новый текст сообщения (1-4096 символов).
     */
    @JsonProperty("text")
    @NonNull
    private String text;

    /**
     * Режим парсинга.
     */
    @JsonProperty("parse_mode")
    private String parseMode;

    /**
     * Список сущностей.
     */
    @JsonProperty("entities")
    private List<MessageEntity> entities;

    /**
     * Настройки предпросмотра ссылок.
     */
    @JsonProperty("link_preview_options")
    private LinkPreviewOptions linkPreviewOptions;

    /**
     * Новая Inline-клавиатура.
     */
    @JsonProperty("reply_markup")
    private InlineKeyboardMarkup replyMarkup;

    @Override
    public String getMethod() {
        return PATH;
    }

    @Override
    public Serializable deserializeResponse(String answer) throws TelegramApiRequestException {
        try {
            return deserializeResponse(answer, com.kaleert.nyagram.api.objects.message.Message.class);
        } catch (TelegramApiRequestException e) {
            return deserializeResponse(answer, Boolean.class);
        }
    }

    @Override
    public void validate() throws TelegramApiValidationException {
        if (text == null || text.isEmpty()) {
            throw new TelegramApiValidationException("Text cannot be empty", PATH);
        }
        
        boolean hasChat = (chatId != null && messageId != null);
        boolean hasInline = (inlineMessageId != null);

        if (!hasChat && !hasInline) {
            throw new TelegramApiValidationException("Must provide either (chatId + messageId) or inlineMessageId", PATH);
        }
        if (hasChat && hasInline) {
            throw new TelegramApiValidationException("Cannot provide both (chatId + messageId) and inlineMessageId", PATH);
        }
    }
    
    /**
     * Устанавливает уникальный идентификатор чата из Long.
     *
     * @param chatId ID чата.
     */
    public void setChatId(Long chatId) {
        this.chatId = chatId.toString();
    }
    
    /**
     * Включает HTML-разметку для текста сообщения.
     *
     * @return текущий билдер.
     */
    public EditMessageText enableHtml() {
        this.parseMode = ParseMode.HTML;
        return this;
    }
    
    /**
     * Включает MarkdownV2-разметку для текста сообщения.
     * <p>
     * Позволяет использовать расширенный синтаксис (спойлеры, зачеркивание, вложенные сущности).
     * </p>
     *
     * @return текущий билдер.
     */
    public EditMessageText enableMarkdown() {
        this.parseMode = ParseMode.MARKDOWNV2;
        return this;
    }
    
    /**
     * Отключает генерацию превью ссылок в отредактированном сообщении.
     *
     * @return текущий билдер.
     */
    public EditMessageText disableWebPreview() {
        if (this.linkPreviewOptions == null) this.linkPreviewOptions = new LinkPreviewOptions();
        this.linkPreviewOptions.setIsDisabled(true);
        return this;
    }
}