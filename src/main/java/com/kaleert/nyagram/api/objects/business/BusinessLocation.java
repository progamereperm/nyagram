package com.kaleert.nyagram.api.objects.business;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.kaleert.nyagram.api.meta.BotApiObject;
import com.kaleert.nyagram.api.objects.Location;

/**
 * Содержит информацию о местоположении бизнес-аккаунта.
 *
 * @param address Текстовый адрес бизнеса.
 * @param location Географические координаты бизнеса.
 *
 * @since 1.0.0
 */
@JsonIgnoreProperties(ignoreUnknown = true)
public record BusinessLocation(
    @JsonProperty("address") String address,
    @JsonProperty("location") Location location
) implements BotApiObject {
    
    /**
     * Проверяет, установлена ли геолокация.
     * @return true, если координаты заданы.
     */
    @JsonIgnore
    public boolean hasGeoLocation() {
        return location != null;
    }
    
    /**
     * Генерирует ссылку на Google Maps.
     * Если есть координаты, использует их. Если нет — ищет по текстовому адресу.
     *
     * @return URL ссылка или null, если адрес и локация отсутствуют.
     */
    @JsonIgnore
    public String getGoogleMapsLink() {
        if (location != null) {
            return String.format("https://www.google.com/maps/search/?api=1&query=%s,%s", 
                location.latitude(), location.longitude());
        }
        if (address != null && !address.isBlank()) {
            return "https://www.google.com/maps/search/?api=1&query=" + address.replace(" ", "+");
        }
        return null;
    }
}