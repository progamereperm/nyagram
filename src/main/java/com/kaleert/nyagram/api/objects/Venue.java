package com.kaleert.nyagram.api.objects;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.kaleert.nyagram.api.meta.BotApiObject;

/**
 * Представляет место (Venue) на карте.
 *
 * @param location Координаты места.
 * @param title Название места.
 * @param address Адрес места.
 * @param foursquareId Foursquare ID (опционально).
 * @param foursquareType Тип Foursquare (опционально).
 * @param googlePlaceId Google Places ID (опционально).
 * @param googlePlaceType Тип Google Places (опционально).
 *
 * @since 1.0.0
 */
@JsonIgnoreProperties(ignoreUnknown = true)
public record Venue(
    @JsonProperty("location") Location location,
    @JsonProperty("title") String title,
    @JsonProperty("address") String address,
    @JsonProperty("foursquare_id") String foursquareId,
    @JsonProperty("foursquare_type") String foursquareType,
    @JsonProperty("google_place_id") String googlePlaceId,
    @JsonProperty("google_place_type") String googlePlaceType
) implements BotApiObject {

    /**
     * Генерирует ссылку на Google Maps для данного места.
     * @return URL строки или null, если координаты отсутствуют.
     */
    @JsonIgnore
    public String getGoogleMapsLink() {
        if (location == null) return null;
        return String.format("https://www.google.com/maps/search/?api=1&query=%f,%f", 
            location.latitude(), location.longitude());
    }
}