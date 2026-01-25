package com.kaleert.nyagram.api.objects.games;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.kaleert.nyagram.api.meta.BotApiObject;

/**
 * Заглушка (placeholder), используемая в объекте {@link com.kaleert.nyagram.api.objects.replykeyboard.buttons.InlineKeyboardButton}.
 * <p>
 * Указывает, что кнопка должна запустить игру.
 * </p>
 *
 * @since 1.0.0
 */
@JsonIgnoreProperties(ignoreUnknown = true)
public record CallbackGame() implements BotApiObject {}