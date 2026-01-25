package com.kaleert.nyagram.api.objects.replykeyboard;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.kaleert.nyagram.api.objects.replykeyboard.buttons.InlineKeyboardButton;
import lombok.Builder;
import lombok.Singular;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * Представляет Inline-клавиатуру, которая отображается прямо под сообщением.
 * <p>
 * Состоит из массива массивов кнопок {@link InlineKeyboardButton}.
 * </p>
 *
 * @param inlineKeyboard Сетка кнопок (список рядов).
 *
 * @since 1.0.0
 */
@Builder
public record InlineKeyboardMarkup(
    @JsonProperty("inline_keyboard")
    @Singular("row") List<List<InlineKeyboardButton>> inlineKeyboard
) implements ReplyKeyboard {

    /**
     * Создает клавиатуру с одной кнопкой.
     * @param button Кнопка.
     * @return клавиатура.
     */
    public static InlineKeyboardMarkup createSingle(InlineKeyboardButton button) {
        return new InlineKeyboardMarkup(List.of(List.of(button)));
    }
    
    /**
     * Создает вертикальную клавиатуру (каждая кнопка на новой строке).
     * @param buttons Список кнопок.
     * @return клавиатура.
     */    
    public static InlineKeyboardMarkup createVertical(InlineKeyboardButton... buttons) {
        List<List<InlineKeyboardButton>> rows = new ArrayList<>();
        for (InlineKeyboardButton btn : buttons) {
            rows.add(List.of(btn));
        }
        return new InlineKeyboardMarkup(rows);
    }

    /**
     * Создает горизонтальную клавиатуру (все кнопки в одной строке).
     * @param buttons Список кнопок.
     * @return клавиатура.
     */
    public static InlineKeyboardMarkup createHorizontal(InlineKeyboardButton... buttons) {
        return new InlineKeyboardMarkup(List.of(Arrays.asList(buttons)));
    }
    
    @JsonIgnore
    @Override
    public void validate() {
    }
}