package com.kaleert.nyagram.api.objects;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.kaleert.nyagram.api.meta.BotApiObject;
import com.kaleert.nyagram.api.objects.PhotoSize;
import java.util.List;

/**
 * Представляет список фотографий профиля пользователя.
 *
 * @param totalCount Общее количество фотографий профиля у пользователя (может быть больше, чем возвращено в запросе).
 * @param photos Запрошенные фотографии профиля (каждая фото представлена массивом размеров).
 *
 * @see com.kaleert.nyagram.api.methods.GetUserProfilePhotos
 * @since 1.0.0
 */
public record UserProfilePhotos(
    @JsonProperty("total_count") Integer totalCount,
    @JsonProperty("photos") List<List<PhotoSize>> photos
) implements BotApiObject {}