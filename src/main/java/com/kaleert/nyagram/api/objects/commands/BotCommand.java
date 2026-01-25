package com.kaleert.nyagram.api.objects.commands;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.kaleert.nyagram.api.meta.BotApiObject;
import com.kaleert.nyagram.api.meta.Validable;

/**
 * Представляет команду бота.
 * <p>
 * Список таких команд отображается в меню бота при вводе "/" или нажатии кнопки меню.
 * </p>
 *
 * @param command Текст команды (1-32 символа). Должен содержать только строчные английские буквы, цифры и подчеркивания.
 * @param description Описание команды (3-256 символов).
 *
 * @see com.kaleert.nyagram.api.methods.commands.SetMyCommands
 * @since 1.0.0
 */
public record BotCommand(
    @JsonProperty("command") String command,
    @JsonProperty("description") String description
) implements BotApiObject, Validable {

    @Override
    public void validate() {
        if (command == null || command.length() < 1 || command.length() > 32) {
            throw new IllegalArgumentException("Command must be 1-32 chars");
        }
        if (description == null || description.length() < 3 || description.length() > 256) {
             throw new IllegalArgumentException("Description must be 3-256 chars");
        }
    }
}