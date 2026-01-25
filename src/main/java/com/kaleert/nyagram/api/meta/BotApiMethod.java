package com.kaleert.nyagram.api.meta;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AccessLevel;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;

import java.io.Serializable;

/**
 * Абстрактный базовый класс для всех методов Telegram Bot API.
 * <p>
 * Обеспечивает сериализацию имени метода в поле JSON {@code method}.
 * </p>
 *
 * @param <T> Тип возвращаемого значения (результата выполнения метода).
 * @since 1.0.0
 */
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@SuperBuilder
@JsonIgnoreProperties(ignoreUnknown = true)
@JsonInclude(JsonInclude.Include.NON_NULL)
public abstract class BotApiMethod<T> extends PartialBotApiMethod<T> {
    
    /**
     * Имя поля в JSON-объекте, которое будет содержать название метода API (например, "sendMessage").
     * Используется при сериализации.
     */
    public static final String METHOD_FIELD = "method";
    
    /**
     * Возвращает имя метода для сериализации в JSON.
     * <p>
     * Используется библиотекой Jackson через аннотацию {@code @JsonProperty("method")}.
     * Это необходимо, так как поле {@code method} в JSON-теле запроса (например, при webhook reply)
     * должно содержать строковое название метода API (например, "sendMessage").
     * </p>
     *
     * @return Имя метода API.
     */
    @JsonProperty(METHOD_FIELD)
    public String getMethodNameForSerialization() {
        return getMethod();
    }
}