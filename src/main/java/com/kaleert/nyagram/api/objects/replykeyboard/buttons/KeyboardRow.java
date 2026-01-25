package com.kaleert.nyagram.api.objects.replykeyboard.buttons;

import java.util.ArrayList;
import java.util.Collection;

/**
 * Вспомогательный класс, представляющий один ряд кнопок в {@link com.kaleert.nyagram.api.objects.replykeyboard.ReplyKeyboardMarkup}.
 *
 * @since 1.0.0
 */
public class KeyboardRow extends ArrayList<KeyboardButton> {
    
    /**
     * Создает пустой ряд кнопок.
     */
    public KeyboardRow() { super(); }
    
    /**
     * Создает ряд кнопок, копируя элементы из существующей коллекции.
     * @param c Коллекция кнопок.
     */
    public KeyboardRow(Collection<? extends KeyboardButton> c) { super(c); }
    
    /**
     * Добавляет простую текстовую кнопку в ряд.
     * @param text Текст кнопки.
     */
    public void add(String text) {
        this.add(KeyboardButton.text(text));
    }
}