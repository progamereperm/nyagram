package com.kaleert.nyagram.api.objects.inlinequery.inputmessagecontent;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Builder;

/**
 * Представляет содержимое сообщения с геолокацией, которое будет отправлено
 * в результате выбора inline-результата.
 *
 * @param latitude Широта.
 * @param longitude Долгота.
 * @param horizontalAccuracy Точность (радиус неопределенности) в метрах.
 * @param livePeriod Период трансляции геопозиции в секундах (для Live Location).
 * @param heading Направление движения в градусах (1-360).
 * @param proximityAlertRadius Радиус оповещения о приближении в метрах.
 *
 * @since 1.0.0
 */
@Builder
@JsonInclude(JsonInclude.Include.NON_NULL)
@JsonIgnoreProperties(ignoreUnknown = true)
public record InputLocationMessageContent(
    @JsonProperty("latitude") Double latitude,
    @JsonProperty("longitude") Double longitude,
    @JsonProperty("horizontal_accuracy") Double horizontalAccuracy,
    @JsonProperty("live_period") Integer livePeriod,
    @JsonProperty("heading") Integer heading,
    @JsonProperty("proximity_alert_radius") Integer proximityAlertRadius
) implements InputMessageContent {
    
    @Override
    public void validate() {
        if (latitude == null || longitude == null) {
            throw new IllegalArgumentException("Lat/Lon required");
        }
    }
}