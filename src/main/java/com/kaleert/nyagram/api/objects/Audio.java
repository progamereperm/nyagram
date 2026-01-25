package com.kaleert.nyagram.api.objects;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.kaleert.nyagram.api.meta.BotApiObject;

/**
 * Этот объект представляет аудиофайл, который должен восприниматься как музыка
 * (встроенный аудиоплеер).
 *
 * @param fileId Идентификатор файла.
 * @param fileUniqueId Уникальный идентификатор файла.
 * @param duration Длительность в секундах.
 * @param performer Исполнитель (из метаданных).
 * @param title Название трека (из метаданных).
 * @param fileName Имя файла.
 * @param mimeType MIME-тип файла (например, audio/mpeg).
 * @param fileSize Размер файла.
 * @param thumbnail Обложка альбома.
 * 
 * @since 1.0.0
 */
@JsonIgnoreProperties(ignoreUnknown = true)
public record Audio(
    @JsonProperty("file_id") String fileId,
    @JsonProperty("file_unique_id") String fileUniqueId,
    @JsonProperty("duration") Integer duration,
    @JsonProperty("performer") String performer,
    @JsonProperty("title") String title,
    @JsonProperty("file_name") String fileName,
    @JsonProperty("mime_type") String mimeType,
    @JsonProperty("file_size") Long fileSize,
    @JsonProperty("thumbnail") PhotoSize thumbnail
) implements BotApiObject {
    
    /**
     * Формирует отображаемое имя трека.
     * <p>
     * Если есть метаданные исполнителя и названия, возвращает "Исполнитель - Название".
     * Иначе возвращает имя файла.
     * </p>
     *
     * @return строка с названием.
     */
    @JsonIgnore
    public String getDisplayName() {
        if (performer != null && title != null) {
            return performer + " - " + title;
        }
        return fileName != null ? fileName : "Audio";
    }
    
    /**
     * Проверяет, является ли файл MP3-треком.
     *
     * @return true, если MIME-тип "audio/mpeg" или расширение файла ".mp3".
     */
    @JsonIgnore
    public boolean isMp3() {
        return "audio/mpeg".equalsIgnoreCase(mimeType) || 
               (fileName != null && fileName.toLowerCase().endsWith(".mp3"));
    }
    
    /**
     * Возвращает длительность трека в формате "MM:SS".
     *
     * @return отформатированная строка времени.
     */
    @JsonIgnore
    public String getFormattedDuration() {
        if (duration == null) return "0:00";
        int minutes = duration / 60;
        int seconds = duration % 60;
        return String.format("%d:%02d", minutes, seconds);
    }
}