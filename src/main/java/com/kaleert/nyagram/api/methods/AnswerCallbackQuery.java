package com.kaleert.nyagram.api.methods;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.kaleert.nyagram.api.exception.TelegramApiValidationException;
import com.kaleert.nyagram.api.meta.BotApiMethodBoolean;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import lombok.NonNull;

/**
 * Используйте этот метод для отправки ответов на callback-запросы, отправленные с inline-клавиатур.
 * <p>
 * Ответ будет отображен пользователю как уведомление вверху экрана (toast) или как всплывающее окно (alert).
 * Если не вызвать этот метод, у пользователя будет бесконечно крутиться индикатор загрузки на кнопке.
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
public class AnswerCallbackQuery extends BotApiMethodBoolean {

    /** Имя метода в Telegram Bot API. */
    public static final String PATH = "answerCallbackQuery";
    
    /**
     * Уникальный идентификатор запроса, на который нужно ответить.
     */
    @JsonProperty("callback_query_id")
    @NonNull
    private String callbackQueryId;
    
    /**
     * Текст уведомления. Если не задан, ничего не будет показано пользователю.
     * 0-200 символов.
     */
    @JsonProperty("text")
    private String text;
   
    /**
     * Если true, будет показано модальное окно (alert) с кнопкой "ОК".
     * Если false (по умолчанию), будет показано временное уведомление вверху экрана.
     */
    @JsonProperty("show_alert")
    private Boolean showAlert;
    
    /**
     * URL, который будет открыт приложением пользователя.
     * В основном используется для игр.
     */
    @JsonProperty("url")
    private String url;
    
    /**
     * Максимальное время в секундах, в течение которого результат запроса может быть кэширован
     * на стороне клиента Telegram. По умолчанию 0.
     */
    @JsonProperty("cache_time")
    private Integer cacheTime;

    @Override
    public String getMethod() {
        return PATH;
    }

    @Override
    public void validate() throws TelegramApiValidationException {
        if (callbackQueryId == null || callbackQueryId.isEmpty()) {
            throw new TelegramApiValidationException("CallbackQueryId cannot be empty", PATH, "callback_query_id");
        }
        if (text != null && text.length() > 200) {
            throw new TelegramApiValidationException("Text must be <= 200 characters", PATH, "text");
        }
    }
    
    /**
     * Создает пустой ответ, чтобы убрать часики (индикатор загрузки) с кнопки.
     * Никакого текста пользователю показано не будет.
     *
     * @param callbackQueryId ID callback-запроса.
     * @return готовый объект запроса.
     */
    public static AnswerCallbackQuery ok(String callbackQueryId) {
        return AnswerCallbackQuery.builder()
                .callbackQueryId(callbackQueryId)
                .build();
    }
    
    /**
     * Создает ответ, который покажет пользователю всплывающее уведомление (Toast) вверху экрана.
     *
     * @param callbackQueryId ID callback-запроса.
     * @param text Текст сообщения.
     * @return готовый объект запроса.
     */
    public static AnswerCallbackQuery notification(String callbackQueryId, String text) {
        return AnswerCallbackQuery.builder()
                .callbackQueryId(callbackQueryId)
                .text(text)
                .showAlert(false)
                .build();
    }
    
    /**
     * Создает ответ, который покажет пользователю модальное окно с кнопкой "ОК" (Alert).
     *
     * @param callbackQueryId ID callback-запроса.
     * @param text Текст сообщения.
     * @return готовый объект запроса.
     */
    public static AnswerCallbackQuery alert(String callbackQueryId, String text) {
        return AnswerCallbackQuery.builder()
                .callbackQueryId(callbackQueryId)
                .text(text)
                .showAlert(true)
                .build();
    }
}