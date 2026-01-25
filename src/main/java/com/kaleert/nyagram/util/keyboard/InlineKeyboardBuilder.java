package com.kaleert.nyagram.util.keyboard;

import com.kaleert.nyagram.api.objects.replykeyboard.InlineKeyboardMarkup;
import com.kaleert.nyagram.api.objects.replykeyboard.buttons.InlineKeyboardButton;
import com.kaleert.nyagram.api.objects.replykeyboard.buttons.InlineKeyboardRow;

import java.util.ArrayList;
import java.util.List;

/**
 * Строитель (Builder) для создания {@link com.kaleert.nyagram.api.objects.replykeyboard.InlineKeyboardMarkup}.
 * <p>
 * Позволяет создавать клавиатуры в Fluent-стиле, автоматически управляя рядами кнопок.
 * Пример:
 * <pre>
 * InlineKeyboardBuilder.create()
 *     .button("Да", "yes")
 *     .button("Нет", "no")
 *     .row()
 *     .url("Справка", "https://google.com")
 *     .build();
 * </pre>
 * </p>
 *
 * @since 1.0.0
 */
public class InlineKeyboardBuilder {

    private final List<List<InlineKeyboardButton>> rows = new ArrayList<>();
    private final List<InlineKeyboardButton> currentRow = new ArrayList<>();

    private InlineKeyboardBuilder() {}
    
    /**
     * Создает новый экземпляр строителя.
     *
     * @return Новый InlineKeyboardBuilder.
     */
    public static InlineKeyboardBuilder create() {
        return new InlineKeyboardBuilder();
    }
    
    /**
     * Завершает текущий ряд кнопок и создает новый пустой ряд.
     * <p>
     * Следующие добавленные кнопки будут располагаться на новой строке.
     * </p>
     *
     * @return Текущий билдер.
     */
    public InlineKeyboardBuilder row() {
        if (!currentRow.isEmpty()) {
            rows.add(new ArrayList<>(currentRow));
            currentRow.clear();
        }
        return this;
    }
    
    /**
     * Завершает текущий ряд кнопок и переходит на следующую строку.
     * <p>
     * Алиас для метода {@link #row()}. Делает код более читаемым в конце блока кнопок.
     * </p>
     *
     * @return Текущий билдер.
     */
    public InlineKeyboardBuilder endRow() {
        if (!currentRow.isEmpty()) {
            rows.add(new ArrayList<>(currentRow));
            currentRow.clear();
        }
        return this;
    }
    
    /**
     * Добавляет кнопку с callback-данными в текущий ряд.
     *
     * @param text Текст на кнопке.
     * @param callbackData Данные, которые придут боту при нажатии (макс. 64 байта).
     * @return Текущий билдер.
     */
    public InlineKeyboardBuilder button(String text, String callbackData) {
        currentRow.add(InlineKeyboardButton.builder()
                .text(text)
                .callbackData(callbackData)
                .build());
        return this;
    }
    
    /**
     * Добавляет кнопку-ссылку в текущий ряд.
     *
     * @param text Текст на кнопке.
     * @param url Адрес, который откроется при нажатии.
     * @return Текущий билдер.
     */
    public InlineKeyboardBuilder url(String text, String url) {
        currentRow.add(InlineKeyboardButton.builder()
                .text(text)
                .url(url)
                .build());
        return this;
    }
    
    /**
     * "Умное" добавление кнопки: автоматически выбирает тип (URL или Callback).
     * <p>
     * Если {@code data} начинается с "http://" или "https://", создается кнопка-ссылка.
     * В противном случае создается кнопка с callback-данными.
     * </p>
     *
     * @param text Текст на кнопке.
     * @param data URL или Payload.
     * @return Текущий билдер.
     */
    public InlineKeyboardBuilder smartButton(String text, String data) {
        if (data.startsWith("http://") || data.startsWith("https://")) {
            return url(text, data);
        } else {
            return button(text, data);
        }
    }
    
    /**
     * Завершает построение клавиатуры.
     * <p>
     * Добавляет текущий (незавершенный) ряд кнопок в общий список и создает объект {@link InlineKeyboardMarkup}.
     * </p>
     *
     * @return Готовая клавиатура.
     */
    public InlineKeyboardMarkup build() {
        if (!currentRow.isEmpty()) {
            rows.add(new ArrayList<>(currentRow));
        }
        return InlineKeyboardMarkup.builder().inlineKeyboard(rows).build();
    }
}