package com.kaleert.nyagram.api.methods.gifts;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.kaleert.nyagram.api.exception.TelegramApiValidationException;
import com.kaleert.nyagram.api.meta.BotApiMethodBoolean;
import com.kaleert.nyagram.api.objects.message.MessageEntity;
import lombok.*;
import java.util.List;

/**
 * Отправляет подарок указанному пользователю.
 * <p>
 * Подарок — это специальный тип виртуального товара в Telegram Stars.
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
public class SendGift extends BotApiMethodBoolean {
    
    /** Имя метода в Telegram Bot API. */
    public static final String PATH = "sendGift";
    
    /**
     * Уникальный идентификатор целевого пользователя.
     */
    @JsonProperty("user_id")
    private Long userId;
    
    /**
     * Уникальный идентификатор подарка.
     */
    @JsonProperty("gift_id")
    private String giftId;
    
    /**
     * Текст, который будет показан вместе с подарком (0-255 символов).
     */
    @JsonProperty("text")
    private String text;
    
    /**
     * Режим парсинга текста (HTML/Markdown).
     */
    @JsonProperty("text_parse_mode")
    private String textParseMode;
    
    /**
     * Список специальных сущностей в тексте.
     */
    @JsonProperty("text_entities")
    private List<MessageEntity> textEntities;

    @Override public String getMethod() { return PATH; }

    @Override public void validate() throws TelegramApiValidationException {
        if (userId == null) throw new TelegramApiValidationException("UserId required", PATH);
        if (giftId == null) throw new TelegramApiValidationException("GiftId required", PATH);
    }
}