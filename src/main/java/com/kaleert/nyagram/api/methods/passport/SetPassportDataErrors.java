package com.kaleert.nyagram.api.methods.passport;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.kaleert.nyagram.api.exception.TelegramApiValidationException;
import com.kaleert.nyagram.api.meta.BotApiMethodBoolean;
import com.kaleert.nyagram.api.objects.passport.PassportElementError;
import lombok.*;

import java.util.List;

/**
 * Используйте этот метод, чтобы сообщить пользователю, что некоторые данные Telegram Passport содержат ошибки.
 * <p>
 * Пользователь не сможет использовать эти данные, пока не исправит их.
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
public class SetPassportDataErrors extends BotApiMethodBoolean {
    
    /** Имя метода в Telegram Bot API. */
    public static final String PATH = "setPassportDataErrors";

    /**
     * Уникальный идентификатор пользователя.
     */
    @JsonProperty("user_id")
    private Long userId;

    /**
     * Список ошибок (массив JSON-объектов).
     */
    @JsonProperty("errors")
    private List<PassportElementError> errors;

    @Override
    public String getMethod() {
        return PATH;
    }

    @Override
    public void validate() throws TelegramApiValidationException {
        if (userId == null) throw new TelegramApiValidationException("UserId required", PATH, "user_id");
        if (errors == null || errors.isEmpty()) {
            throw new TelegramApiValidationException("Errors list required", PATH, "errors");
        }
    }
}