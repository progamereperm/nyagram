package com.kaleert.nyagram.api.objects.chatmember;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.kaleert.nyagram.api.objects.User;

/**
 * Представляет пользователя, который был забанен (удален и добавлен в черный список) в чате.
 *
 * @param user Информация о пользователе.
 * @param untilDate Дата окончания бана (Unix timestamp). 0 - навсегда.
 *
 * @since 1.0.0
 */
@JsonIgnoreProperties(ignoreUnknown = true)
public record ChatMemberBanned(
    @JsonProperty("user") User user,
    @JsonProperty("until_date") Integer untilDate
) implements ChatMember {

    @Override
    public String getStatus() {
        return "kicked";
    }
    
    @Override
    public User getUser() {
        return user;
    }
}