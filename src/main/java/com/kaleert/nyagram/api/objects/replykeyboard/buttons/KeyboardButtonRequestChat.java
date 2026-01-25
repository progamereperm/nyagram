package com.kaleert.nyagram.api.objects.replykeyboard.buttons;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.kaleert.nyagram.api.meta.BotApiObject;
import com.kaleert.nyagram.api.objects.ChatPermissions;

/**
 * Определяет критерии для запроса чата при нажатии на {@link KeyboardButton}.
 *
 * @param requestId Уникальный идентификатор запроса (будет возвращен в ChatShared).
 * @param chatIsChannel True - запрашивать каналы, False - группы.
 * @param chatIsForum True - запрашивать форумы (супергруппы с топиками).
 * @param chatHasUsername True - запрашивать только чаты с юзернеймом.
 * @param chatIsCreated True - запрашивать чаты, созданные пользователем.
 * @param userAdministratorRights Обязательные права админа у пользователя в этом чате.
 * @param botAdministratorRights Обязательные права админа у бота в этом чате.
 * @param botIsMember True - бот должен быть участником чата.
 *
 * @since 1.0.0
 */
public record KeyboardButtonRequestChat(
    @JsonProperty("request_id") Integer requestId,
    @JsonProperty("chat_is_channel") Boolean chatIsChannel,
    @JsonProperty("chat_is_forum") Boolean chatIsForum,
    @JsonProperty("chat_has_username") Boolean chatHasUsername,
    @JsonProperty("chat_is_created") Boolean chatIsCreated,
    @JsonProperty("user_administrator_rights") ChatPermissions userAdministratorRights,
    @JsonProperty("bot_administrator_rights") ChatPermissions botAdministratorRights,
    @JsonProperty("bot_is_member") Boolean botIsMember
) implements BotApiObject {}