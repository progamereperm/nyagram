package com.kaleert.nyagram.api.meta;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.JavaType;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.kaleert.nyagram.api.exception.TelegramApiRequestException;
import com.kaleert.nyagram.api.objects.ApiResponse;
import com.kaleert.nyagram.api.utils.NyagramJson;
import lombok.AccessLevel;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;

import java.io.IOException;
import java.io.Serializable;
import java.util.List;

/**
 * Базовый абстрактный класс для всех методов API.
 * <p>
 * Содержит логику сериализации запросов и десериализации ответов с помощью Jackson.
 * Обрабатывает как успешные ответы, так и ошибки API, выбрасывая исключения.
 * </p>
 *
 * @param <T> Тип возвращаемого значения метода (например, {@code Message} или {@code Boolean}).
 * @since 1.0.0
 */
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@SuperBuilder
@JsonIgnoreProperties(ignoreUnknown = true)
@JsonInclude(JsonInclude.Include.NON_NULL)
public abstract class PartialBotApiMethod<T> implements Validable, BotApiObject {

    @JsonIgnore
    protected static final ObjectMapper OBJECT_MAPPER = NyagramJson.getMapper();
    
    /**
     * Десериализует JSON-ответ от Telegram API в объект результата.
     *
     * @param answer Строка JSON-ответа.
     * @return Объект результата типа T.
     * @throws TelegramApiRequestException Если API вернул ошибку (ok=false).
     */
    public abstract T deserializeResponse(String answer) throws TelegramApiRequestException;

    protected T deserializeResponseArray(String answer, Class<?> contentClass) throws TelegramApiRequestException {
        try {
            JavaType contentType = OBJECT_MAPPER.getTypeFactory().constructType(contentClass);
            JavaType listType = OBJECT_MAPPER.getTypeFactory().constructCollectionType(List.class, contentType);
            JavaType responseType = OBJECT_MAPPER.getTypeFactory().constructParametricType(ApiResponse.class, listType);

            ApiResponse<T> result = OBJECT_MAPPER.readValue(answer, responseType);

            if (Boolean.TRUE.equals(result.getOk())) {
                return result.getResult();
            } else {
                throw new TelegramApiRequestException(
                        "Error executing method " + this.getMethod(),
                        answer,
                        result.getErrorCode(),
                        result.getDescription(),
                        result.getParameters()
                );
            }
        } catch (IOException e) {
            throw new TelegramApiRequestException("Unable to deserialize list response: " + e.getMessage(), e);
        }
    }

    protected <R> R deserializeResponse(String answer, Class<R> returnClass) throws TelegramApiRequestException {
        JavaType type = OBJECT_MAPPER.getTypeFactory().constructType(returnClass);
        return deserializeResponseInternal(answer, type);
    }

    protected <R> R deserializeResponse(String answer, TypeReference<R> typeReference) throws TelegramApiRequestException {
        JavaType type = OBJECT_MAPPER.getTypeFactory().constructType(typeReference);
        return deserializeResponseInternal(answer, type);
    }

    private <R> R deserializeResponseInternal(String answer, JavaType type) throws TelegramApiRequestException {
        try {
            JavaType responseType = OBJECT_MAPPER.getTypeFactory()
                    .constructParametricType(ApiResponse.class, type);
            
            ApiResponse<R> result = OBJECT_MAPPER.readValue(answer, responseType);

            if (Boolean.TRUE.equals(result.getOk())) {
                return result.getResult();
            } else {
                throw new TelegramApiRequestException(
                        "Error executing method " + this.getMethod(),
                        answer,
                        result.getErrorCode(),
                        result.getDescription(),
                        result.getParameters()
                );
            }
        } catch (IOException e) {
            throw new TelegramApiRequestException(
                    "Unable to deserialize response: " + e.getMessage(),
                    e
            );
        }
    }
    
    /**
     * Возвращает название метода API (например, "sendMessage").
     *
     * @return строковое имя метода.
     */
    @JsonIgnore
    public abstract String getMethod();
    
    /**
     * Возвращает класс, используемый для десериализации ответа.
     * <p>
     * По умолчанию возвращает {@code null}. Может быть переопределен в подклассах,
     * чтобы указать конкретный тип {@code Class<T>} для Jackson, если стандартной
     * десериализации через дженерики недостаточно.
     * </p>
     *
     * @return Класс типа ответа или null.
     */
    @JsonIgnore
    public Class<T> getDeserializeResponseClass() {
        return null;
    }
}