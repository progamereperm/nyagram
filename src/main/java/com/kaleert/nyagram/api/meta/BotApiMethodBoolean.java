package com.kaleert.nyagram.api.meta;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.kaleert.nyagram.api.exception.TelegramApiRequestException;
import lombok.AccessLevel;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;

/**
 * Удобный базовый класс для методов API, которые возвращают {@code Boolean} (true/false).
 * <p>
 * Автоматически обрабатывает десериализацию ответа в булево значение.
 * </p>
 *
 * @since 1.0.0
 */
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@SuperBuilder
@JsonIgnoreProperties(ignoreUnknown = true)
@JsonInclude(JsonInclude.Include.NON_NULL)
public abstract class BotApiMethodBoolean extends BotApiMethod<Boolean> {
    
    @Override
    public Boolean deserializeResponse(String answer) throws TelegramApiRequestException {
        return deserializeResponse(answer, Boolean.class);
    }
}