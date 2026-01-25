package com.kaleert.nyagram.api.objects;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.kaleert.nyagram.api.meta.BotApiObject;
import lombok.Builder;

/**
 * Представляет пользователя Telegram или бота.
 *
 * @param id Уникальный идентификатор пользователя или бота.
 * @param isBot true, если пользователь является ботом.
 * @param firstName Имя пользователя или бота.
 * @param lastName Фамилия (опционально).
 * @param username Username (без @, опционально).
 * @param languageCode IETF языковой тег (например, "ru", "en").
 * @param isPremium true, если у пользователя есть Telegram Premium.
 * @param addedToAttachmentMenu true, если бот добавлен в меню вложений.
 * @param canJoinGroups true, если бота можно приглашать в группы (возвращается только в getMe).
 * @param canReadAllGroupMessages true, если режим приватности отключен (возвращается только в getMe).
 * @param supportsInlineQueries true, если бот поддерживает inline-запросы.
 * @param canConnectToBusiness true, если бот может быть подключен к бизнес-аккаунту.
 * @param hasMainWebApp true, если у бота есть настроенное Web App.
 * 
 * @since 1.0.0
 */
@Builder
@JsonIgnoreProperties(ignoreUnknown = true)
public record User(
    @JsonProperty("id") Long id,
    @JsonProperty("is_bot") Boolean isBot,
    @JsonProperty("first_name") String firstName,
    @JsonProperty("last_name") String lastName,
    @JsonProperty("username") String username,
    @JsonProperty("language_code") String languageCode,
    @JsonProperty("is_premium") Boolean isPremium,
    @JsonProperty("added_to_attachment_menu") Boolean addedToAttachmentMenu,
    @JsonProperty("can_join_groups") Boolean canJoinGroups,
    @JsonProperty("can_read_all_group_messages") Boolean canReadAllGroupMessages,
    @JsonProperty("supports_inline_queries") Boolean supportsInlineQueries,
    @JsonProperty("can_connect_to_business") Boolean canConnectToBusiness,
    @JsonProperty("has_main_web_app") Boolean hasMainWebApp
) implements BotApiObject {
    
    /**
     * Возвращает уникальный идентификатор пользователя или бота.
     * @return ID (Long).
     */
    public Long getId() { return id; }
    
    /**
     * Возвращает имя пользователя (без @).
     * @return Username или null, если он не установлен.
     */
    public String getUsername() { return username; }
    
    /**
     * Возвращает имя пользователя.
     * @return Имя.
     */
    public String getFirstName() { return firstName; }
    
    /**
     * Возвращает фамилию пользователя.
     * @return Фамилия или null.
     */
    public String getLastName() { return lastName; }
    
    /**
     * Возвращает код языка (IETF tag), например "ru", "en".
     * @return Код языка или null.
     */
    public String getLanguageCode() { return languageCode; }
    
    /**
     * Возвращает true, если это бот.
     * @return Boolean.
     */
    public Boolean getIsBot() { return isBot; }
    
    /**
     * Возвращает true, если у пользователя есть Telegram Premium.
     * @return Boolean.
     */
    public Boolean getIsPremium() { return isPremium; }
    
    /**
     * Проверяет, добавлен ли бот в меню вложений.
     *
     * @return true, если добавлен.
     */
    public Boolean getAddedToAttachmentMenu() { return addedToAttachmentMenu; }
    
    /**
     * Проверяет, можно ли приглашать бота в группы.
     * (Только для getMe).
     *
     * @return true, если можно.
     */
    public Boolean getCanJoinGroups() { return canJoinGroups; }
    
    /**
     * Проверяет, отключен ли у бота режим приватности (читает ли он все сообщения).
     * (Только для getMe).
     *
     * @return true, если Privacy Mode выключен.
     */
    public Boolean getCanReadAllGroupMessages() { return canReadAllGroupMessages; }
    
    /**
     * Возвращает true, если бот поддерживает Inline-режим.
     * (Только для getMe).
     * @return Boolean.
     */
    public Boolean getSupportsInlineQueries() { return supportsInlineQueries; }
    
    /**
     * Проверяет, может ли бот быть подключен к бизнес-аккаунту.
     *
     * @return true, если может.
     */
    public Boolean getCanConnectToBusiness() { return canConnectToBusiness; }
    
    /**
     * Возвращает true, если у бота настроено главное Web App (кнопка меню).
     * (Только для getMe).
     * @return Boolean.
     */
    public Boolean getHasMainWebApp() { return hasMainWebApp; }
    
    /**
     * Возвращает полное имя пользователя (Имя + Фамилия).
     * @return строка с полным именем.
     */
    public String getFullName() {
        if (lastName != null && !lastName.isBlank()) {
            return firstName + " " + lastName;
        }
        return firstName != null ? firstName : "";
    }
    
    /**
     * Возвращает упоминание пользователя (@username или HTML-ссылка).
     * @return строка упоминания.
     */
    public String getMention() {
        if (username != null && !username.isBlank()) {
            return "@" + username;
        }
        return getHtmlLink();
    }
    
    /**
     * Возвращает HTML-ссылку на пользователя (tg://user?id=...).
     * @return HTML строка.
     */
    public String getHtmlLink() {
        return String.format("<a href=\"tg://user?id=%d\">%s</a>", id, escapeHtml(getFullName()));
    }
    
    /**
     * Генерирует ссылку для открытия профиля пользователя в приложении Telegram (tg://).
     * <p>
     * Ссылка вида {@code tg://user?id=123456789}. Работает даже если у пользователя нет юзернейма.
     * </p>
     *
     * @return строка deep-link.
     */
    public String getDeepLink() {
        return "tg://user?id=" + id;
    }
    
    /**
     * Безопасная проверка на то, является ли пользователь ботом.
     * Обрабатывает null (считает false).
     *
     * @return true, если пользователь - бот.
     */
    public boolean checkIsBot() {
        return Boolean.TRUE.equals(isBot);
    }
    
    /**
     * Безопасная проверка на наличие Premium подписки.
     * Обрабатывает null (считает false).
     *
     * @return true, если у пользователя есть Telegram Premium.
     */
    public boolean checkIsPremium() {
        return Boolean.TRUE.equals(isPremium);
    }

    private String escapeHtml(String text) {
        if (text == null) return "";
        return text.replace("&", "&amp;")
                   .replace("<", "&lt;")
                   .replace(">", "&gt;")
                   .replace("\"", "&quot;");
    }
}