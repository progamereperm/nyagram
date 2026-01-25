package com.kaleert.nyagram.api.methods.payments;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.kaleert.nyagram.api.exception.TelegramApiRequestException;
import com.kaleert.nyagram.api.meta.BotApiMethod;
import com.kaleert.nyagram.api.objects.payments.StarTransaction;
import lombok.*;
import java.util.List;

/**
 * Используйте этот метод для получения списка транзакций Telegram Stars (входящих и исходящих).
 * <p>
 * Возвращает список {@link StarTransaction}.
 * </p>
 *
 * @since 1.0.0
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode(callSuper = false)
public class GetStarTransactions extends BotApiMethod<List<StarTransaction>> {
        
    /** Имя метода в Telegram Bot API. */
    public static final String PATH = "getStarTransactions";

    /**
     * Количество транзакций, которые нужно пропустить.
     */
    @JsonProperty("offset") 
    private Integer offset;
    
    /**
     * Максимальное количество транзакций для получения (1-100). По умолчанию 100.
     */
    @JsonProperty("limit") 
    private Integer limit;

    @Override public String getMethod() { return PATH; }

    @Override
    public List<StarTransaction> deserializeResponse(String answer) throws TelegramApiRequestException {
        return deserializeResponseArray(answer, StarTransaction.class);
    }
    
    @Override public void validate() {}
}