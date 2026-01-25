/* nyagram/src/main/java/com/kaleert/nyagram/api/objects/ChatPhoto.java */

package com.kaleert.nyagram.api.objects;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.kaleert.nyagram.api.meta.BotApiObject;

/**
 * Представляет фотографию профиля чата.
 *
 * @param smallFileId Идентификатор файла маленького (160x160) фото чата.
 *                    Можно использовать для скачивания через {@code getFile}.
 * @param smallFileUniqueId Уникальный идентификатор маленького фото (нельзя использовать для скачивания).
 * @param bigFileId Идентификатор файла большого (640x640) фото чата.
 *                  Можно использовать для скачивания через {@code getFile}.
 * @param bigFileUniqueId Уникальный идентификатор большого фото.
 *
 * @since 1.0.0
 */
@JsonIgnoreProperties(ignoreUnknown = true)
public record ChatPhoto(
    @JsonProperty("small_file_id") String smallFileId,
    @JsonProperty("small_file_unique_id") String smallFileUniqueId,
    @JsonProperty("big_file_id") String bigFileId,
    @JsonProperty("big_file_unique_id") String bigFileUniqueId
) implements BotApiObject {}