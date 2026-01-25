package com.kaleert.nyagram.api.objects.payments;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.kaleert.nyagram.api.meta.BotApiObject;

/**
 * Представляет часть цены товара (например, "Цена товара", "Налог", "Доставка").
 *
 * @param label Название этой части цены.
 * @param amount Цена в минимальных единицах валюты (копейки, центы).
 *
 * @since 1.0.0
 */
public record LabeledPrice(
    @JsonProperty("label") String label,
    @JsonProperty("amount") Integer amount
) implements BotApiObject {

    /**
     * Удобный фабричный метод для создания цены.
     * @param label Метка.
     * @param amount Сумма.
     * @return объект LabeledPrice.
     */
    public static LabeledPrice of(String label, int amount) {
        return new LabeledPrice(label, amount);
    }
}