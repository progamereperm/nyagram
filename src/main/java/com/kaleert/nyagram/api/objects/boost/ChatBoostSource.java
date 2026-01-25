package com.kaleert.nyagram.api.objects.boost; // Обратите внимание на пакет

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.kaleert.nyagram.api.meta.BotApiObject;
import com.kaleert.nyagram.api.objects.User;
import java.io.Serializable;

/**
 * Описывает источник буста (Chat Boost).
 * <p>
 * Поле {@code source} определяет тип: "premium", "gift_code" или "giveaway".
 * </p>
 *
 * @since 1.0.0
 */
@JsonIgnoreProperties(ignoreUnknown = true)
public class ChatBoostSource implements Serializable, BotApiObject {
    /** Источник буста: "premium", "gift_code" или "giveaway". */
    @JsonProperty("source")
    private String source;
    
    /** Пользователь, который добавил буст (для premium и gift_code). */
    @JsonProperty("user")
    private User user;
    
    /** Идентификатор сообщения с розыгрышем (для giveaway). */
    @JsonProperty("giveaway_message_id")
    private Integer giveawayMessageId;
    
    /** Количество звезд, если буст получен за звезды (опционально). */
    @JsonProperty("prize_star_count") 
    private Integer prizeStarCount;
    
    /** True, если приз в розыгрыше не был востребован (для giveaway). */
    @JsonProperty("is_unclaimed") 
    private Boolean isUnclaimed;
    
    /**
     * Возвращает тип источника буста.
     * <p>
     * Возможные значения: "premium" (от пользователя), "gift_code" (подарочный код), "giveaway" (розыгрыш).
     * </p>
     *
     * @return строка типа источника.
     */
    public String getSource() { return source; }
    
    /**
     * Устанавливает тип источника буста.
     *
     * @param source Тип источника.
     */
    public void setSource(String source) { this.source = source; }
    
    /**
     * Устанавливает пользователя, добавившего буст.
     *
     * @param user Объект пользователя.
     */
    public User getUser() { return user; }
    
    /**
     * Устанавливает пользователя, который добавил буст.
     * <p>
     * Актуально для источников типа "premium" или "gift_code".
     * </p>
     *
     * @param user Пользователь.
     */
    public void setUser(User user) { this.user = user; }
}