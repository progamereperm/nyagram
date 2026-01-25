package com.kaleert.nyagram.api.objects;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.kaleert.nyagram.api.meta.BotApiObject;
import com.kaleert.nyagram.api.meta.Validable;
import com.kaleert.nyagram.api.exception.TelegramApiValidationException;

/**
 * Представляет параметр inline-кнопки, используемый для автоматической авторизации пользователя.
 * <p>
 * Служит заменой Telegram Login Widget при использовании в inline-клавиатурах.
 * При нажатии на кнопку откроется HTTPS URL вашего сайта, и вы получите данные пользователя.
 * </p>
 *
 * @param url HTTPS URL, который будет открыт с данными авторизации пользователя.
 * @param forwardText Текст кнопки при пересылке сообщения (опционально).
 * @param botUsername Юзернейм бота, который будет использоваться для авторизации (если отличается от текущего).
 * @param requestWriteAccess Если true, запрашивает разрешение на отправку сообщений пользователю.
 *
 * @since 1.0.0
 */
@JsonInclude(JsonInclude.Include.NON_NULL)
@JsonIgnoreProperties(ignoreUnknown = true)
public record LoginUrl(
    @JsonProperty("url") String url,
    @JsonProperty("forward_text") String forwardText,
    @JsonProperty("bot_username") String botUsername,
    @JsonProperty("request_write_access") Boolean requestWriteAccess
) implements BotApiObject, Validable {

    @Override
    public void validate() throws TelegramApiValidationException {
        if (url == null || url.isBlank()) {
            throw new TelegramApiValidationException("LoginUrl URL cannot be empty", "LoginUrl");
        }
    }
}