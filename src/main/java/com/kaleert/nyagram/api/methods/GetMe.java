package com.kaleert.nyagram.api.methods;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.kaleert.nyagram.api.exception.TelegramApiRequestException;
import com.kaleert.nyagram.api.meta.BotApiMethod;
import com.kaleert.nyagram.api.objects.User;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

/**
 * Простой метод для тестирования токена аутентификации вашего бота.
 * <p>
 * Не требует параметров. Возвращает основную информацию о боте в виде объекта {@link User}.
 * </p>
 *
 * @since 1.0.0
 */
@NoArgsConstructor
@EqualsAndHashCode(callSuper = false)
@JsonIgnoreProperties(ignoreUnknown = true)
@JsonInclude(JsonInclude.Include.NON_NULL)
public class GetMe extends BotApiMethod<User> {

    /** Имя метода в Telegram Bot API. */
    public static final String PATH = "getMe";
    
    /** Статический экземпляр для удобства использования. */
    public static final GetMe INSTANCE = new GetMe();

    @Override
    public String getMethod() {
        return PATH;
    }

    @Override
    public User deserializeResponse(String answer) throws TelegramApiRequestException {
        return deserializeResponse(answer, User.class);
    }
    
    @Override
    public void validate() {
    }
}