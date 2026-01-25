package com.kaleert.nyagram.api.objects.chat;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.kaleert.nyagram.api.meta.BotApiObject;
import lombok.Builder;

/**
 * Представляет чат.
 * <p>
 * Это может быть личный чат с пользователем (Private), группа, супергруппа или канал.
 * </p>
 *
 * @param id Уникальный идентификатор чата (абсолютный).
 * @param type Тип чата: "private", "group", "supergroup" или "channel".
 * @param title Название чата (для групп и каналов).
 * @param username Username чата (для личных чатов, супергрупп и каналов).
 * @param firstName Имя собеседника (для личных чатов).
 * @param lastName Фамилия собеседника (для личных чатов).
 * @param isForum true, если это супергруппа с включенными топиками (форум).
 * 
 * @since 1.0.0
 */
@Builder
@JsonIgnoreProperties(ignoreUnknown = true)
public record Chat(
    @JsonProperty("id") Long id,
    @JsonProperty("type") String type,
    @JsonProperty("title") String title,
    @JsonProperty("username") String username,
    @JsonProperty("first_name") String firstName,
    @JsonProperty("last_name") String lastName,
    @JsonProperty("is_forum") Boolean isForum
) implements BotApiObject {
    
    /**
     * Возвращает уникальный идентификатор чата.
     * @return ID (Long).
     */
    public Long getId() { return id; }
    
    /**
     * Возвращает тип чата.
     * @return "private", "group", "supergroup" или "channel".
     */
    public String getType() { return type; }
    
    /**
     * Возвращает название чата.
     * @return Название (для групп и каналов) или null.
     */
    public String getTitle() { return title; }
    
    /**
     * Возвращает username чата (без @).
     * @return Username или null.
     */
    public String getUsername() { return username; }
    
    /**
     * Возвращает имя собеседника.
     * @return Имя (для личных чатов) или null.
     */
    public String getFirstName() { return firstName; }
    
    /**
     * Возвращает фамилию собеседника.
     * @return Фамилия (для личных чатов) или null.
     */
    public String getLastName() { return lastName; }
    
    /**
     * Возвращает true, если у чата включены топики (форум).
     * @return Boolean.
     */
    public Boolean getIsForum() { return isForum; }

    /**
     * Проверяет, является ли чат личным (Private).
     * @return true, если это диалог с пользователем.
     */
    @JsonIgnore
    public boolean isPrivate() { return "private".equals(type); }
    
    /**
     * Проверяет, является ли чат группой любого типа (Group или Supergroup).
     * @return true, если это групповой чат.
     */
    @JsonIgnore
    public boolean isGroup() { return "group".equals(type) || "supergroup".equals(type); }
    
    /**
     * Проверяет, является ли чат каналом.
     * @return true, если это канал.
     */
    @JsonIgnore
    public boolean isChannel() { return "channel".equals(type); }
    
    /**
     * Проверяет, является ли чат личным (Private).
     * <p>
     * Алиас для метода {@link #isPrivate()}.
     * </p>
     *
     * @return true, если это диалог с пользователем.
     */
    @JsonIgnore
    public boolean isUserChat() { return isPrivate(); }
    
    /**
     * Проверяет, является ли чат обычной группой (не супергруппой).
     * @return true, если тип "group".
     */
    @JsonIgnore
    public boolean isGroupChat() { return "group".equals(type); }
    
    /**
     * Проверяет, является ли чат супергруппой.
     * @return true, если тип "supergroup".
     */
    @JsonIgnore
    public boolean isSuperGroupChat() { return "supergroup".equals(type); }
    
    /**
     * Проверяет, является ли чат каналом (alias для isChannel).
     * @return true, если это канал.
     */
    @JsonIgnore
    public boolean isChannelChat() { return isChannel(); }
    
    /**
     * Возвращает лучшее доступное имя для отображения чата.
     * <p>
     * Для групп и каналов возвращает {@code title}.
     * Для личных чатов возвращает {@code firstName + lastName}.
     * </p>
     *
     * @return Строка с названием.
     */
    @JsonIgnore
    public String getDisplayName() {
        if (title != null && !title.isBlank()) return title;
        if (firstName != null) return (lastName != null) ? firstName + " " + lastName : firstName;
        return "Unknown";
    }
}