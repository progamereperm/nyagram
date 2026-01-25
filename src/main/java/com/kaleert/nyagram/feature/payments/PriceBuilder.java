package com.kaleert.nyagram.feature.payment;

import com.kaleert.nyagram.api.objects.payments.LabeledPrice;

import java.util.ArrayList;
import java.util.List;

/**
 * Утилита (Builder) для удобного создания списка цен {@link LabeledPrice}.
 *
 * @since 1.0.0
 */
public class PriceBuilder {

    private final List<LabeledPrice> prices = new ArrayList<>();
    
    /**
     * Создает новый экземпляр строителя.
     *
     * @return новый PriceBuilder.
     */
    public static PriceBuilder create() {
        return new PriceBuilder();
    }
    
    /**
     * Добавляет позицию в чек.
     * @param label Название.
     * @param amount Цена в минимальных единицах.
     */
    public PriceBuilder add(String label, Integer amount) {
        prices.add(new LabeledPrice(label, amount));
        return this;
    }
   
    /**
     * Завершает построение и возвращает список цен.
     *
     * @return Список {@link LabeledPrice}.
     */
    public List<LabeledPrice> build() {
        return prices;
    }
    
    /**
     * Создает список цен для оплаты в Telegram Stars.
     * <p>
     * Автоматически добавляет одну позицию с меткой "Price".
     * </p>
     *
     * @param amount Количество звезд.
     * @return Список, содержащий один объект {@link LabeledPrice}.
     */
    public static List<LabeledPrice> stars(Integer amount) {
        return create().add("Price", amount).build();
    }
    
    /**
     * Создает список цен с одной позицией.
     * <p>
     * Удобно для простых товаров без налогов и доставки.
     * </p>
     *
     * @param label Название товара или услуги (например, "Подписка на 1 месяц").
     * @param amount Цена в минимальных единицах валюты (копейки, центы).
     * @return Список, содержащий один объект {@link LabeledPrice}.
     */
    public static List<LabeledPrice> one(String label, Integer amount) {
        return create().add(label, amount).build();
    }
}