package com.kaleert.nyagram.api.objects.chatmember;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.kaleert.nyagram.api.objects.User;

/**
 * Представляет владельца (создателя) чата.
 * Владелец имеет все права администратора и не может быть удален другими администраторами.
 *
 * @param user Информация о пользователе.
 * @param isAnonymous True, если владелец скрыт (анонимен) в списке участников.
 * @param customTitle Кастомный титул (должность) владельца.
 *
 * @since 1.0.0
 */
@JsonIgnoreProperties(ignoreUnknown = true)
public record ChatMemberOwner(
    @JsonProperty("user") User user,
    @JsonProperty("is_anonymous") Boolean isAnonymous,
    @JsonProperty("custom_title") String customTitle
) implements ChatMember {
    
    @Override
    public String getStatus() {
        return "creator";
    }
    
    @Override
    public User getUser() {
        return user;
    }
}