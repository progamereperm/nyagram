package com.kaleert.nyagram.api.objects.chatmember;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonSubTypes;
import com.fasterxml.jackson.annotation.JsonTypeInfo;
import com.kaleert.nyagram.api.meta.BotApiObject;
import com.kaleert.nyagram.api.objects.User;

/**
 * Базовый интерфейс, представляющий участника чата и его статус.
 * <p>
 * В зависимости от статуса (status), этот объект будет десериализован в один из следующих классов:
 * <ul>
 *     <li>{@link ChatMemberOwner} - создатель (creator)</li>
 *     <li>{@link ChatMemberAdministrator} - администратор (administrator)</li>
 *     <li>{@link ChatMemberMember} - обычный участник (member)</li>
 *     <li>{@link ChatMemberRestricted} - ограниченный участник (restricted)</li>
 *     <li>{@link ChatMemberLeft} - покинувший чат (left)</li>
 *     <li>{@link ChatMemberBanned} - забаненный (kicked)</li>
 * </ul>
 * </p>
 *
 * @since 1.0.0
 */
@JsonTypeInfo(
    use = JsonTypeInfo.Id.NAME,
    include = JsonTypeInfo.As.PROPERTY,
    property = "status"
)
@JsonSubTypes({
    @JsonSubTypes.Type(value = ChatMemberOwner.class, name = "creator"),
    @JsonSubTypes.Type(value = ChatMemberAdministrator.class, name = "administrator"),
    @JsonSubTypes.Type(value = ChatMemberMember.class, name = "member"),
    @JsonSubTypes.Type(value = ChatMemberRestricted.class, name = "restricted"),
    @JsonSubTypes.Type(value = ChatMemberLeft.class, name = "left"),
    @JsonSubTypes.Type(value = ChatMemberBanned.class, name = "kicked")
})
public sealed interface ChatMember extends BotApiObject 
    permits ChatMemberOwner, ChatMemberAdministrator, ChatMemberMember, 
            ChatMemberRestricted, ChatMemberLeft, ChatMemberBanned {
    /**
     * Статус участника в чате.
     * @return "creator", "administrator", "member", "restricted", "left" или "kicked".
     */
    String getStatus();
    
    /**
     * Информация о пользователе.
     * @return объект User.
     */
    User getUser();
    
    /**
     * Проверяет, является ли пользователь администратором или создателем чата.
     * @return true, если пользователь имеет права управления.
     */
    @JsonIgnore
    default boolean isAdminOrOwner() {
        return this instanceof ChatMemberOwner || this instanceof ChatMemberAdministrator;
    }
}