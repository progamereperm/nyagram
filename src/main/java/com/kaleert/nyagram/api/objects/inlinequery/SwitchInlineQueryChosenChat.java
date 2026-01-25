package com.kaleert.nyagram.api.objects.inlinequery;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.kaleert.nyagram.api.meta.BotApiObject;

/**
 * Представляет inline-кнопку, которая переключает пользователя в inline-режим в выбранном чате.
 * <p>
 * Пользователю будет предложено выбрать чат определенного типа, после чего в поле ввода
 * этого чата будет вставлено имя бота и указанный запрос.
 * </p>
 *
 * @param query Запрос, который будет вставлен в поле ввода.
 * @param allowUserChats True, если можно выбрать личные чаты.
 * @param allowBotChats True, если можно выбрать чаты с ботами.
 * @param allowGroupChats True, если можно выбрать группы и супергруппы.
 * @param allowChannelChats True, если можно выбрать каналы.
 *
 * @since 1.0.0
 */
@JsonInclude(JsonInclude.Include.NON_NULL)
@JsonIgnoreProperties(ignoreUnknown = true)
public record SwitchInlineQueryChosenChat(
    @JsonProperty("query") String query,
    @JsonProperty("allow_user_chats") Boolean allowUserChats,
    @JsonProperty("allow_bot_chats") Boolean allowBotChats,
    @JsonProperty("allow_group_chats") Boolean allowGroupChats,
    @JsonProperty("allow_channel_chats") Boolean allowChannelChats
) implements BotApiObject {
    
    /**
     * Создает пустой объект (без ограничений по типам чатов).
     * @return объект SwitchInlineQueryChosenChat.
     */
    public static SwitchInlineQueryChosenChat empty() {
        return new SwitchInlineQueryChosenChat(null, null, null, null, null);
    }
}