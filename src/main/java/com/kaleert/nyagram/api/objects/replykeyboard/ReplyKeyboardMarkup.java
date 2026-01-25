package com.kaleert.nyagram.api.objects.replykeyboard;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.kaleert.nyagram.api.exception.TelegramApiValidationException;
import com.kaleert.nyagram.api.objects.replykeyboard.buttons.KeyboardButton;
import lombok.Builder;
import lombok.Singular;

import java.util.ArrayList;
import java.util.List;

/**
 * Представляет кастомную клавиатуру с вариантами ответов (нижняя клавиатура).
 * <p>
 * Отображается вместо стандартной клавиатуры ввода текста.
 * </p>
 *
 * @param keyboard Массив рядов кнопок.
 * @param isPersistent Если true, клавиатура всегда видна (даже если поле ввода скрыто).
 * @param resizeKeyboard Если true, высота клавиатуры подстраивается под количество кнопок (становится меньше).
 *                       По умолчанию false (клавиатура занимает ту же высоту, что и стандартная).
 * @param oneTimeKeyboard Если true, клавиатура скроется после нажатия на любую кнопку.
 * @param inputFieldPlaceholder Текст-подсказка в поле ввода (когда оно пустое).
 * @param selective Если true, клавиатура будет показана только определенным пользователям
 *                  (упомянутым в тексте или тому, на чье сообщение ответил бот).
 *
 * @since 1.0.0
 */
@Builder
@JsonInclude(JsonInclude.Include.NON_NULL)
public record ReplyKeyboardMarkup(
    @JsonProperty("keyboard")
    @Singular("row") List<List<KeyboardButton>> keyboard,
    
    @JsonProperty("is_persistent") Boolean isPersistent,
    @JsonProperty("resize_keyboard") Boolean resizeKeyboard,
    @JsonProperty("one_time_keyboard") Boolean oneTimeKeyboard,
    @JsonProperty("input_field_placeholder") String inputFieldPlaceholder,
    @JsonProperty("selective") Boolean selective
) implements ReplyKeyboard {

    /**
     * Создает простую клавиатуру из списка строк.
     * @param resize Нужно ли уменьшать размер клавиатуры.
     * @param texts Тексты кнопок.
     * @return объект ReplyKeyboardMarkup.
     */
    public static ReplyKeyboardMarkup simple(boolean resize, String... texts) {
        List<List<KeyboardButton>> rows = new ArrayList<>();
        List<KeyboardButton> currentRow = new ArrayList<>();
        
        for (String text : texts) {
            currentRow.add(KeyboardButton.text(text));
        }
        rows.add(currentRow);

        return ReplyKeyboardMarkup.builder()
                .keyboard(rows)
                .resizeKeyboard(resize)
                .build();
    }
    /**
     * Создает вертикальную клавиатуру (каждая кнопка на новой строке).
     * @param resize Нужно ли уменьшать размер клавиатуры.
     * @param texts Тексты кнопок.
     * @return объект ReplyKeyboardMarkup.
     */
    public static ReplyKeyboardMarkup vertical(boolean resize, String... texts) {
        List<List<KeyboardButton>> rows = new ArrayList<>();
        for (String text : texts) {
            rows.add(List.of(KeyboardButton.text(text)));
        }
        return ReplyKeyboardMarkup.builder()
                .keyboard(rows)
                .resizeKeyboard(resize)
                .build();
    }

    @JsonIgnore
    @Override
    public void validate() throws TelegramApiValidationException {
        if (keyboard == null || keyboard.isEmpty()) {
            throw new TelegramApiValidationException("Keyboard cannot be empty", "ReplyKeyboardMarkup");
        }
    }
}