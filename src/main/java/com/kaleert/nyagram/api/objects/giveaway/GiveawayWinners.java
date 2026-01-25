package com.kaleert.nyagram.api.objects.giveaway;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.kaleert.nyagram.api.meta.BotApiObject;
import com.kaleert.nyagram.api.objects.User;
import com.kaleert.nyagram.api.objects.chat.Chat;

import java.util.List;

/**
 * Представляет сообщение с результатами розыгрыша (Giveaway Winners).
 *
 * @param chat Чат, который проводил розыгрыш.
 * @param giveawayMessageId Идентификатор сообщения с анонсом розыгрыша.
 * @param winnersSelectionDate Дата выбора победителей.
 * @param winnerCount Общее количество победителей.
 * @param winners Список победителей (может быть пустым, если победителей много или они скрыты).
 * @param additionalChatCount Количество других чатов, участвовавших в розыгрыше.
 * @param premiumSubscriptionMonthCount Количество месяцев Premium подписки в призе.
 * @param unclaimedPrizeCount Количество невостребованных призов.
 * @param onlyNewMembers True, если участвовали только новые подписчики.
 * @param wasRefunded True, если розыгрыш был отменен и средства возвращены.
 * @param prizeDescription Описание дополнительного приза.
 *
 * @since 1.0.0
 */
@JsonIgnoreProperties(ignoreUnknown = true)
public record GiveawayWinners(
    @JsonProperty("chat") Chat chat,
    @JsonProperty("giveaway_message_id") Integer giveawayMessageId,
    @JsonProperty("winners_selection_date") Integer winnersSelectionDate,
    @JsonProperty("winner_count") Integer winnerCount,
    @JsonProperty("winners") List<User> winners,
    @JsonProperty("additional_chat_count") Integer additionalChatCount,
    @JsonProperty("premium_subscription_month_count") Integer premiumSubscriptionMonthCount,
    @JsonProperty("unclaimed_prize_count") Integer unclaimedPrizeCount,
    @JsonProperty("only_new_members") Boolean onlyNewMembers,
    @JsonProperty("was_refunded") Boolean wasRefunded,
    @JsonProperty("prize_description") String prizeDescription
) implements BotApiObject {}