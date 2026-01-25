package com.kaleert.nyagram.api.objects.giveaway;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.kaleert.nyagram.api.meta.BotApiObject;
import com.kaleert.nyagram.api.objects.chat.Chat;

import java.util.List;

/**
 * Представляет сообщение о запланированном розыгрыше (Giveaway).
 *
 * @param chats Список каналов, на которые пользователь должен подписаться для участия.
 * @param winnersSelectionDate Дата, когда будут выбраны победители (Unix timestamp).
 * @param winnerCount Количество победителей.
 * @param onlyNewMembers True, если участвовать могут только новые подписчики.
 * @param hasPublicWinners True, если список победителей будет публичным.
 * @param prizeDescription Описание дополнительного приза (если есть).
 * @param countryCodes Список кодов стран (ISO 3166-1 alpha-2), для которых доступен розыгрыш.
 * @param premiumSubscriptionMonthCount Количество месяцев подписки Telegram Premium, которые получат победители.
 *
 * @since 1.0.0
 */
@JsonIgnoreProperties(ignoreUnknown = true)
public record Giveaway(
    @JsonProperty("chats") List<Chat> chats,
    @JsonProperty("winners_selection_date") Integer winnersSelectionDate,
    @JsonProperty("winner_count") Integer winnerCount,
    @JsonProperty("only_new_members") Boolean onlyNewMembers,
    @JsonProperty("has_public_winners") Boolean hasPublicWinners,
    @JsonProperty("prize_description") String prizeDescription,
    @JsonProperty("country_codes") List<String> countryCodes,
    @JsonProperty("premium_subscription_month_count") Integer premiumSubscriptionMonthCount
) implements BotApiObject {}