package com.kaleert.nyagram.api.objects.replykeyboard.buttons;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.kaleert.nyagram.api.meta.BotApiObject;

/**
 * Определяет критерии для запроса пользователя при нажатии на {@link KeyboardButton}.
 *
 * @param requestId Уникальный идентификатор запроса (будет возвращен в UsersShared).
 * @param userIsBot True - запрашивать ботов, False - людей. Null - не важно.
 * @param userIsPremium True - запрашивать только Premium пользователей.
 *
 * @since 1.0.0
 */
public record KeyboardButtonRequestUser(
    @JsonProperty("request_id") Integer requestId,
    @JsonProperty("user_is_bot") Boolean userIsBot,
    @JsonProperty("user_is_premium") Boolean userIsPremium
) implements BotApiObject {}