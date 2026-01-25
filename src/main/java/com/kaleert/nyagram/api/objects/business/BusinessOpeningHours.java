package com.kaleert.nyagram.api.objects.business;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.kaleert.nyagram.api.meta.BotApiObject;

import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.util.List;

/**
 * Описывает часы работы бизнес-аккаунта.
 *
 * @param timeZoneName Уникальное имя часового пояса, в котором указаны часы работы.
 * @param openingHours Список временных интервалов, когда бизнес открыт.
 *
 * @since 1.0.0
 */
@JsonIgnoreProperties(ignoreUnknown = true)
public record BusinessOpeningHours(
    @JsonProperty("time_zone_name") String timeZoneName,
    @JsonProperty("opening_hours") List<BusinessOpeningHoursInterval> openingHours
) implements BotApiObject {

    /**
     * Проверяет, открыт ли бизнес прямо сейчас (относительно его часового пояса).
     * @return true, если сейчас рабочее время.
     */
    @JsonIgnore
    public boolean isOpenNow() {
        if (openingHours == null || openingHours.isEmpty()) return false;
        
        ZoneId zone = ZoneId.of(timeZoneName);
        ZonedDateTime now = ZonedDateTime.now(zone);
        
        int currentDayOfWeek = now.getDayOfWeek().getValue() - 1; // 0-6
        int currentMinuteOfDay = now.getHour() * 60 + now.getMinute();
        int minutesFromWeekStart = (currentDayOfWeek * 24 * 60) + currentMinuteOfDay;

        return openingHours.stream().anyMatch(interval -> 
            minutesFromWeekStart >= interval.openingMinute() && 
            minutesFromWeekStart < interval.closingMinute()
        );
    }
    
    /**
     * Представляет временной интервал, когда бизнес открыт.
     * <p>
     * Время указано в минутах от начала недели (понедельник 00:00).
     * Значения от 0 до 7 * 24 * 60 = 10080.
     * </p>
     *
     * @param openingMinute Минута открытия (от начала недели).
     * @param closingMinute Минута закрытия (от начала недели).
     */
    @JsonIgnoreProperties(ignoreUnknown = true)
    public record BusinessOpeningHoursInterval(
        @JsonProperty("opening_minute") Integer openingMinute,
        @JsonProperty("closing_minute") Integer closingMinute
    ) implements BotApiObject {
        
        /**
         * Вычисляет продолжительность рабочего интервала в минутах.
         * @return количество минут.
         */        
        @JsonIgnore
        public int getDurationMinutes() {
            return closingMinute - openingMinute;
        }
    }
}