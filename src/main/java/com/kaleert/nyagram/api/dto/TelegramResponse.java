package com.kaleert.nyagram.api.dto;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

/**
 * DTO для десериализации стандартного ответа Telegram API.
 * <p>
 * Используется для обработки ошибок (когда {@code ok = false}) или простых ответов.
 * </p>
 *
 * @param <T> Тип результата.
 * @since 1.0.0
 */
@Data
@JsonIgnoreProperties(ignoreUnknown = true)
public class TelegramResponse<T> {
    private Boolean ok;
    private T result;
    private String description;
    @JsonProperty("error_code")
    private Integer errorCode;
}