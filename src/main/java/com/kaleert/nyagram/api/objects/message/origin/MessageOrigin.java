package com.kaleert.nyagram.api.objects.message.origin;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonSubTypes;
import com.fasterxml.jackson.annotation.JsonTypeInfo;
import com.kaleert.nyagram.api.meta.BotApiObject;
import com.kaleert.nyagram.api.objects.User;
import com.kaleert.nyagram.api.objects.chat.Chat;

/**
 * Описывает источник сообщения (откуда оно было переслано).
 *
 * @since 1.0.0
 */
@JsonTypeInfo(
    use = JsonTypeInfo.Id.NAME,
    include = JsonTypeInfo.As.PROPERTY,
    property = "type",
    defaultImpl = UnknownMessageOrigin.class 
)
@JsonSubTypes({
    @JsonSubTypes.Type(value = MessageOriginUser.class, name = "user"),
    @JsonSubTypes.Type(value = MessageOriginHiddenUser.class, name = "hidden_user"),
    @JsonSubTypes.Type(value = MessageOriginChat.class, name = "chat"),
    @JsonSubTypes.Type(value = MessageOriginChannel.class, name = "channel")
})
public sealed interface MessageOrigin extends BotApiObject 
    permits MessageOriginUser, MessageOriginHiddenUser, MessageOriginChat, MessageOriginChannel, UnknownMessageOrigin {
    
    /** Тип источника. */
    String getType();
    
    /** Дата отправки оригинального сообщения (Unix timestamp). */
    Integer getDate();
}

/**
 * Сообщение было переслано от известного пользователя.
 * @param date Дата отправки.
 * @param senderUser Пользователь, отправивший сообщение.
 */
@JsonIgnoreProperties(ignoreUnknown = true)
record MessageOriginUser(Integer date, User senderUser) implements MessageOrigin {
    @Override public String getType() { return "user"; }
    @Override public Integer getDate() { return date(); }
}

/**
 * Сообщение было переслано от пользователя, который скрыл свой аккаунт при пересылке.
 * @param date Дата отправки.
 * @param senderUserName Имя пользователя (как оно отображается).
 */
@JsonIgnoreProperties(ignoreUnknown = true)
record MessageOriginHiddenUser(Integer date, String senderUserName) implements MessageOrigin {
    @Override public String getType() { return "hidden_user"; }
    @Override public Integer getDate() { return date(); }
}

/**
 * Сообщение было переслано из чата (группы или супергруппы).
 * @param date Дата отправки.
 * @param senderChat Чат-отправитель.
 * @param authorSignature Подпись автора (если есть).
 */
@JsonIgnoreProperties(ignoreUnknown = true)
record MessageOriginChat(Integer date, Chat senderChat, String authorSignature) implements MessageOrigin {
    @Override public String getType() { return "chat"; }
    @Override public Integer getDate() { return date(); }
}

/**
 * Сообщение было переслано из канала.
 * @param date Дата отправки.
 * @param chat Канал-отправитель.
 * @param messageId ID сообщения в канале.
 * @param authorSignature Подпись автора (если есть).
 */
@JsonIgnoreProperties(ignoreUnknown = true)
record MessageOriginChannel(Integer date, Chat chat, Integer messageId, String authorSignature) implements MessageOrigin {
    @Override public String getType() { return "channel"; }
    @Override public Integer getDate() { return date(); }
}

/**
 * Неизвестный тип источника (резерв на случай обновления API).
 * @param date Дата отправки.
 */
@JsonIgnoreProperties(ignoreUnknown = true)
record UnknownMessageOrigin(Integer date) implements MessageOrigin {
    @Override public String getType() { return "unknown"; }
    @Override public Integer getDate() { return date(); }
}