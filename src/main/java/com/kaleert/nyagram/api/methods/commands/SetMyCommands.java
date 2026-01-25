package com.kaleert.nyagram.api.methods.commands;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.kaleert.nyagram.api.exception.TelegramApiValidationException;
import com.kaleert.nyagram.api.meta.BotApiMethodBoolean;
import com.kaleert.nyagram.api.objects.commands.BotCommand;
import com.kaleert.nyagram.api.objects.commands.scope.BotCommandScope;
import lombok.*;

import java.util.ArrayList;
import java.util.List;

/**
 * Используйте этот метод для изменения списка команд бота.
 * <p>
 * Эти команды будут отображаться в меню бота при нажатии кнопки "/" или при вводе.
 * </p>
 *
 * @since 1.0.0
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode(callSuper = false)
@JsonIgnoreProperties(ignoreUnknown = true)
@JsonInclude(JsonInclude.Include.NON_NULL)
public class SetMyCommands extends BotApiMethodBoolean {

    /** Имя метода в Telegram Bot API. */
    public static final String PATH = "setMyCommands";
    
    /**
     * Список команд, которые будут установлены. Не более 100 команд.
     * Команды должны состоять из 1-32 символов (строчные английские буквы, цифры и подчеркивания).
     */
    @JsonProperty("commands")
    private List<BotCommand> commands;
    
    /**
     * Область действия (Scope). Позволяет задавать разные наборы команд для личных чатов,
     * групп, администраторов и конкретных пользователей.
     */
    @JsonProperty("scope")
    private BotCommandScope scope;
    
    /**
     * Двухбуквенный код языка ISO 639-1. Если указан, команды будут применяться
     * только к пользователям с этим языком.
     */
    @JsonProperty("language_code")
    private String languageCode;

    @Override
    public String getMethod() {
        return PATH;
    }

    @Override
    public void validate() throws TelegramApiValidationException {
        if (commands == null) {
            throw new TelegramApiValidationException("Commands list cannot be null", PATH, "commands");
        }
        if (commands.size() > 100) {
             throw new TelegramApiValidationException("Commands list cannot exceed 100 items", PATH, "commands");
        }
        for (BotCommand cmd : commands) {
            cmd.validate();
        }
    }
    
    /**
     * Добавляет одну команду в список.
     * @param command текст команды (без /).
     * @param description описание.
     * @return this.
     */
    public SetMyCommands addCommand(String command, String description) {
        if (this.commands == null) {
            this.commands = new ArrayList<>();
        }
        this.commands.add(new BotCommand(command, description));
        return this;
    }

    /**
     * Устанавливает область видимости команд (Scope).
     *
     * @param scope Объект области видимости (например, {@link com.kaleert.nyagram.api.objects.commands.scope.BotCommandScopeDefault}).
     * @return текущий билдер.
     */
    public SetMyCommands scope(BotCommandScope scope) {
        this.scope = scope;
        return this;
    }

    /**
     * Устанавливает язык, для которого будут применяться команды.
     *
     * @param languageCode Двухбуквенный код языка (ISO 639-1), например "ru" или "en".
     * @return текущий билдер.
     */
    public SetMyCommands lang(String languageCode) {
        this.languageCode = languageCode;
        return this;
    }
    
    /**
     * Создает запрос на полную очистку списка команд бота.
     *
     * @return объект запроса с пустым списком команд.
     */
    public static SetMyCommands clear() {
        return SetMyCommands.builder()
                .commands(new ArrayList<>())
                .build();
    }
}