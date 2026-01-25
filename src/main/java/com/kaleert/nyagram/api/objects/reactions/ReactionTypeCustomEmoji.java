package com.kaleert.nyagram.api.objects.reactions;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.kaleert.nyagram.api.exception.TelegramApiValidationException;
import lombok.*;

/**
 * Реакция на основе кастомного эмодзи (доступна Premium пользователям).
 *
 * @since 1.0.0
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ReactionTypeCustomEmoji implements ReactionType {
    private static final String TYPE = "custom_emoji";

    @JsonProperty("type")
    private final String type = TYPE;
    
    /**
     * Уникальный идентификатор кастомного эмодзи.
     */
    @JsonProperty("custom_emoji_id")
    private String customEmojiId;

    @Override
    public void validate() throws TelegramApiValidationException {
        if (customEmojiId == null || customEmojiId.isEmpty()) {
            throw new TelegramApiValidationException("CustomEmojiId cannot be empty", "ReactionTypeCustomEmoji", "custom_emoji_id");
        }
    }
}