package com.kaleert.nyagram.api.methods.invoices;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.kaleert.nyagram.api.exception.TelegramApiValidationException;
import com.kaleert.nyagram.api.meta.BotApiMethodBoolean;
import lombok.*;

/**
 * Используйте этот метод для ответа на запросы pre-checkout.
 * <p>
 * После того как пользователь нажал кнопку "Оплатить", бот получает событие PreCheckoutQuery.
 * Бот должен ответить на него в течение 10 секунд подтверждением (ok=true) или отказом (ok=false).
 * </p>
 *
 * @since 1.0.0
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode(callSuper = false)
@JsonIgnoreProperties(ignoreUnknown = true)
@JsonInclude(JsonInclude.Include.NON_NULL)
public class AnswerPreCheckoutQuery extends BotApiMethodBoolean {
    
    /** Имя метода в Telegram Bot API. */
    public static final String PATH = "answerPreCheckoutQuery";

    /**
     * Уникальный идентификатор запроса, на который нужно ответить.
     */
    @JsonProperty("pre_checkout_query_id")
    private String preCheckoutQueryId;

    /**
     * Укажите True, если все в порядке (товар есть в наличии) и можно продолжать оплату.
     * Иначе False.
     */
    @JsonProperty("ok")
    private Boolean ok;

    /**
     * Требуется, если ok равен False. Сообщение об ошибке в человекочитаемом формате,
     * которое будет показано пользователю.
     */
    @JsonProperty("error_message")
    private String errorMessage;

    @Override
    public String getMethod() {
        return PATH;
    }

    @Override
    public void validate() throws TelegramApiValidationException {
        if (preCheckoutQueryId == null) throw new TelegramApiValidationException("ID required", PATH);
        if (ok == null) throw new TelegramApiValidationException("OK status required", PATH);
        if (Boolean.FALSE.equals(ok) && errorMessage == null) {
            throw new TelegramApiValidationException("Error message required when ok is false", PATH);
        }
    }
    
    /**
     * Создает положительный ответ на запрос проверки платежа.
     * Означает, что все проверки пройдены, товар в наличии, можно списывать деньги.
     *
     * @param queryId ID запроса (pre_checkout_query_id).
     * @return готовый объект ответа.
     */
    public static AnswerPreCheckoutQuery approve(String queryId) {
        return AnswerPreCheckoutQuery.builder()
                .preCheckoutQueryId(queryId)
                .ok(true)
                .build();
    }

    /**
     * Создает отрицательный ответ на запрос проверки платежа.
     * Платеж будет отменен, а пользователь увидит сообщение об ошибке.
     *
     * @param queryId ID запроса (pre_checkout_query_id).
     * @param reason Текст ошибки, который увидит пользователь.
     * @return готовый объект ответа.
     */
    public static AnswerPreCheckoutQuery reject(String queryId, String reason) {
        return AnswerPreCheckoutQuery.builder()
                .preCheckoutQueryId(queryId)
                .ok(false)
                .errorMessage(reason)
                .build();
    }
}