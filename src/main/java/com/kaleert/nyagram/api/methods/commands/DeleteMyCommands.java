package com.kaleert.nyagram.api.methods.commands;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.kaleert.nyagram.api.meta.BotApiMethodBoolean;
import com.kaleert.nyagram.api.objects.commands.scope.BotCommandScope;
import lombok.*;

/**
 * Используйте этот метод для удаления списка команд бота.
 * <p>
 * После удаления пользователям будет отображаться дефолтный список команд (если он есть)
 * или пустое меню.
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
public class DeleteMyCommands extends BotApiMethodBoolean {

    /** Имя метода в Telegram Bot API. */
    public static final String PATH = "deleteMyCommands";
    
    /**
     * Объект, описывающий область действия, для которой удаляются команды.
     * По умолчанию команды удаляются для всех пользователей (ScopeDefault).
     */
    @JsonProperty("scope")
    private BotCommandScope scope;
    
    /**
     * Двухбуквенный код языка ISO 639-1.
     * Если указан, команды будут удалены только для пользователей с этим языком.
     */
    @JsonProperty("language_code")
    private String languageCode;

    @Override
    public String getMethod() {
        return PATH;
    }
    
    @Override
    public void validate() {
    }
}