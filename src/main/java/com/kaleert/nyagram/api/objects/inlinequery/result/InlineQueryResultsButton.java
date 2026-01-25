package com.kaleert.nyagram.api.objects.inlinequery.result;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.kaleert.nyagram.api.meta.BotApiObject;
import com.kaleert.nyagram.api.objects.webapp.WebAppInfo;

/**
 * Представляет кнопку, которая будет показана над результатами inline-запроса.
 * <p>
 * При нажатии пользователь либо переключится в приватный чат с ботом,
 * либо откроет Web App.
 * </p>
 *
 * @param text Текст кнопки.
 * @param webApp Описание Web App, которое будет запущено при нажатии.
 *               Если не указано, нажатие переключит пользователя в личный чат с ботом.
 * @param startParameter Параметр Deep-link для команды /start (если web_app не указан).
 *                       Пример: если параметр "test", то при нажатии будет отправлено "/start test".
 *
 * @since 1.0.0
 */
@JsonIgnoreProperties(ignoreUnknown = true)
public record InlineQueryResultsButton(
    @JsonProperty("text") String text,
    @JsonProperty("web_app") WebAppInfo webApp,
    @JsonProperty("start_parameter") String startParameter
) implements BotApiObject {}