package com.kaleert.nyagram.api.objects;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.kaleert.nyagram.api.meta.BotApiObject;

/**
 * Представляет файл общего назначения (в отличие от фото, видео и т.д.).
 *
 * @param fileId Идентификатор файла.
 * @param fileUniqueId Уникальный идентификатор файла (нельзя использовать для скачивания).
 * @param thumbnail Превью документа (если есть).
 * @param fileName Имя оригинального файла.
 * @param mimeType MIME-тип файла.
 * @param fileSize Размер файла в байтах.
 *
 * @since 1.0.0
 */
@JsonIgnoreProperties(ignoreUnknown = true)
public record Document(
    @JsonProperty("file_id") String fileId,
    @JsonProperty("file_unique_id") String fileUniqueId,
    @JsonProperty("thumbnail") PhotoSize thumbnail,
    @JsonProperty("file_name") String fileName,
    @JsonProperty("mime_type") String mimeType,
    @JsonProperty("file_size") Long fileSize
) implements BotApiObject {

    /**
     * Проверяет, является ли файл архивом (zip, rar, 7z).
     * @return true, если расширение файла соответствует архиву.
     */
    @JsonIgnore
    public boolean isArchive() {
        return fileName != null && (fileName.endsWith(".zip") || fileName.endsWith(".rar") || fileName.endsWith(".7z"));
    }

    /**
     * Проверяет, является ли файл PDF-документом.
     * @return true, если mime-type или расширение указывают на PDF.
     */
    @JsonIgnore
    public boolean isPdf() {
        return "application/pdf".equals(mimeType) || (fileName != null && fileName.endsWith(".pdf"));
    }
    
    /**
     * Проверяет, является ли документ изображением (отправленным без сжатия).
     * @return true, если mime-type начинается с "image/".
     */
    @JsonIgnore
    public boolean isImage() {
        return mimeType != null && mimeType.startsWith("image/");
    }
}