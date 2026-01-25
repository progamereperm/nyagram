package com.kaleert.nyagram.api.objects;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.kaleert.nyagram.api.meta.BotApiObject;

import java.time.Instant;

/**
 * Представляет пригласительную ссылку для чата.
 *
 * @param inviteLink Пригласительная ссылка. Если ссылка была создана другим администратором,
 *                   вторая часть ссылки будет скрыта звездочками.
 * @param creator Создатель ссылки.
 * @param createsJoinRequest True, если пользователям нужно одобрение администратора для вступления.
 * @param isPrimary True, если ссылка является основной (постоянной) для чата.
 * @param isRevoked True, если ссылка была отозвана.
 * @param name Название ссылки (видна только администраторам).
 * @param expireDate Дата истечения срока действия (Unix timestamp).
 * @param memberLimit Максимальное количество пользователей, которые могут вступить по ссылке.
 * @param pendingJoinRequestCount Количество ожидающих заявок на вступление.
 *
 * @since 1.0.0
 */
@JsonIgnoreProperties(ignoreUnknown = true)
public record ChatInviteLink(
    @JsonProperty("invite_link") String inviteLink,
    @JsonProperty("creator") User creator,
    @JsonProperty("creates_join_request") Boolean createsJoinRequest,
    @JsonProperty("is_primary") Boolean isPrimary,
    @JsonProperty("is_revoked") Boolean isRevoked,
    @JsonProperty("name") String name,
    @JsonProperty("expire_date") Integer expireDate,
    @JsonProperty("member_limit") Integer memberLimit,
    @JsonProperty("pending_join_request_count") Integer pendingJoinRequestCount
) implements BotApiObject {

    /**
     * Проверяет, истек ли срок действия ссылки.
     * @return true, если срок действия истек.
     */
    @JsonIgnore
    public boolean isExpired() {
        if (expireDate == null) return false;
        return Instant.now().getEpochSecond() > expireDate;
    }
    
    /**
     * Проверяет, имеет ли ссылка лимит на количество вступлений.
     * @return true, если лимит установлен.
     */
    @JsonIgnore
    public boolean hasLimit() {
        return memberLimit != null && memberLimit > 0;
    }
}