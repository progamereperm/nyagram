package com.kaleert.nyagram.api.objects;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.kaleert.nyagram.api.meta.BotApiObject;

/**
 * Представляет телефонный контакт.
 *
 * @param phoneNumber Номер телефона контакта.
 * @param firstName Имя контакта.
 * @param lastName Фамилия контакта (опционально).
 * @param userId Идентификатор пользователя Telegram, если этот контакт зарегистрирован в Telegram.
 * @param vcard Дополнительные данные о контакте в формате vCard.
 *
 * @since 1.0.0
 */
@JsonIgnoreProperties(ignoreUnknown = true)
public record Contact(
    @JsonProperty("phone_number") String phoneNumber,
    @JsonProperty("first_name") String firstName,
    @JsonProperty("last_name") String lastName,
    @JsonProperty("user_id") Long userId,
    @JsonProperty("vcard") String vcard
) implements BotApiObject {

    /**
     * Возвращает полное имя контакта.
     * @return "Имя" или "Имя Фамилия".
     */
    @JsonIgnore
    public String getFullName() {
        if (lastName == null || lastName.isBlank()) return firstName;
        return firstName + " " + lastName;
    }

    /**
     * Создает ссылку на пользователя (tg://user?id=...).
     * @return ссылка или null, если userId отсутствует.
     */
    @JsonIgnore
    public String getTelegramLink() {
        if (userId == null) return null;
        return "tg://user?id=" + userId;
    }
    
    /**
     * Проверяет, связан ли этот контакт с реальным пользователем Telegram.
     * @return true, если есть userId.
     */
    @JsonIgnore
    public boolean isAssociatedWithUser() {
        return userId != null;
    }
}