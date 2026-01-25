package com.kaleert.nyagram.api.objects.passport;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.kaleert.nyagram.api.meta.BotApiObject;
import java.util.List;

/**
 * Содержит информацию о данных Telegram Passport, переданных пользователем.
 *
 * @param data Список зашифрованных элементов (документов).
 * @param credentials Зашифрованные данные для расшифровки документов.
 *
 * @since 1.0.0
 */
@JsonIgnoreProperties(ignoreUnknown = true)
public record PassportData(
    @JsonProperty("data") List<EncryptedPassportElement> data,
    @JsonProperty("credentials") EncryptedCredentials credentials
) implements BotApiObject {}

/**
 * Описывает зашифрованный элемент Telegram Passport (паспорт, права, адрес и т.д.).
 * <p>
 * Эти данные зашифрованы публичным ключом бота и могут быть расшифрованы только приватным ключом.
 * </p>
 *
 * @param type Тип элемента (personal_details, passport, driver_license, etc.).
 * @param data Зашифрованные данные (Base64), содержащие JSON с деталями.
 * @param phoneNumber Номер телефона (если type="phone_number").
 * @param email Email (если type="email").
 * @param files Список зашифрованных файлов (сканов).
 * @param frontSide Лицевая сторона документа.
 * @param reverseSide Обратная сторона документа.
 * @param selfie Селфи с документом.
 * @param translation Перевод документов.
 * @param hash Хэш данных.
 *
 * @since 1.0.0
 */
@JsonIgnoreProperties(ignoreUnknown = true)
record EncryptedPassportElement(
    @JsonProperty("type") String type,
    @JsonProperty("data") String data,
    @JsonProperty("phone_number") String phoneNumber,
    @JsonProperty("email") String email,
    @JsonProperty("files") List<PassportFile> files,
    @JsonProperty("front_side") PassportFile frontSide,
    @JsonProperty("reverse_side") PassportFile reverseSide,
    @JsonProperty("selfie") PassportFile selfie,
    @JsonProperty("translation") List<PassportFile> translation,
    @JsonProperty("hash") String hash
) implements BotApiObject {}

/**
 * Содержит данные, необходимые для расшифровки зашифрованных элементов.
 *
 * @param data Зашифрованные данные с уникальной "солью" и секретом.
 * @param hash Хэш данных.
 * @param secret Зашифрованный секрет (Base64).
 *
 * @since 1.0.0
 */
@JsonIgnoreProperties(ignoreUnknown = true)
record EncryptedCredentials(
    @JsonProperty("data") String data,
    @JsonProperty("hash") String hash,
    @JsonProperty("secret") String secret
) implements BotApiObject {}

/**
 * Представляет файл, загруженный в Telegram Passport.
 * <p>
 * Это может быть скан документа, селфи, перевод и т.д.
 * </p>
 *
 * @param fileId Идентификатор файла. Можно использовать для скачивания.
 * @param fileUniqueId Уникальный идентификатор файла (постоянен во времени).
 * @param fileSize Размер файла в байтах.
 * @param fileDate Время загрузки файла (Unix timestamp).
 *
 * @since 1.0.0
 */
@JsonIgnoreProperties(ignoreUnknown = true)
record PassportFile(
    @JsonProperty("file_id") String fileId,
    @JsonProperty("file_unique_id") String fileUniqueId,
    @JsonProperty("file_size") Integer fileSize,
    @JsonProperty("file_date") Integer fileDate
) implements BotApiObject {}