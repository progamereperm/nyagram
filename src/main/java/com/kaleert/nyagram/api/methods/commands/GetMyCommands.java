package com.kaleert.nyagram.api.methods.commands;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.kaleert.nyagram.api.exception.TelegramApiRequestException;
import com.kaleert.nyagram.api.meta.BotApiMethod;
import com.kaleert.nyagram.api.objects.commands.BotCommand;
import com.kaleert.nyagram.api.objects.commands.scope.BotCommandScope;
import lombok.*;

import java.util.List;

/**
 * Используйте этот метод для получения текущего списка команд бота.
 * <p>
 * Возвращает список объектов {@link BotCommand}.
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
public class GetMyCommands extends BotApiMethod<List<BotCommand>> {

    /** Имя метода в Telegram Bot API. */
    public static final String PATH = "getMyCommands";
    
    /**
     * Область действия, для которой запрашиваются команды.
     * По умолчанию ScopeDefault.
     */
    @JsonProperty("scope")
    private BotCommandScope scope;
    
    /**
     * Двухбуквенный код языка ISO 639-1 или пустая строка.
     */
    @JsonProperty("language_code")
    private String languageCode;

    @Override
    public String getMethod() {
        return PATH;
    }

    @Override
    public List<BotCommand> deserializeResponse(String answer) throws TelegramApiRequestException {
        return deserializeResponseArray(answer, BotCommand.class);
    }
    
    @Override
    public void validate() {
    }
}