package com.kaleert.nyagram.api.objects.chatmember;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.kaleert.nyagram.api.objects.User;

/**
 * Представляет обычного участника чата без дополнительных привилегий или ограничений.
 *
 * @param user Информация о пользователе.
 * @param untilDate Дата истечения подписки (актуально только для платных подписок, иначе 0).
 *
 * @since 1.0.0
 */
@JsonIgnoreProperties(ignoreUnknown = true)
public record ChatMemberMember(
    @JsonProperty("user") User user,
    @JsonProperty("until_date") Integer untilDate
) implements ChatMember {
    
    @Override
    public String getStatus() {
        return "member";
    }
    
    @Override
    public User getUser() {
        return user;
    }
}