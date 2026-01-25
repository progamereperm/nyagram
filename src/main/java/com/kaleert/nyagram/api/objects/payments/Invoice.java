package com.kaleert.nyagram.api.objects.payments;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.kaleert.nyagram.api.meta.BotApiObject;

/**
 * Содержит основную информацию о счете (Invoice).
 *
 * @param title Название продукта.
 * @param description Описание продукта.
 * @param startParameter Уникальный параметр deep-linking, использованный для генерации этого счета.
 * @param currency Трехбуквенный код валюты (ISO 4217) или "XTR" для Telegram Stars.
 * @param totalAmount Общая цена в минимальных единицах валюты (копейки, центы, или целые звезды).
 *
 * @since 1.0.0
 */
@JsonIgnoreProperties(ignoreUnknown = true)
public record Invoice(
    @JsonProperty("title") String title,
    @JsonProperty("description") String description,
    @JsonProperty("start_parameter") String startParameter,
    @JsonProperty("currency") String currency,
    @JsonProperty("total_amount") Integer totalAmount
) implements BotApiObject {

    /**
     * Возвращает реальную сумму в основной валюте (например, рубли или доллары).
     * Делит {@code totalAmount} на 100.
     * <p>
     * <b>Внимание:</b> Для Telegram Stars ("XTR") делить на 100 не нужно, там сумма всегда целая.
     * </p>
     *
     * @return сумма.
     */
    @JsonIgnore
    public double getRealAmount() {
        return totalAmount / 100.0;
    }
    
    /**
     * Проверяет, выставлен ли счет в Telegram Stars.
     * @return true, если валюта "XTR".
     */
    @JsonIgnore
    public boolean isStars() {
        return "XTR".equals(currency);
    }
}