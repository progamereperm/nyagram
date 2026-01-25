package com.kaleert.nyagram.validation;

import lombok.experimental.UtilityClass;
import lombok.extern.slf4j.Slf4j;

import java.util.regex.Pattern;

/**
 * Утилитарный класс, предоставляющий статические методы для валидации различных форматов данных.
 * <p>
 * Используется для проверки строк, чисел и специфичных для Telegram форматов
 * (username, deep links и т.д.) в {@link com.kaleert.nyagram.middleware.ValidationMiddleware}.
 * </p>
 *
 * @since 1.0.0
 */
@Slf4j
@UtilityClass
public class Validator {

    private static final Pattern USERNAME_PATTERN = Pattern.compile("^[a-zA-Z0-9_]{5,32}$");
    private static final Pattern EMAIL_PATTERN = Pattern.compile("^[A-Za-z0-9+_.-]+@[A-Za-z0-9.-]+\\.[A-Za-z]{2,}$");
    private static final Pattern PHONE_PATTERN = Pattern.compile("^\\+?[1-9]\\d{1,14}$");
    private static final Pattern URL_PATTERN = Pattern.compile("^(https?|ftp)://[^\\s/$.?#].[^\\s]*$");
    private static final Pattern HASHTAG_PATTERN = Pattern.compile("^#[a-zA-Z0-9_]+$");
    private static final Pattern MENTION_PATTERN = Pattern.compile("^@[a-zA-Z0-9_]{5,32}$");
    private static final Pattern DATE_PATTERN = Pattern.compile("^\\d{4}-\\d{2}-\\d{2}$");
    private static final Pattern TIME_PATTERN = Pattern.compile("^\\d{2}:\\d{2}(:\\d{2})?$");
    
    /**
     * Проверяет соответствие строки произвольному регулярному выражению.
     *
     * @param value Проверяемая строка.
     * @param pattern Регулярное выражение.
     * @return true, если строка соответствует шаблону.
     */
    public static boolean matchesPattern(String value, String pattern) {
        if (value == null || pattern == null) {
            return false;
        }
        try {
            return Pattern.compile(pattern).matcher(value).matches();
        } catch (Exception e) {
            log.warn("Invalid regex pattern: {}", pattern);
            return false;
        }
    }
    
    /**
     * Проверяет, находится ли длина строки в заданном диапазоне.
     *
     * @param value Строка.
     * @param min Минимальная длина.
     * @param max Максимальная длина.
     * @return true, если длина корректна.
     */
    public static boolean validateLength(String value, int min, int max) {
        if (value == null) return false;
        int length = value.length();
        return length >= min && length <= max;
    }
    
    /**
     * Проверяет, находится ли числовое значение в заданном диапазоне.
     *
     * @param value Число.
     * @param min Минимум.
     * @param max Максимум.
     * @return true, если значение в диапазоне.
     */
    public static boolean validateRange(Number value, Number min, Number max) {
        if (value == null) return false;
        double doubleValue = value.doubleValue();
        return doubleValue >= min.doubleValue() && doubleValue <= max.doubleValue();
    }
    
    /**
     * Проверяет валидность юзернейма Telegram.
     * <p>
     * Правила: 5-32 символа, a-z, 0-9, подчеркивание. Не может состоять только из цифр.
     * </p>
     *
     * @param username Юзернейм (с @ или без).
     * @return true, если валиден.
     */
    public static boolean isValidTelegramUsername(String username) {
        if (username == null) return false;
        
        String cleanUsername = username.startsWith("@") ? username.substring(1) : username;
        
        return USERNAME_PATTERN.matcher(cleanUsername).matches() && 
               !cleanUsername.matches("^[0-9]+$"); // Не может состоять только из цифр
    }
    
    /**
     * Проверяет валидность email адреса.
     *
     * @param email Строка email.
     * @return true, если формат email верный.
     */
    public static boolean isValidEmail(String email) {
        return email != null && EMAIL_PATTERN.matcher(email).matches();
    }
    
    /**
     * Проверяет валидность номера телефона (международный формат).
     *
     * @param phone Номер телефона.
     * @return true, если формат верный.
     */
    public static boolean isValidPhoneNumber(String phone) {
        return phone != null && PHONE_PATTERN.matcher(phone).matches();
    }
    
    /**
     * Проверяет валидность URL адреса (http/https/ftp).
     *
     * @param url Ссылка.
     * @return true, если URL валиден.
     */
    public static boolean isValidUrl(String url) {
        return url != null && URL_PATTERN.matcher(url).matches();
    }
    
    /**
     * Проверяет, является ли строка валидным хэштегом.
     * <p>
     * Хэштег должен начинаться с {@code #} и содержать буквы, цифры или подчеркивания.
     * </p>
     *
     * @param hashtag Строка.
     * @return true, если это хэштег.
     */
    public static boolean isValidHashtag(String hashtag) {
        return hashtag != null && HASHTAG_PATTERN.matcher(hashtag).matches();
    }
    
    /**
     * Проверяет, является ли строка валидным упоминанием пользователя.
     * <p>
     * Упоминание должно начинаться с {@code @} и соответствовать правилам юзернейма.
     * </p>
     *
     * @param mention Строка.
     * @return true, если это упоминание.
     */
    public static boolean isValidMention(String mention) {
        return mention != null && MENTION_PATTERN.matcher(mention).matches();
    }
    
    /**
     * Проверяет, соответствует ли строка формату даты (YYYY-MM-DD).
     *
     * @param date Строка с датой.
     * @return true, если формат верный.
     */
    public static boolean isValidDate(String date) {
        return date != null && DATE_PATTERN.matcher(date).matches();
    }
    
    /**
     * Проверяет, соответствует ли строка формату времени (HH:mm или HH:mm:ss).
     *
     * @param time Строка времени.
     * @return true, если формат верный.
     */
    public static boolean isValidTime(String time) {
        return time != null && TIME_PATTERN.matcher(time).matches();
    }
    
    /**
     * Проверяет, что строка не является null, пустой или состоящей только из пробелов.
     *
     * @param value Проверяемая строка.
     * @return true, если в строке есть хотя бы один значимый символ.
     */
    public static boolean isNotBlank(String value) {
        return value != null && !value.trim().isEmpty();
    }
    
    /**
     * Проверяет, содержит ли строка только буквы (алфавитные символы).
     * <p>
     * Поддерживает латиницу, кириллицу и пробелы.
     * </p>
     *
     * @param value Проверяемая строка.
     * @return true, если строка состоит только из букв и пробелов.
     */
    public static boolean isAlphabetic(String value) {
        return value != null && value.matches("^[a-zA-Zа-яА-ЯёЁ\\s]+$");
    }
    
    /**
     * Проверяет, состоит ли строка только из цифр.
     *
     * @param value Проверяемая строка.
     * @return true, если строка содержит только цифры.
     */
    public static boolean isNumeric(String value) {
        return value != null && value.matches("^[0-9]+$");
    }
    
    /**
     * Проверяет, содержит ли строка только буквы и цифры.
     * <p>
     * Поддерживает латиницу, кириллицу, цифры и пробелы.
     * </p>
     *
     * @param value Проверяемая строка.
     * @return true, если строка состоит из букв, цифр и пробелов.
     */
    public static boolean isAlphanumeric(String value) {
        return value != null && value.matches("^[a-zA-Zа-яА-ЯёЁ0-9\\s]+$");
    }
}