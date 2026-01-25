package com.kaleert.nyagram.api.methods.sending;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.kaleert.nyagram.api.exception.TelegramApiRequestException;
import com.kaleert.nyagram.api.exception.TelegramApiValidationException;
import com.kaleert.nyagram.api.meta.BotApiMethod;
import com.kaleert.nyagram.api.objects.message.MessageEntity;
import com.kaleert.nyagram.api.objects.MessageId;
import com.kaleert.nyagram.api.objects.replykeyboard.ReplyKeyboard;
import lombok.*;

import java.util.List;

/**
 * Используйте этот метод для копирования сообщений любого типа.
 * <p>
 * Метод аналогичен методу пересылки (Forward), но скопированное сообщение не будет иметь
 * ссылки на оригинального отправителя.
 * Возвращает {@link MessageId} отправленного сообщения.
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
public class CopyMessage extends BotApiMethod<MessageId> {
    
    /** Имя метода в Telegram Bot API. */
    public static final String PATH = "copyMessage";

    /**
     * Уникальный идентификатор чата, КУДА копируется сообщение.
     */
    @JsonProperty("chat_id") 
    private String chatId;
    
    /**
     * Уникальный идентификатор чата, ОТКУДА копируется сообщение.
     */
    @JsonProperty("from_chat_id") 
    private String fromChatId;
    
    /**
     * Идентификатор копируемого сообщения.
     */
    @JsonProperty("message_id") 
    private Integer messageId;
    
    /**
     * Идентификатор топика.
     */
    @JsonProperty("message_thread_id") 
    private Integer messageThreadId;
    
    /**
     * Новая подпись. Если не указана, будет использована оригинальная подпись.
     */
    @JsonProperty("caption") 
    private String caption;
    
    /**
     * Режим парсинга новой подписи.
     */
    @JsonProperty("parse_mode") 
    private String parseMode;
    
    /**
     * Список сущностей для новой подписи.
     */
    @JsonProperty("caption_entities") 
    private List<MessageEntity> captionEntities;
    
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
     * Новая клавиатура.
     */
    @JsonProperty("reply_markup") 
    private ReplyKeyboard replyMarkup;

    @Override public String getMethod() { return PATH; }

    @Override
    public MessageId deserializeResponse(String answer) throws TelegramApiRequestException {
        return deserializeResponse(answer, MessageId.class);
    }

    @Override
    public void validate() throws TelegramApiValidationException {
        if (chatId == null) throw new TelegramApiValidationException("ChatId required", PATH);
        if (fromChatId == null) throw new TelegramApiValidationException("FromChatId required", PATH);
        if (messageId == null) throw new TelegramApiValidationException("MessageId required", PATH);
    }
}