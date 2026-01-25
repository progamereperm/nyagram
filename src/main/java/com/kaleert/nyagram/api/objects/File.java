package com.kaleert.nyagram.api.objects;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.kaleert.nyagram.api.meta.BotApiObject;

/**
 * Этот объект представляет файл, готовый к скачиванию.
 * Файл можно скачать по ссылке, которую формирует метод {@link #getDownloadUrl(String)}.
 *
 * @param fileId Идентификатор файла.
 * @param fileUniqueId Уникальный идентификатор файла.
 * @param fileSize Размер файла в байтах.
 * @param filePath Путь к файлу. Используйте его для скачивания: {@code https://api.telegram.org/file/bot<token>/<file_path>}.
 *
 * @since 1.0.0
 */
@JsonIgnoreProperties(ignoreUnknown = true)
public record File(
    @JsonProperty("file_id") String fileId,
    @JsonProperty("file_unique_id") String fileUniqueId,
    @JsonProperty("file_size") Long fileSize,
    @JsonProperty("file_path") String filePath
) implements BotApiObject {

    /**
     * Формирует полную ссылку для скачивания файла.
     *
     * @param botToken Токен вашего бота.
     * @return HTTPS ссылка на скачивание.
     * @throws IllegalStateException Если filePath равен null (файл недоступен или слишком велик).
     */
    public String getDownloadUrl(String botToken) {
        if (filePath == null) {
            throw new IllegalStateException("FilePath is null. You must call getFile first.");
        }
        return String.format("https://api.telegram.org/file/bot%s/%s", botToken, filePath);
    }
}