package com.kaleert.nyagram.util;

import com.kaleert.nyagram.i18n.LocaleService;
import java.time.Duration;
import java.util.Locale;
import lombok.RequiredArgsConstructor;

/**
 * Утилита для форматирования временных интервалов.
 * <p>
 * Преобразует {@link java.time.Duration} в человекочитаемую строку
 * (например, "2 дня 5 часов") с учетом локализации и правил плюрализации
 * (1 день, 2 дня, 5 дней).
 * </p>
 *
 * @since 1.0.0
 */
@RequiredArgsConstructor
public class TimeUtil {

    private static final long SECONDS_IN_MINUTE = 60L;
    private static final long SECONDS_IN_HOUR = 60L * SECONDS_IN_MINUTE;
    private static final long SECONDS_IN_DAY = 24L * SECONDS_IN_HOUR;
    private static final long SECONDS_IN_WEEK = 7L * SECONDS_IN_DAY;
    private static final long SECONDS_IN_MONTH = 30L * SECONDS_IN_DAY;
    private static final long SECONDS_IN_YEAR = 365L * SECONDS_IN_DAY;
    
    private final LocaleService localeService;
    
    /**
     * Форматирует длительность в локализованную строку.
     * <p>
     * Преобразует {@link Duration} в строку вида "2 дня 4 часа", используя ресурсы локализации
     * и правильные формы множественного числа (1 минута, 2 минуты, 5 минут).
     * </p>
     *
     * @param duration Временной интервал.
     * @param localeService Сервис локализации для получения строк.
     * @param locale Целевая локаль.
     * @return Строковое представление времени.
     */
    public static String formatDuration(Duration duration, LocaleService localeService, Locale locale) {
        if (duration == null) {
            return localeService.getTranslation(locale, "time.infinity"); 
        }
        if (duration.isZero() || duration.isNegative()) {
            return localeService.getTranslation(locale, "time.zero"); 
        }
        
        long totalSeconds = duration.getSeconds();
        StringBuilder sb = new StringBuilder();

        long years = totalSeconds / SECONDS_IN_YEAR;
        totalSeconds %= SECONDS_IN_YEAR;
        if (years > 0) {
            appendLocalizedUnit(sb, years, "time.years", localeService, locale);
        }

        long months = totalSeconds / SECONDS_IN_MONTH;
        totalSeconds %= SECONDS_IN_MONTH;
        if (months > 0) {
            appendLocalizedUnit(sb, months, "time.months", localeService, locale);
        }
        
        long weeks = totalSeconds / SECONDS_IN_WEEK;
        totalSeconds %= SECONDS_IN_WEEK;
        if (weeks > 0) {
            appendLocalizedUnit(sb, weeks, "time.weeks", localeService, locale);
        }
        
        long days = totalSeconds / SECONDS_IN_DAY;
        totalSeconds %= SECONDS_IN_DAY;
        if (days > 0) {
            appendLocalizedUnit(sb, days, "time.days", localeService, locale);
        }
        
        long hours = totalSeconds / SECONDS_IN_HOUR;
        totalSeconds %= SECONDS_IN_HOUR;
        if (hours > 0) {
            appendLocalizedUnit(sb, hours, "time.hours", localeService, locale);
        }
        
        long minutes = totalSeconds / SECONDS_IN_MINUTE;
        totalSeconds %= SECONDS_IN_MINUTE;
        if (minutes > 0) {
            appendLocalizedUnit(sb, minutes, "time.minutes", localeService, locale);
        }
        
        long seconds = totalSeconds;
        if (seconds > 0) {
            appendLocalizedUnit(sb, seconds, "time.seconds", localeService, locale);
        }
        
        return sb.toString().trim();
    }
    
    private static void appendLocalizedUnit(
            StringBuilder sb, 
            long amount, 
            String baseKey, 
            LocaleService localeService, 
            Locale locale
    ) {
        String pluralKey = getPluralFormKey(amount);
        String translationKey = baseKey + "." + pluralKey;
        String unitString = localeService.getTranslation(locale, translationKey);
        
        sb.append(amount).append(unitString).append(" ");
    }
    
    private static String getPluralFormKey(long number) {
        if (number < 0) {
            number = 0;
        }

        long n100 = number % 100;
        if (n100 >= 11 && n100 <= 19) {
            return "many"; // Form 3
        }
        
        long n10 = number % 10;
        
        if (n10 == 1) {
            return "one"; // Form 1: 1, 21, 31
        }
        
        if (n10 >= 2 && n10 <= 4) {
            return "few"; // Form 2: 2, 3, 4, 22, 23, 24
        }
        
        return "many"; // Form 3: 0, 5, 6...
    }
}