package com.kaleert.nyagram.util.keyboard;

import com.kaleert.nyagram.api.objects.replykeyboard.ReplyKeyboardMarkup;
import com.kaleert.nyagram.api.objects.replykeyboard.buttons.KeyboardRow;
import com.kaleert.nyagram.api.objects.replykeyboard.buttons.KeyboardButton;

import java.util.ArrayList;
import java.util.List;

/**
 * Строитель (Builder) для создания {@link com.kaleert.nyagram.api.objects.replykeyboard.ReplyKeyboardMarkup}.
 * <p>
 * Позволяет легко собирать обычные клавиатуры (под полем ввода), настраивать их размер
 * и поведение (скрытие после нажатия).
 * </p>
 *
 * @since 1.0.0
 */
public class ReplyKeyboardBuilder {

    private final List<KeyboardRow> rows = new ArrayList<>();
    private KeyboardRow currentRow = new KeyboardRow();
    private boolean resizeKeyboard = true;
    private boolean oneTimeKeyboard = false;

    private ReplyKeyboardBuilder() {}
     
    /**
     * Создает новый экземпляр строителя.
     *
     * @return Новый ReplyKeyboardBuilder.
     */
    public static ReplyKeyboardBuilder create() {
        return new ReplyKeyboardBuilder();
    }
    
    /**
     * Завершает текущий ряд и начинает новый.
     *
     * @return Текущий билдер.
     */
    public ReplyKeyboardBuilder row() {
        if (!currentRow.isEmpty()) {
            rows.add(currentRow);
            currentRow = new KeyboardRow();
        }
        return this;
    }
    
    /**
     * Завершает текущий ряд кнопок.
     * <p>
     * Алиас для {@link #row()}.
     * </p>
     *
     * @return Текущий билдер.
     */
    public ReplyKeyboardBuilder endRow() {
        return row();
    }
    
    /**
     * Добавляет простую текстовую кнопку в текущий ряд.
     *
     * @param text Текст кнопки.
     * @return Текущий билдер.
     */
    public ReplyKeyboardBuilder button(String text) {
        currentRow.add(text);
        return this;
    }
    
    /**
     * Включает автоматическое изменение размера клавиатуры (Resize).
     * <p>
     * Клавиатура будет занимать ровно столько места, сколько нужно для кнопок,
     * а не половину экрана. Рекомендуется использовать почти всегда.
     * </p>
     *
     * @return Текущий билдер.
     */
    public ReplyKeyboardBuilder resize() {
        this.resizeKeyboard = true;
        return this;
    }
    
    /**
     * Отключает автоматическое изменение размера клавиатуры.
     * <p>
     * Клавиатура будет занимать стандартную высоту (обычно половину экрана).
     * </p>
     *
     * @return Текущий билдер.
     */
    public ReplyKeyboardBuilder noResize() {
        this.resizeKeyboard = false;
        return this;
    }
     
    /**
     * Делает клавиатуру одноразовой.
     * <p>
     * Клавиатура будет скрыта автоматически после нажатия на любую кнопку.
     * </p>
     *
     * @return Текущий билдер.
     */
    public ReplyKeyboardBuilder oneTime() {
        this.oneTimeKeyboard = true;
        return this;
    }
    
    /**
     * Завершает построение и возвращает готовый объект клавиатуры.
     * <p>
     * Автоматически добавляет последний ряд кнопок, если он не пуст.
     * </p>
     *
     * @return Объект {@link ReplyKeyboardMarkup}.
     */
    public ReplyKeyboardMarkup build() {
        if (!currentRow.isEmpty()) {
            rows.add(currentRow);
        }
        
        return ReplyKeyboardMarkup.builder()
                .keyboard(rows)
                .resizeKeyboard(resizeKeyboard)
                .oneTimeKeyboard(oneTimeKeyboard)
                .build();
    }
}