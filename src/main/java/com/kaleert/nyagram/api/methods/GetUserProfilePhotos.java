package com.kaleert.nyagram.api.methods;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.kaleert.nyagram.api.exception.TelegramApiRequestException;
import com.kaleert.nyagram.api.meta.BotApiMethod;
import com.kaleert.nyagram.api.objects.UserProfilePhotos;
import lombok.*;

/**
 * Используйте этот метод для получения списка фотографий профиля пользователя.
 * <p>
 * Возвращает объект {@link UserProfilePhotos}.
 * </p>
 *
 * @since 1.0.0
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode(callSuper = false)
public class GetUserProfilePhotos extends BotApiMethod<UserProfilePhotos> {
    
    /** Имя метода в Telegram Bot API. */
    public static final String PATH = "getUserProfilePhotos";

    /**
     * Уникальный идентификатор целевого пользователя.
     */
    @JsonProperty("user_id") 
    private Long userId;
    
    /**
     * Норядковый номер первой возвращаемой фотографии. По умолчанию 0.
     */
    @JsonProperty("offset") 
    private Integer offset;
    
    /**
     * Ограничение количества получаемых фотографий. Значения от 1 до 100. По умолчанию 100.
     */
    @JsonProperty("limit") 
    private Integer limit;

    @Override public String getMethod() { return PATH; }

    @Override
    public UserProfilePhotos deserializeResponse(String answer) throws TelegramApiRequestException {
        return deserializeResponse(answer, UserProfilePhotos.class);
    }
    
    @Override public void validate() { 
        if (userId == null) throw new IllegalArgumentException("UserId required");
    }
    
    /**
     * Создает запрос на получение фото профиля.
     * @param userId ID пользователя.
     * @return объект запроса.
     */
    public static GetUserProfilePhotos of(Long userId) {
        return GetUserProfilePhotos.builder().userId(userId).build();
    }
}