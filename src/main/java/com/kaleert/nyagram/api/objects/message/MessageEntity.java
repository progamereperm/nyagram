package com.kaleert.nyagram.api.objects.message;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.kaleert.nyagram.api.meta.BotApiObject;
import com.kaleert.nyagram.api.objects.User;
import lombok.Builder;

/**
 * Представляет специальную сущность в тексте сообщения.
 * <p>
 * Это может быть хештег, имя пользователя, ссылка, жирный шрифт и т.д.
 * </p>
 *
 * @param type Тип сущности (mention, hashtag, bold, etc.).
 * @param offset Смещение от начала текста (в UTF-16 code units).
 * @param length Длина сущности (в UTF-16 code units).
 * @param url URL, который будет открыт при нажатии (для text_link).
 * @param user Пользователь, который упоминается (для text_mention).
 * @param language Язык программирования (для pre).
 * @param customEmojiId Уникальный идентификатор кастомного эмодзи (для custom_emoji).
 *
 * @since 1.0.0
 */
@Builder
@JsonIgnoreProperties(ignoreUnknown = true)
public record MessageEntity(
    @JsonProperty("type") String type,
    @JsonProperty("offset") Integer offset,
    @JsonProperty("length") Integer length,
    @JsonProperty("url") String url,
    @JsonProperty("user") User user,
    @JsonProperty("language") String language,
    @JsonProperty("custom_emoji_id") String customEmojiId
) implements BotApiObject {
    
    /** Тип сущности: упоминание пользователя (@username). */
    public static final String MENTION = "mention";
    
    /** Тип сущности: хештег (#hashtag). */
    public static final String HASHTAG = "hashtag";
    
    /** Тип сущности: кэштег ($USD). */
    public static final String CASHTAG = "cashtag";
    
    /** Тип сущности: команда бота (/start@jobs_bot). */
    public static final String BOT_COMMAND = "bot_command";
    
    /** Тип сущности: URL (https://telegram.org). */
    public static final String URL = "url";
    
    /** Тип сущности: email (do-not-reply@telegram.org). */
    public static final String EMAIL = "email";
    
    /** Тип сущности: номер телефона (+1-212-555-0123). */
    public static final String PHONE_NUMBER = "phone_number";
    
    /** Тип сущности: жирный текст. */
    public static final String BOLD = "bold";
    
    /** Тип сущности: курсив. */
    public static final String ITALIC = "italic";
    
    /** Тип сущности: подчеркнутый текст. */
    public static final String UNDERLINE = "underline";
    
    /** Тип сущности: зачеркнутый текст. */
    public static final String STRIKETHROUGH = "strikethrough";
    
    /** Тип сущности: спойлер (скрытый текст). */
    public static final String SPOILER = "spoiler";
    
    /** Тип сущности: цитата (блок с вертикальной чертой). */
    public static final String BLOCKQUOTE = "blockquote";
    
    /** Тип сущности: расширяемая цитата (сворачиваемый блок). */
    public static final String EXPANDABLE_BLOCKQUOTE = "expandable_blockquote";
    
    /** Тип сущности: моноширинный текст (код внутри строки). */
    public static final String CODE = "code";
    
    /** Тип сущности: блок кода (pre). */
    public static final String PRE = "pre";
    
    /** Тип сущности: текстовая ссылка (текст, клик по которому открывает URL). */
    public static final String TEXT_LINK = "text_link";
    
    /** Тип сущности: упоминание пользователя без username (по ID). */
    public static final String TEXT_MENTION = "text_mention";
    
    /** Тип сущности: кастомный эмодзи. */
    public static final String CUSTOM_EMOJI = "custom_emoji";
    
    /**
     * Извлекает текст сущности из полного текста сообщения.
     * @param fullText Полный текст сообщения.
     * @return Подстрока, соответствующая этой сущности.
     */
    public String getText(String fullText) {
        if (fullText == null || offset == null || length == null) {
            return null;
        }
        int end = Math.min(fullText.length(), offset + length);
        return fullText.substring(offset, end);
    }
}