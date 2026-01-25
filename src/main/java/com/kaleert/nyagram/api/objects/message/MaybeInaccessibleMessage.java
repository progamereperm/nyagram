package com.kaleert.nyagram.api.objects.message;

import com.fasterxml.jackson.annotation.JsonSubTypes;
import com.fasterxml.jackson.annotation.JsonTypeInfo;
import com.kaleert.nyagram.api.meta.BotApiObject;
import com.kaleert.nyagram.api.objects.chat.Chat;

/**
 * Этот интерфейс описывает сообщение, которое может быть недоступно боту.
 * <p>
 * Используется в объектах (например, в CallbackQuery), где сообщение могло быть удалено.
 * Различается по полю {@code date}:
 * <ul>
 *     <li>Если {@code date != 0} -> это {@link Message}.</li>
 *     <li>Если {@code date == 0} -> это {@link InaccessibleMessage}.</li>
 * </ul>
 * </p>
 *
 * @since 1.0.0
 */
@JsonTypeInfo(
    use = JsonTypeInfo.Id.NAME,
    include = JsonTypeInfo.As.EXISTING_PROPERTY,
    property = "date",
    defaultImpl = Message.class,
    visible = true
)
@JsonSubTypes({
    @JsonSubTypes.Type(value = InaccessibleMessage.class, name = "0")
})
public sealed interface MaybeInaccessibleMessage extends BotApiObject permits Message, InaccessibleMessage {
    /** Чат, которому принадлежит сообщение. */
    Chat chat();
    
    /** Уникальный идентификатор сообщения. */
    Long messageId();
    
    /** Дата отправки (0 для недоступных сообщений). */
    Integer date();
}