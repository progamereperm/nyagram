package com.kaleert.nyagram.api.objects.passport;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonSubTypes;
import com.fasterxml.jackson.annotation.JsonTypeInfo;
import com.kaleert.nyagram.api.meta.BotApiObject;
import com.kaleert.nyagram.api.meta.Validable;
import lombok.Builder;
import lombok.NonNull;

/**
 * Базовый интерфейс для ошибок в данных Telegram Passport.
 * <p>
 * Используется в методе {@code setPassportDataErrors}, чтобы сообщить пользователю,
 * что его документы не прошли проверку.
 * </p>
 *
 * @since 1.0.0
 */
@JsonTypeInfo(use = JsonTypeInfo.Id.NAME, property = "source")
@JsonSubTypes({
    @JsonSubTypes.Type(value = PassportElementErrorDataField.class, name = "data"),
    @JsonSubTypes.Type(value = PassportElementErrorUnspecified.class, name = "unspecified")
})
public sealed interface PassportElementError extends BotApiObject, Validable 
    permits PassportElementErrorDataField, PassportElementErrorUnspecified {
}

/**
 * Ошибка в конкретном поле данных (например, неверное имя или дата рождения).
 *
 * @param type Тип элемента (personal_details, passport, etc.).
 * @param fieldName Имя поля, в котором ошибка.
 * @param dataHash Хэш данных (base64).
 * @param message Сообщение об ошибке для пользователя.
 */
@Builder
@JsonInclude(JsonInclude.Include.NON_NULL)
record PassportElementErrorDataField(
    @JsonProperty("type") @NonNull String type,
    @JsonProperty("field_name") @NonNull String fieldName,
    @JsonProperty("data_hash") @NonNull String dataHash,
    @JsonProperty("message") @NonNull String message
) implements PassportElementError {
    @Override public void validate() {}
}

/**
 * Общая ошибка в элементе (например, плохое качество скана).
 *
 * @param type Тип элемента.
 * @param elementHash Хэш элемента.
 * @param message Сообщение об ошибке.
 */
@Builder
@JsonInclude(JsonInclude.Include.NON_NULL)
record PassportElementErrorUnspecified(
    @JsonProperty("type") @NonNull String type,
    @JsonProperty("element_hash") @NonNull String elementHash,
    @JsonProperty("message") @NonNull String message
) implements PassportElementError {
    @Override public void validate() {}
}