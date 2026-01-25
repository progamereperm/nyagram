package com.kaleert.nyagram.api.objects;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.kaleert.nyagram.api.meta.BotApiObject;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * Представляет один размер фотографии или превью файла (thumbnail).
 *
 * @since 1.0.0
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@JsonIgnoreProperties(ignoreUnknown = true)
public class PhotoSize implements BotApiObject {

    /**
     * Идентификатор файла. Можно использовать для скачивания.
     */
    @JsonProperty("file_id")
    private String fileId;

    /**
     * Уникальный идентификатор файла (нельзя использовать для скачивания, но он постоянен).
     */
    @JsonProperty("file_unique_id")
    private String fileUniqueId;

    /**
     * Ширина фото.
     */
    @JsonProperty("width")
    private Integer width;

    /**
     * Высота фото.
     */
    @JsonProperty("height")
    private Integer height;

    /**
     * Размер файла в байтах.
     */
    @JsonProperty("file_size")
    private Integer fileSize;
}