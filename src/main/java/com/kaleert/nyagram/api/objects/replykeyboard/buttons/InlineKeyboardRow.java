package com.kaleert.nyagram.api.objects.replykeyboard.buttons;

import java.util.ArrayList;
import java.util.Collection;

/**
 * Вспомогательный класс, представляющий один ряд кнопок в {@link com.kaleert.nyagram.api.objects.replykeyboard.InlineKeyboardMarkup}.
 * <p>
 * Является наследником {@link ArrayList}, что упрощает создание списков кнопок.
 * </p>
 *
 * @since 1.0.0
 */
public class InlineKeyboardRow extends ArrayList<InlineKeyboardButton> {
    
    /**
     * Создает пустой ряд кнопок.
     */
    public InlineKeyboardRow() { super(); }
    
    /**
     * Создает ряд кнопок, копируя элементы из существующей коллекции.
     * @param c Коллекция кнопок.
     */
    public InlineKeyboardRow(Collection<? extends InlineKeyboardButton> c) { super(c); }
    
    /**
     * Удобный конструктор для создания ряда с одной кнопкой.
     * @param button Кнопка.
     */
    public InlineKeyboardRow(InlineKeyboardButton button) {
        super();
        this.add(button);
    }
}