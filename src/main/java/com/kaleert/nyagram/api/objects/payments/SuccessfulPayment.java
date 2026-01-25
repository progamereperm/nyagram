package com.kaleert.nyagram.api.objects.payments;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.kaleert.nyagram.api.meta.BotApiObject;
import com.kaleert.nyagram.api.objects.OrderInfo;

/**
 * Содержит информацию об успешно проведенном платеже.
 * Приходит в обновлении типа {@code message} как служебное сообщение.
 *
 * @param currency Трехбуквенный код валюты (ISO 4217) или "XTR".
 * @param totalAmount Общая сумма платежа.
 * @param invoicePayload Полезная нагрузка, указанная ботом при создании счета.
 * @param shippingOptionId Идентификатор выбранной доставки (если была).
 * @param orderInfo Информация о заказе (адрес, телефон), если была запрошена.
 * @param telegramPaymentChargeId Идентификатор платежа в Telegram.
 * @param providerPaymentChargeId Идентификатор платежа у провайдера.
 *
 * @since 1.0.0
 */
@JsonIgnoreProperties(ignoreUnknown = true)
public record SuccessfulPayment(
    @JsonProperty("currency") String currency,
    @JsonProperty("total_amount") Integer totalAmount,
    @JsonProperty("invoice_payload") String invoicePayload,
    @JsonProperty("shipping_option_id") String shippingOptionId,
    @JsonProperty("order_info") OrderInfo orderInfo,
    @JsonProperty("telegram_payment_charge_id") String telegramPaymentChargeId,
    @JsonProperty("provider_payment_charge_id") String providerPaymentChargeId
) implements BotApiObject {

    /**
     * Получает реальную сумму (для фиатных валют делит на 100).
     * @return сумма.
     */
    @JsonIgnore
    public double getRealAmount() {
        return totalAmount / 100.0;
    }
    
    /**
     * Проверяет, был ли платеж совершен в Telegram Stars.
     * @return true, если валюта "XTR".
     */
    @JsonIgnore
    public boolean isStars() {
        return "XTR".equals(currency);
    }
}