package com.kaleert.nyagram.api.methods.invoices;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.kaleert.nyagram.api.exception.TelegramApiRequestException;
import com.kaleert.nyagram.api.meta.BotApiMethod;
import com.kaleert.nyagram.api.objects.payments.LabeledPrice;
import lombok.*;
import java.util.List;

/**
 * Используйте этот метод для создания ссылки на оплату счета (Invoice Link).
 * <p>
 * Возвращает строку URL. Эту ссылку можно отправить пользователю в ЛС,
 * разместить на канале или в QR-коде.
 * </p>
 *
 * @since 1.0.0
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode(callSuper = false)
@JsonInclude(JsonInclude.Include.NON_NULL)
public class CreateInvoiceLink extends BotApiMethod<String> {
        
    /** Имя метода в Telegram Bot API. */
    public static final String PATH = "createInvoiceLink";

    /**
     * Название продукта (1-32 символа).
     */
    @JsonProperty("title") 
    private String title;
    
    /**
     * Описание продукта (1-255 символов).
     */
    @JsonProperty("description") 
    private String description;
    
    /**
     * Внутренний ID заказа (не виден пользователю).
     */
    @JsonProperty("payload") 
    private String payload;
    
    /**
     * Токен провайдера платежей (Stripe, Smart Glocal и т.д.).
     * Для Telegram Stars оставьте пустым.
     */
    @JsonProperty("provider_token") 
    private String providerToken;
    
    /**
     * Валюта (ISO 4217 код, например "USD", "EUR"). Для Telegram Stars используйте "XTR".
     */
    @JsonProperty("currency") 
    private String currency;
    
    /**
     * Список цен (разбивка стоимости).
     */
    @JsonProperty("prices") 
    private List<LabeledPrice> prices;

    @Override public String getMethod() { return PATH; }
    @Override public String deserializeResponse(String answer) throws TelegramApiRequestException {
        return deserializeResponse(answer, String.class);
    }
    @Override public void validate() {}
}