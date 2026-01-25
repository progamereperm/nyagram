package com.kaleert.nyagram.api.objects.payments;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.kaleert.nyagram.api.meta.BotApiObject;
import com.kaleert.nyagram.api.objects.User;
import java.util.List;

/**
 * Описывает транзакцию Telegram Stars.
 *
 * @param id Уникальный идентификатор транзакции.
 * @param amount Количество звезд. Положительное для входящих транзакций, отрицательное для исходящих.
 * @param date Дата транзакции (Unix timestamp).
 * @param source Источник средств (от кого пришли звезды).
 * @param receiver Получатель средств (кому ушли звезды).
 *
 * @since 1.0.0
 */
@JsonIgnoreProperties(ignoreUnknown = true)
public record StarTransaction(
    @JsonProperty("id") String id,
    @JsonProperty("amount") Integer amount,
    @JsonProperty("date") Integer date,
    @JsonProperty("source") TransactionPartner source,
    @JsonProperty("receiver") TransactionPartner receiver
) implements BotApiObject {}

/**
 * Описывает участника транзакции Telegram Stars.
 * <p>
 * Это может быть пользователь, фрагмент (для вывода средств) или другой бот/канал.
 * </p>
 *
 * @param type Тип партнера ("user", "fragment", "telegram_ads" и т.д.).
 * @param user Информация о пользователе (если type="user").
 * @param invoicePayload Полезная нагрузка инвойса (если применимо).
 *
 * @since 1.0.0
 */
@JsonIgnoreProperties(ignoreUnknown = true)
record TransactionPartner(
    @JsonProperty("type") String type,
    @JsonProperty("user") User user,
    @JsonProperty("invoice_payload") String invoicePayload
) implements BotApiObject {}