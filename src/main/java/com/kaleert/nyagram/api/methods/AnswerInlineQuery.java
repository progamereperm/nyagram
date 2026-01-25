package com.kaleert.nyagram.api.methods;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.kaleert.nyagram.api.exception.TelegramApiValidationException;
import com.kaleert.nyagram.api.meta.BotApiMethodBoolean;
import com.kaleert.nyagram.api.objects.inlinequery.result.InlineQueryResult;
import com.kaleert.nyagram.api.objects.inlinequery.result.InlineQueryResultsButton; // Нужно создать, если нет
import lombok.*;

import java.util.List;

/**
 * Используйте этот метод для отправки ответов на Inline-запросы.
 * <p>
 * В ответ на ввод пользователя (например, "@gif cat") бот отправляет список результатов
 * (картинок, статей, видео), которые пользователь может выбрать.
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
public class AnswerInlineQuery extends BotApiMethodBoolean {

    /** Имя метода в Telegram Bot API. */
    public static final String PATH = "answerInlineQuery";
    
    /**
     * Уникальный идентификатор inline-запроса, на который отправляется ответ.
     */
    @JsonProperty("inline_query_id")
    private String inlineQueryId;
    
    /**
     * Массив результатов для отображения пользователю (до 50 штук).
     */
    @JsonProperty("results")
    private List<InlineQueryResult> results;
    
    /**
     * Максимальное время в секундах, в течение которого результат запроса может быть кэширован
     * на серверах Telegram. По умолчанию 300.
     */
    @JsonProperty("cache_time")
    private Integer cacheTime;
    
    /**
     * Передайте true, если результаты могут быть личными для конкретного пользователя
     * и их нельзя кэшировать для других.
     * <p>
     * Например, если бот возвращает баланс кошелька, это поле должно быть true.
     * </p>
     */
    @JsonProperty("is_personal")
    private Boolean isPersonal;
    
    /**
     * Смещение, которое клиент должен отправить в следующем запросе с тем же текстом,
     * чтобы получить следующую порцию результатов. Передайте пустую строку, если результатов больше нет.
     * (Пагинация).
     */
    @JsonProperty("next_offset")
    private String nextOffset;
    
    /**
     * Объект, описывающий кнопку, которая будет показана над результатами поиска.
     * Обычно используется для перехода в настройки бота или переключения в личный чат.
     */
    @JsonProperty("button")
    private InlineQueryResultsButton button;

    @Override
    public String getMethod() {
        return PATH;
    }

    @Override
    public void validate() throws TelegramApiValidationException {
        if (inlineQueryId == null || inlineQueryId.isEmpty()) {
            throw new TelegramApiValidationException("InlineQueryId cannot be empty", PATH, "inline_query_id");
        }
        if (results == null) {
            throw new TelegramApiValidationException("Results cannot be null", PATH, "results");
        }
    }
    
    /**
     * Устанавливает время кэширования результатов на серверах Telegram.
     *
     * @param seconds Время в секундах.
     * @return текущий билдер.
     */
    public AnswerInlineQuery cacheFor(int seconds) {
        this.cacheTime = seconds;
        return this;
    }
    
    /**
     * Отключает кэширование результатов (cache_time = 0).
     *
     * @return текущий билдер.
     */
    public AnswerInlineQuery noCache() {
        this.cacheTime = 0;
        return this;
    }
    
    /**
     * Помечает результаты как личные (Personal).
     * Они будут кэшироваться только для конкретного пользователя, а не для всех.
     *
     * @return текущий билдер.
     */
    public AnswerInlineQuery personal() {
        this.isPersonal = true;
        return this;
    }
    
    /**
     * Устанавливает смещение для пагинации.
     * Клиент отправит это значение в следующем запросе в поле {@code offset}.
     *
     * @param offset Строка смещения.
     * @return текущий билдер.
     */
    public AnswerInlineQuery nextOffset(String offset) {
        this.nextOffset = offset;
        return this;
    }

    /**
     * Статический метод для быстрого создания ответа со списком результатов.
     *
     * @param inlineQueryId ID запроса.
     * @param results Список результатов.
     * @return готовый объект запроса.
     */
    public static AnswerInlineQuery of(String inlineQueryId, List<InlineQueryResult> results) {
        return AnswerInlineQuery.builder()
                .inlineQueryId(inlineQueryId)
                .results(results)
                .build();
    }
}