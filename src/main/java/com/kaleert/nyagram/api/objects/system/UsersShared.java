package com.kaleert.nyagram.api.objects.system;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.kaleert.nyagram.api.meta.BotApiObject;
import java.util.List;

/**
 * Содержит информацию о пользователях, которыми текущий пользователь поделился с ботом.
 * <p>
 * Приходит в ответ на нажатие кнопки {@link com.kaleert.nyagram.api.objects.replykeyboard.buttons.KeyboardButtonRequestUser}.
 * </p>
 *
 * @param requestId Идентификатор запроса (совпадает с тем, что был в кнопке).
 * @param userIds Список идентификаторов выбранных пользователей.
 *
 * @since 1.0.0
 */
@JsonIgnoreProperties(ignoreUnknown = true)
public record UsersShared(
    @JsonProperty("request_id") Integer requestId,
    @JsonProperty("user_ids") List<Long> userIds
) implements BotApiObject {}