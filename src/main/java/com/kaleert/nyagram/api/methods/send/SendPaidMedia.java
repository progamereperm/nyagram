package com.kaleert.nyagram.api.methods.send;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.kaleert.nyagram.api.exception.TelegramApiRequestException;
import com.kaleert.nyagram.api.exception.TelegramApiValidationException;
import com.kaleert.nyagram.api.meta.BotApiMethod;
import com.kaleert.nyagram.api.meta.MultipartRequest;
import com.kaleert.nyagram.api.objects.media.InputPaidMedia;
import com.kaleert.nyagram.api.objects.InputFile;
import com.kaleert.nyagram.api.objects.message.Message;
import com.kaleert.nyagram.api.objects.message.MessageEntity;
import com.kaleert.nyagram.api.objects.replykeyboard.ReplyKeyboard;
import lombok.*;

import java.util.*;

/**
 * Используйте этот метод для отправки платных медиафайлов в канал или бот.
 * <p>
 * Пользователь должен заплатить указанное количество Telegram Stars, чтобы увидеть контент.
 * </p>
 *
 * @since 1.0.0
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode(callSuper = false)
public class SendPaidMedia extends BotApiMethod<Message> implements MultipartRequest {
        
    /** Имя метода в Telegram Bot API. */
    public static final String PATH = "sendPaidMedia";

    /**
     * Уникальный идентификатор чата.
     */
    @JsonProperty("chat_id") private String chatId;
    
    /**
     * Стоимость медиа в Telegram Stars (1-2500).
     */
    @JsonProperty("star_count") private Integer starCount;
    
    /**
     * Список платных медиа (фото или видео).
     */
    @JsonProperty("media") private List<InputPaidMedia> media;
    
    /**
     * Подпись к медиа.
     */
    @JsonProperty("caption") private String caption;
    
    /**
     * Режим парсинга подписи.
     */
    @JsonProperty("parse_mode") private String parseMode;
    
    /**
     * Список сущностей.
     */
    @JsonProperty("caption_entities") private List<MessageEntity> captionEntities;
    
    /**
     * Полезная нагрузка (Payload), которая вернется боту при покупке.
     */
    @JsonProperty("payload") private String payload;
    
    /**
     * Защитить контент.
     */
    @JsonProperty("protect_content") private Boolean protectContent;
    
    /**
     * Клавиатура.
     */
    @JsonProperty("reply_markup") private ReplyKeyboard replyMarkup;

    @Override public String getMethod() { return PATH; }
    
    @Override public Message deserializeResponse(String answer) throws TelegramApiRequestException {
        return deserializeResponse(answer, Message.class);
    }

    @Override public void validate() throws TelegramApiValidationException {
        if (chatId == null) throw new TelegramApiValidationException("ChatID обязателен", PATH);
        if (starCount == null || starCount < 1) throw new TelegramApiValidationException("StarCount должен быть >= 1", PATH);
        if (media == null || media.isEmpty()) throw new TelegramApiValidationException("Список медиа не может быть пустым", PATH);
        for(InputPaidMedia m : media) m.validate();
    }
    
    @Override
    public Map<String, InputFile> getFiles() {
        Map<String, InputFile> files = new HashMap<>();
        List<InputPaidMedia> processedList = new ArrayList<>(media.size());
        
        for (InputPaidMedia m : media) {
            InputPaidMedia processed = m;

            if (m.getMediaFile() != null && m.getMediaFile().isNew()) {
                String attachName = "paid_" + UUID.randomUUID();
                
                files.put(attachName, m.getMediaFile());
                
                processed = m.withMedia("attach://" + attachName);
            }
            processedList.add(processed);
        } 
        this.media = processedList;
        
        return files;
    }
}