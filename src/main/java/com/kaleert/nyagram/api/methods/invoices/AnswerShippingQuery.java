package com.kaleert.nyagram.api.methods.invoices;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.kaleert.nyagram.api.meta.BotApiMethodBoolean;
import com.kaleert.nyagram.api.objects.payments.ShippingOption;
import lombok.*;
import java.util.List;

/**
 * Используйте этот метод для ответа на запросы доставки (Shipping Query).
 * <p>
 * Если вы отправили счет с параметром {@code is_flexible}, бот получит этот запрос,
 * когда пользователь выберет адрес доставки. Вы должны ответить списком вариантов доставки
 * или ошибкой (если доставка в этот регион невозможна).
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
public class AnswerShippingQuery extends BotApiMethodBoolean {
        
    /** Имя метода в Telegram Bot API. */
    public static final String PATH = "answerShippingQuery";

    /**
     * Уникальный идентификатор запроса доставки.
     */
    @JsonProperty("shipping_query_id") 
    private String shippingQueryId;
    
    /**
     * True, если доставка по указанному адресу возможна.
     */
    @JsonProperty("ok") 
    private Boolean ok;
    
    /**
     * Обязательно, если ok=True. Список доступных вариантов доставки.
     */
    @JsonProperty("shipping_options") 
    private List<ShippingOption> shippingOptions;
    
    /**
     * Обязательно, если ok=False. Сообщение об ошибке.
     */
    @JsonProperty("error_message") 
    private String errorMessage;

    @Override public String getMethod() { return PATH; }
    @Override public void validate() {}
}