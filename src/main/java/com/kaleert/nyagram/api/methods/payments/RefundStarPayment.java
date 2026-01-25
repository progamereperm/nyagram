package com.kaleert.nyagram.api.methods.payments;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.kaleert.nyagram.api.exception.TelegramApiValidationException;
import com.kaleert.nyagram.api.meta.BotApiMethodBoolean;
import lombok.*;

/**
 * Используйте этот метод для возврата платежа в Telegram Stars.
 * <p>
 * Возвращает {@code True} в случае успеха.
 * </p>
 *
 * @since 1.0.0
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode(callSuper = false)
public class RefundStarPayment extends BotApiMethodBoolean {
        
    /** Имя метода в Telegram Bot API. */
    public static final String PATH = "refundStarPayment";

    /**
     * Уникальный идентификатор пользователя.
     */
    @JsonProperty("user_id") 
    private Long userId;
    
    /**
     * Идентификатор транзакции (charge ID), которую нужно вернуть.
     * Этот ID можно найти в {@link com.kaleert.nyagram.api.objects.payments.SuccessfulPayment}.
     */
    @JsonProperty("telegram_payment_charge_id") 
    private String telegramPaymentChargeId;

    @Override public String getMethod() { return PATH; }

    @Override public void validate() throws TelegramApiValidationException {
        if (userId == null) throw new TelegramApiValidationException("UserId обязателен", PATH);
        if (telegramPaymentChargeId == null) throw new TelegramApiValidationException("ChargeID обязателен", PATH);
    }
}