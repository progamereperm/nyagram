package com.kaleert.nyagram.api.objects.boost;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.kaleert.nyagram.api.meta.BotApiObject;

import java.time.Instant;

/**
 * Содержит информацию о бусте (Boost), добавленном чату.
 *
 * @param boostId Уникальный идентификатор буста.
 * @param addDate Дата добавления буста (Unix timestamp).
 * @param expirationDate Дата истечения срока действия буста (Unix timestamp).
 * @param source Источник буста (премиум, подарок и т.д.).
 *
 * @since 1.0.0
 */
@JsonIgnoreProperties(ignoreUnknown = true)
public record ChatBoost(
    @JsonProperty("boost_id") String boostId,
    @JsonProperty("add_date") Integer addDate,
    @JsonProperty("expiration_date") Integer expirationDate,
    @JsonProperty("source") ChatBoostSource source
) implements BotApiObject {

    /**
     * Проверяет, истек ли срок действия этого буста.
     * @return true, если буст больше не активен.
     */
    @JsonIgnore
    public boolean isExpired() {
        return Instant.now().getEpochSecond() > expirationDate;
    }
}