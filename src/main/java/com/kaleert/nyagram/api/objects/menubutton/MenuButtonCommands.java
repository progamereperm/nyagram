package com.kaleert.nyagram.api.objects.menubutton;

/**
 * Кнопка меню, открывающая список команд бота.
 *
 * @since 1.0.0
 */
public record MenuButtonCommands() implements MenuButton {
    @Override public String toString() { return "commands"; }
}