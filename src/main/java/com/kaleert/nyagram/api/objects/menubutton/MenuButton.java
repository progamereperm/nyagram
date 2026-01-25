package com.kaleert.nyagram.api.objects.menubutton;

import com.fasterxml.jackson.annotation.JsonSubTypes;
import com.fasterxml.jackson.annotation.JsonTypeInfo;
import com.kaleert.nyagram.api.meta.BotApiObject;

/**
 * Описывает кнопку меню бота (синяя кнопка слева от поля ввода).
 *
 * @see com.kaleert.nyagram.api.methods.menubutton.SetChatMenuButton
 * @since 1.0.0
 */
@JsonTypeInfo(use = JsonTypeInfo.Id.NAME, property = "type")
@JsonSubTypes({
    @JsonSubTypes.Type(value = MenuButtonCommands.class, name = "commands"),
    @JsonSubTypes.Type(value = MenuButtonWebApp.class, name = "web_app"),
    @JsonSubTypes.Type(value = MenuButtonDefault.class, name = "default")
})
public sealed interface MenuButton extends BotApiObject permits MenuButtonCommands, MenuButtonWebApp, MenuButtonDefault {
}