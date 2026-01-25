package com.kaleert.nyagram.api.objects;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.kaleert.nyagram.api.meta.BotApiObject;

/**
 * Представляет точку на карте (географические координаты).
 *
 * @param longitude Долгота, определенная отправителем.
 * @param latitude Широта, определенная отправителем.
 * @param horizontalAccuracy Радиус неопределенности (точность) в метрах. 0-1500.
 * @param livePeriod Время в секундах, в течение которого локация будет обновляться (Live Location).
 * @param heading Направление движения в градусах (1-360).
 * @param proximityAlertRadius Максимальное расстояние в метрах для уведомлений о приближении (1-100000).
 *
 * @since 1.0.0
 */
@JsonIgnoreProperties(ignoreUnknown = true)
public record Location(
    @JsonProperty("longitude") Double longitude,
    @JsonProperty("latitude") Double latitude,
    @JsonProperty("horizontal_accuracy") Double horizontalAccuracy,
    @JsonProperty("live_period") Integer livePeriod,
    @JsonProperty("heading") Integer heading,
    @JsonProperty("proximity_alert_radius") Integer proximityAlertRadius
) implements BotApiObject {

    private static final int EARTH_RADIUS_KM = 6371;
    
    /**
     * Вычисляет расстояние до другой точки на карте в километрах.
     * Использует формулу гаверсинусов (Haversine formula).
     *
     * @param other Другая локация.
     * @return Расстояние в километрах или -1.0, если координаты отсутствуют.
     */
    @JsonIgnore
    public double distanceTo(Location other) {
        if (other == null || this.latitude == null || this.longitude == null || 
            other.latitude == null || other.longitude == null) {
            return -1.0;
        }

        double lat1 = Math.toRadians(this.latitude);
        double lon1 = Math.toRadians(this.longitude);
        double lat2 = Math.toRadians(other.latitude);
        double lon2 = Math.toRadians(other.longitude);

        double dLat = lat2 - lat1;
        double dLon = lon2 - lon1;

        double a = Math.sin(dLat / 2) * Math.sin(dLat / 2) +
                   Math.cos(lat1) * Math.cos(lat2) *
                   Math.sin(dLon / 2) * Math.sin(dLon / 2);

        double c = 2 * Math.atan2(Math.sqrt(a), Math.sqrt(1 - a));

        return EARTH_RADIUS_KM * c;
    }
}