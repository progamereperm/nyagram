package com.kaleert.nyagram.api.objects.chatmember;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.kaleert.nyagram.api.objects.User;

/**
 * Представляет пользователя, который покинул чат (сам или был удален без бана).
 *
 * @param user Информация о пользователе.
 *
 * @since 1.0.0
 */
@JsonIgnoreProperties(ignoreUnknown = true)
public record ChatMemberLeft(
    @JsonProperty("user") User user
) implements ChatMember {
    
    @Override
    public String getStatus() {
        return "left";
    }
    
    @Override
    public User getUser() {
        return user;
    }
}