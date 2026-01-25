package com.kaleert.nyagram.api.objects;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.kaleert.nyagram.api.meta.BotApiObject;

/**
 * Представляет анимированный эмодзи, отображающий случайное значение.
 * Например, кубик, дартс или баскетбол.
 *
 * @param emoji Эмодзи, на основе которого создана анимация.
 * @param value Значение результата броска.
 *              <ul>
 *                  <li>🎲, 🎯, 🎳: 1-6</li>
 *                  <li>🏀, ⚽: 1-5</li>
 *                  <li>🎰: 1-64</li>
 *              </ul>
 *
 * @since 1.0.0
 */
@JsonIgnoreProperties(ignoreUnknown = true)
public record Dice(
    @JsonProperty("emoji") String emoji,
    @JsonProperty("value") Integer value
) implements BotApiObject {

    /**
     * Проверяет, выпал ли "джекпот" (максимально возможный результат).
     * <ul>
     *     <li>Для 🎰 это 64 (три семерки).</li>
     *     <li>Для 🎲, 🎯, 🎳 это 6.</li>
     *     <li>Для 🏀, ⚽ это 5 (гол).</li>
     * </ul>
     * @return true, если результат максимальный.
     */
    @JsonIgnore
    public boolean isJackpot() {
        if ("🎰".equals(emoji)) return value == 64;
        if ("🎲".equals(emoji) || "🎯".equals(emoji) || "🎳".equals(emoji)) return value == 6;
        if ("🏀".equals(emoji) || "⚽".equals(emoji)) return value == 5;
        return false;
    }
    
    /**
     * Проверяет, является ли результат минимально возможным (обычно промах или неудача).
     * @return true, если value == 1.
     */
    @JsonIgnore
    public boolean isMin() {
        return value == 1;
    }
}