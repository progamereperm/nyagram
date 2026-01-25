package com.kaleert.nyagram.api.objects.reactions;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.kaleert.nyagram.api.exception.TelegramApiValidationException;
import lombok.*;

/**
 * Платная реакция (отправка Telegram Stars).
 *
 * @since 1.0.0
 */
@Data
@Builder
@NoArgsConstructor
public class ReactionTypePaid implements ReactionType {
    private static final String TYPE = "paid";

    @JsonProperty("type")
    private final String type = TYPE;

    @Override
    public void validate() throws TelegramApiValidationException {
    }
}