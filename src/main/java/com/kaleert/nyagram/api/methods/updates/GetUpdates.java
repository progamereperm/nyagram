package com.kaleert.nyagram.api.methods.updates;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.kaleert.nyagram.api.exception.TelegramApiRequestException;
import com.kaleert.nyagram.api.exception.TelegramApiValidationException;
import com.kaleert.nyagram.api.meta.BotApiMethod;
import com.kaleert.nyagram.api.objects.Update;
import lombok.*;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * Используйте этот метод для получения входящих обновлений с помощью длинных опросов (Long Polling).
 * <p>
 * Возвращает список объектов {@link Update}.
 * <b>Этот метод не работает, если установлен вебхук.</b>
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
public class GetUpdates extends BotApiMethod<List<Update>> {
    
    /** Имя метода в Telegram Bot API. */
    public static final String PATH = "getUpdates";

    /**
     * Идентификатор первого обновления, которое нужно вернуть.
     * Должен быть на единицу больше, чем самый высокий идентификатор среди полученных ранее обновлений.
     */
    @JsonProperty("offset")
    private Long offset;

    /**
     * Ограничение количества получаемых обновлений (1-100). По умолчанию 100.
     */
    @JsonProperty("limit")
    private Integer limit;

    /**
     * Таймаут в секундах для длинного опроса. По умолчанию 0 (короткий опрос).
     */
    @JsonProperty("timeout")
    private Integer timeout;

    /**
     * Список типов обновлений, которые бот хочет получать (например, "message", "edited_channel_post").
     */
    @JsonProperty("allowed_updates")
    private List<String> allowedUpdates;

    @Override
    public String getMethod() {
        return PATH;
    }

    @Override
    public List<Update> deserializeResponse(String answer) throws TelegramApiRequestException {
        return deserializeResponseArray(answer, Update.class);
    }

    @Override
    public void validate() throws TelegramApiValidationException {
        if (limit != null && (limit > 100 || limit <= 0)) {
            throw new TelegramApiValidationException("Limit must be between 1 and 100", PATH, "limit");
        }
        if (timeout != null && timeout < 0) {
            throw new TelegramApiValidationException("Timeout must be positive", PATH, "timeout");
        }
    }
    
    /**
     * Указывает типы обновлений, которые бот хочет получать.
     * <p>
     * Например: "message", "edited_channel_post", "callback_query".
     * </p>
     *
     * @param updateTypes Список типов обновлений.
     * @return текущий билдер.
     */
    public GetUpdates allow(String... updateTypes) {
        if (this.allowedUpdates == null) {
            this.allowedUpdates = new ArrayList<>();
        }
        this.allowedUpdates.addAll(Arrays.asList(updateTypes));
        return this;
    }
    
    /**
     * Устанавливает время ожидания (Long Polling) в секундах.
     * <p>
     * Максимальное значение — 50 секунд.
     * Рекомендуется использовать значение больше 0 для экономии трафика.
     * </p>
     *
     * @param seconds Время ожидания (0-50).
     * @return текущий билдер.
     */
    public GetUpdates waitFor(int seconds) {
        if (seconds > 50) seconds = 50; 
        this.timeout = seconds;
        return this;
    }
    
    /**
     * Создает запрос для получения следующей порции обновлений.
     *
     * @param offset Идентификатор (update_id) последнего обработанного обновления + 1.
     * @return готовый объект запроса.
     */
    public static GetUpdates next(Long offset) {
        return GetUpdates.builder()
                .offset(offset)
                .timeout(50)
                .build();
    }
}