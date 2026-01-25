package com.kaleert.nyagram.api.objects.chat;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.kaleert.nyagram.api.meta.BotApiObject;
import com.kaleert.nyagram.api.objects.*;
import com.kaleert.nyagram.api.objects.business.*;
import com.kaleert.nyagram.api.objects.message.Message;
import com.kaleert.nyagram.api.objects.reactions.ReactionType;
import java.util.List;
import java.util.Optional;
import com.fasterxml.jackson.annotation.JsonIgnore;

/**
 * Содержит полную информацию о чате.
 * <p>
 * Этот объект возвращается методом {@code getChat}. Он содержит расширенные данные,
 * такие как описание, закрепленное сообщение, права, фото и настройки.
 * </p>
 *
 * @param id Уникальный идентификатор чата.
 * @param type Тип чата: "private", "group", "supergroup" или "channel".
 * @param title Название чата (для супергрупп, каналов и групп).
 * @param username Username чата (для приватных чатов, супергрупп и каналов, если установлен).
 * @param firstName Имя пользователя (для приватных чатов).
 * @param lastName Фамилия пользователя (для приватных чатов).
 * @param isForum True, если в супергруппе включены топики (форум).
 * @param photo Фотография профиля чата.
 * @param activeUsernames Список всех активных юзернеймов чата (включая основной).
 * @param birthdate Дата рождения пользователя (для приватных чатов).
 * @param businessIntro Приветствие для бизнес-аккаунта (заголовок, текст, стикер).
 * @param businessLocation Местоположение бизнеса.
 * @param businessOpeningHours Часы работы бизнеса.
 * @param personalChat Личный чат пользователя, если это бизнес-аккаунт.
 * @param bio Биография пользователя (для приватных чатов).
 * @param description Описание чата (для групп, супергрупп и каналов).
 * @param inviteLink Основная пригласительная ссылка (для групп, супергрупп и каналов).
 * @param pinnedMessage Закрепленное сообщение. Может отсутствовать, если сообщение удалено.
 * @param permissions Права по умолчанию для всех участников (для групп и супергрупп).
 * @param slowModeDelay Задержка медленного режима в секундах (для супергрупп).
 * @param messageAutoDeleteTime Время автоудаления сообщений в секундах.
 * @param hasAggressiveAntiSpamEnabled True, если включен агрессивный анти-спам фильтр.
 * @param hasHiddenMembers True, если список участников скрыт.
 * @param hasProtectedContent True, если в чате включена защита контента (запрет пересылки).
 * @param hasVisibleHistory True, если новые участники видят историю сообщений.
 * @param stickerSetName Название набора стикеров группы.
 * @param canSetStickerSet True, если бот может изменить набор стикеров группы.
 * @param linkedChatId ID привязанного канала (для супергрупп) или группы обсуждения (для каналов).
 * @param location Геолокация, к которой привязан чат (для супергрупп).
 * @param availableReactions Список разрешенных реакций. Если null, разрешены все.
 * @param accentColorId ID цвета акцента (имени/фона).
 * @param backgroundCustomEmojiId ID кастомного эмодзи для фона сообщения.
 * @param profileAccentColorId ID цвета акцента профиля.
 * @param profileBackgroundCustomEmojiId ID кастомного эмодзи для фона профиля.
 * @param emojiStatusCustomEmojiId ID кастомного эмодзи статуса.
 * @param emojiStatusExpirationDate Дата истечения срока действия статуса.
 *
 * @since 1.0.0
 */
@JsonIgnoreProperties(ignoreUnknown = true)
public record ChatFullInfo(
    @JsonProperty("id") Long id,
    @JsonProperty("type") String type,
    @JsonProperty("title") String title,
    @JsonProperty("username") String username,
    @JsonProperty("first_name") String firstName,
    @JsonProperty("last_name") String lastName,
    @JsonProperty("is_forum") Boolean isForum,
    @JsonProperty("photo") ChatPhoto photo,
    @JsonProperty("active_usernames") List<String> activeUsernames,
    @JsonProperty("birthdate") Birthdate birthdate,
    @JsonProperty("business_intro") BusinessIntro businessIntro,
    @JsonProperty("business_location") BusinessLocation businessLocation,
    @JsonProperty("business_opening_hours") BusinessOpeningHours businessOpeningHours,
    @JsonProperty("personal_chat") Chat personalChat,
    @JsonProperty("bio") String bio,
    @JsonProperty("description") String description,
    @JsonProperty("invite_link") String inviteLink,
    @JsonProperty("pinned_message") Message pinnedMessage,
    @JsonProperty("permissions") ChatPermissions permissions,
    @JsonProperty("slow_mode_delay") Integer slowModeDelay,
    @JsonProperty("message_auto_delete_time") Integer messageAutoDeleteTime,
    @JsonProperty("has_aggressive_anti_spam_enabled") Boolean hasAggressiveAntiSpamEnabled,
    @JsonProperty("has_hidden_members") Boolean hasHiddenMembers,
    @JsonProperty("has_protected_content") Boolean hasProtectedContent,
    @JsonProperty("has_visible_history") Boolean hasVisibleHistory,
    @JsonProperty("sticker_set_name") String stickerSetName,
    @JsonProperty("can_set_sticker_set") Boolean canSetStickerSet,
    @JsonProperty("linked_chat_id") Long linkedChatId,
    @JsonProperty("location") ChatLocation location,
    @JsonProperty("available_reactions") List<ReactionType> availableReactions,
    @JsonProperty("accent_color_id") Integer accentColorId,
    @JsonProperty("background_custom_emoji_id") String backgroundCustomEmojiId,
    @JsonProperty("profile_accent_color_id") Integer profileAccentColorId,
    @JsonProperty("profile_background_custom_emoji_id") String profileBackgroundCustomEmojiId,
    @JsonProperty("emoji_status_custom_emoji_id") String emojiStatusCustomEmojiId,
    @JsonProperty("emoji_status_expiration_date") Long emojiStatusExpirationDate
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
     * Возвращает название чата (для групп, супергрупп и каналов).
     * @return Название или null.
     */
    public String getTitle() { return title; }
    
    /**
     * Возвращает username чата (без @).
     * @return Username или null.
     */
    public String getUsername() { return username; }
    
    /**
     * Возвращает имя пользователя (для личных чатов).
     * @return имя.
     */
    public String getFirstName() { return firstName; }
    
    /**
     * Возвращает фамилию собеседника (для личных чатов).
     * @return Фамилия или null.
     */
    public String getLastName() { return lastName; }

    /**
     * Возвращает true, если у чата включены топики (форум).
     * @return Boolean.
     */
    public Boolean getIsForum() { return isForum; }

    /**
     * Возвращает фотографию профиля чата.
     * @return объект ChatPhoto или null.
     */
    public ChatPhoto getPhoto() { return photo; }
    
    /**
     * Возвращает список активных юзернеймов чата.
     * @return список строк или null.
     */
    public List<String> getActiveUsernames() { return activeUsernames; }
    
    /**
     * Возвращает дату рождения (для личных чатов).
     * @return объект Birthdate или null.
     */
    public Birthdate getBirthdate() { return birthdate; }
    
    /**
     * Возвращает приветствие (Intro) для бизнес-аккаунта.
     * <p>
     * Содержит заголовок, текст и стикер, которые видит пользователь при открытии чата.
     * </p>
     *
     * @return объект BusinessIntro или null.
     */
    public BusinessIntro getBusinessIntro() { return businessIntro; }
    
    /**
     * Возвращает местоположение бизнеса.
     *
     * @return объект BusinessLocation или null.
     */
    public BusinessLocation getBusinessLocation() { return businessLocation; }
    
    /**
     * Возвращает часы работы бизнеса.
     *
     * @return объект BusinessOpeningHours или null.
     */
    public BusinessOpeningHours getBusinessOpeningHours() { return businessOpeningHours; }
    
    /**
     * Возвращает личный чат пользователя, которому принадлежит этот бизнес-аккаунт.
     * <p>
     * Поле актуально только для чатов с бизнес-аккаунтами.
     * </p>
     *
     * @return объект Chat или null.
     */
    public Chat getPersonalChat() { return personalChat; }
    
    /**
     * Возвращает биографию (описание) пользователя (для личных чатов).
     * @return текст или null.
     */
    public String getBio() { return bio; }
    
    /**
     * Возвращает описание чата (для групп, супергрупп и каналов).
     * @return текст описания или null.
     */
    public String getDescription() { return description; }
    
    /**
     * Возвращает основную пригласительную ссылку для чата.
     * <p>
     * Для групп, супергрупп и каналов.
     * </p>
     *
     * @return URL ссылки или null.
     */
    public String getInviteLink() { return inviteLink; }
    
    /**
     * Возвращает самое последнее закрепленное сообщение.
     * <p>
     * Может быть недоступно, если сообщение было удалено или скрыто.
     * </p>
     *
     * @return объект Message или null.
     */
    public Message getPinnedMessage() { return pinnedMessage; }

    /**
     * Возвращает права по умолчанию для всех участников.
     * <p>
     * Актуально для групп и супергрупп.
     * </p>
     *
     * @return объект ChatPermissions или null.
     */
    public ChatPermissions getPermissions() { return permissions; }

    /**
     * Возвращает задержку медленного режима (Slow Mode) в секундах.
     *
     * @return секунды (0-21600) или null.
     */
    public Integer getSlowModeDelay() { return slowModeDelay; }

    /**
     * Возвращает время автоудаления сообщений в секундах.
     *
     * @return секунды или null, если автоудаление выключено.
     */
    public Integer getMessageAutoDeleteTime() { return messageAutoDeleteTime; }
    
    /**
     * Возвращает true, если в супергруппе включен агрессивный анти-спам фильтр.
     *
     * @return Boolean.
     */
    public Boolean getHasAggressiveAntiSpamEnabled() { return hasAggressiveAntiSpamEnabled; }
    
    /**
     * Возвращает true, если список участников скрыт (доступен только админам).
     *
     * @return Boolean.
     */
    public Boolean getHasHiddenMembers() { return hasHiddenMembers; }
    
    /**
     * Возвращает true, если в чате включена защита контента (запрет пересылки и сохранения).
     *
     * @return Boolean.
     */
    public Boolean getHasProtectedContent() { return hasProtectedContent; }
    
    /**
     * Возвращает true, если новые участники видят историю сообщений (для супергрупп).
     *
     * @return Boolean.
     */
    public Boolean getHasVisibleHistory() { return hasVisibleHistory; }
    
    /**
     * Возвращает название набора стикеров группы.
     *
     * @return short name набора или null.
     */
    public String getStickerSetName() { return stickerSetName; }
    
    /**
     * Возвращает true, если бот может изменить набор стикеров этой группы.
     *
     * @return Boolean.
     */
    public Boolean getCanSetStickerSet() { return canSetStickerSet; }
    
    /**
     * Возвращает ID привязанного чата.
     * <p>
     * Для супергрупп — это ID канала, к которому привязана группа.
     * Для каналов — это ID группы обсуждений.
     * </p>
     *
     * @return ID чата или null.
     */
    public Long getLinkedChatId() { return linkedChatId; }

    /**
     * Возвращает геолокацию, к которой привязан чат.
     * <p>
     * Актуально для супергрупп с привязкой к месту.
     * </p>
     *
     * @return объект ChatLocation или null.
     */
    public ChatLocation getLocation() { return location; }
    
    /**
     * Возвращает список доступных реакций в чате.
     * Если null или пусто, доступны все реакции.
     *
     * @return список типов реакций.
     */
    public List<ReactionType> getAvailableReactions() { return availableReactions; }
    
    /**
     * Возвращает ID цвета акцента (имени пользователя/названия чата).
     *
     * @return числовой идентификатор цвета.
     */
    public Integer getAccentColorId() { return accentColorId; }
    
    /**
     * Возвращает ID кастомного эмодзи, используемого для фона сообщения.
     *
     * @return идентификатор эмодзи или null.
     */
    public String getBackgroundCustomEmojiId() { return backgroundCustomEmojiId; }
    
    /**
     * Возвращает ID цвета акцента профиля.
     * <p>
     * Определяет цвет фона профиля чата/пользователя.
     * </p>
     *
     * @return ID цвета.
     */
    public Integer getProfileAccentColorId() { return profileAccentColorId; }

    /**
     * Возвращает ID кастомного эмодзи для фона профиля.
     *
     * @return ID эмодзи.
     */
    public String getProfileBackgroundCustomEmojiId() { return profileBackgroundCustomEmojiId; }
    
    /**
     * Возвращает идентификатор кастомного эмодзи, установленного как статус чата.
     *
     * @return ID эмодзи или null.
     */
    public String getEmojiStatusCustomEmojiId() { return emojiStatusCustomEmojiId; }
    
    /**
     * Возвращает дату истечения срока действия эмодзи-статуса (Unix timestamp).
     *
     * @return timestamp или null, если статус бессрочный или отсутствует.
     */
    public Long getEmojiStatusExpirationDate() { return emojiStatusExpirationDate; }
    
    /**
     * Возвращает отображаемое имя чата.
     * <p>
     * Если это группа/канал — возвращает {@code title}.
     * Если это личный чат — возвращает {@code First Last} или просто {@code First}.
     * </p>
     *
     * @return название чата.
     */
    @JsonIgnore
    public String getDisplayName() {
        if (title != null && !title.isBlank()) {
            return title;
        }
        if (firstName != null) {
            return (lastName != null) ? firstName + " " + lastName : firstName;
        }
        return "Unknown Chat";
    }
    
    /**
     * Возвращает HTML-ссылку на чат или пользователя.
     * <p>
     * Если есть username: {@code <a href="https://t.me/username">Name</a>}.
     * Если нет username (только для личных): {@code <a href="tg://user?id=123">Name</a>}.
     * Иначе просто жирный текст с названием.
     * </p>
     *
     * @return HTML строка.
     */
    @JsonIgnore
    public String getHtmlLink() {
        String name = escapeHtml(getDisplayName());
        if (username != null && !username.isBlank()) {
            return String.format("<a href=\"https://t.me/%s\">%s</a>", username, name);
        }
        if (isUserChat()) {
            return String.format("<a href=\"tg://user?id=%d\">%s</a>", id, name);
        }
        return "<b>" + name + "</b>";
    }
    
    /**
     * Возвращает упоминание чата/пользователя.
     * <p>
     * Если есть username, возвращает {@code @username}.
     * Если нет — возвращает HTML-ссылку (для пользователей) или название (для чатов).
     * </p>
     *
     * @return Строка упоминания.
     */
    @JsonIgnore
    public String getMention() {
        if (username != null && !username.isBlank()) {
            return "@" + username;
        }
        if (isUserChat()) {
            return getHtmlLink();
        }
        return getDisplayName();
    }
    
    /**
     * Возвращает полное имя пользователя (Имя + Фамилия).
     *
     * @return строка "Имя Фамилия" или просто "Имя".
     */
    @JsonIgnore
    public String getFullName() {
        if (firstName != null && lastName != null) {
            return firstName + " " + lastName;
        }
        return firstName != null ? firstName : "";
    }
    
    /**
     * Возвращает основной юзернейм чата.
     * <p>
     * Пытается вернуть {@code username}, если он есть.
     * Если нет, ищет первый в списке {@code active_usernames}.
     * </p>
     *
     * @return Optional с юзернеймом.
     */
    @JsonIgnore
    public Optional<String> getPrimaryUsername() {
        if (username != null && !username.isBlank()) {
            return Optional.of(username);
        }
        if (activeUsernames != null && !activeUsernames.isEmpty()) {
            return Optional.of(activeUsernames.get(0));
        }
        return Optional.empty();
    }
    
    /**
     * Генерирует Deep-link ссылку для перехода в этот чат.
     * <p>
     * Использует формат {@code tg://resolve?domain=username}.
     * Если юзернейма нет, использует ID (но это работает не во всех клиентах).
     * </p>
     *
     * @return строка ссылки.
     */
    @JsonIgnore
    public String getDeepLink() {
        return "tg://resolve?domain=" + getPrimaryUsername().orElse(String.valueOf(id));
    }
    
    /**
     * Проверяет, установлено ли фото чата.
     * @return true, если photo != null.
     */
    @JsonIgnore
    public boolean hasPhoto() {
        return photo != null;
    }
    
    /**
     * Проверяет наличие биографии (для пользователей).
     * @return true, если bio не пустое.
     */
    @JsonIgnore
    public boolean hasBio() {
        return bio != null && !bio.isBlank();
    }
    
    /**
     * Проверяет наличие описания чата.
     * @return true, если description не пустое.
     */
    @JsonIgnore
    public boolean hasDescription() {
        return description != null && !description.isBlank();
    }
    
    /**
     * Проверяет наличие основной пригласительной ссылки.
     * @return true, если inviteLink не пустой.
     */
    @JsonIgnore
    public boolean hasInviteLink() {
        return inviteLink != null && !inviteLink.isBlank();
    }
    
    /**
     * Проверяет, есть ли в чате закрепленное сообщение.
     * @return true, если pinnedMessage != null.
     */
    @JsonIgnore
    public boolean hasPinnedMessage() {
        return pinnedMessage != null;
    }
    
    /**
     * Проверяет, включен ли в чате медленный режим (Slow Mode).
     * @return true, если {@code slowModeDelay} больше 0.
     */
    @JsonIgnore
    public boolean isSlowModeEnabled() {
        return slowModeDelay != null && slowModeDelay > 0;
    }
    
    /**
     * Проверяет, включен ли в чате таймер автоудаления сообщений.
     * @return true, если {@code messageAutoDeleteTime} больше 0.
     */
    @JsonIgnore
    public boolean isMessageAutoDeleteEnabled() {
        return messageAutoDeleteTime != null && messageAutoDeleteTime > 0;
    }
    
    /**
     * Проверяет, является ли чат личным (Private).
     * @return true, если тип "private".
     */
    @JsonIgnore
    public boolean isPrivate() {
        return "private".equals(type);
    }
    
    /**
     * Проверяет, является ли чат диалогом с пользователем.
     * Алиас для {@link #isPrivate()}.
     * @return true, если это личный чат.
     */
    @JsonIgnore
    public boolean isUserChat() {
        return "private".equals(type);
    }
    
    /**
     * Проверяет, является ли чат обычной группой (Group).
     * @return true, если тип "group".
     */
    @JsonIgnore
    public boolean isGroupChat() {
        return "group".equals(type);
    }
    
    /**
     * Проверяет, является ли чат каналом.
     * @return true, если тип "channel".
     */
    @JsonIgnore
    public boolean isChannelChat() {
        return "channel".equals(type);
    }
    
    /**
     * Проверяет, является ли чат супергруппой.
     * @return true, если тип "supergroup".
     */
    @JsonIgnore
    public boolean isSuperGroupChat() {
        return "supergroup".equals(type);
    }
    
    /**
     * Проверяет, включены ли в супергруппе топики (Forum feature).
     * @return true, если это форум.
     */
    @JsonIgnore
    public Boolean isForum() {
        return Boolean.TRUE.equals(isForum);
    }
    
    /**
     * Проверяет, была ли эта группа мигрирована в супергруппу.
     * <p>
     * Для обычных групп {@code linkedChatId} обычно указывает на ID новой супергруппы.
     * </p>
     * @return true, если группа мигрировала.
     */
    @JsonIgnore
    public boolean isMigrated() {
        return "group".equals(type) && linkedChatId != null; 
    }
    
    /**
     * Проверяет, есть ли у чата список активных юзернеймов.
     * @return true, если список не пуст.
     */
    @JsonIgnore
    public boolean hasActiveUsernames() {
        return activeUsernames != null && !activeUsernames.isEmpty();
    }
    
    /**
     * Проверяет, ограничен ли список доступных реакций в чате.
     * <p>
     * Если возвращает true, значит администраторы настроили конкретный набор разрешенных реакций.
     * Если false — доступны все реакции (или поле не пришло).
     * </p>
     * @return true, если есть список доступных реакций.
     */
    @JsonIgnore
    public boolean hasAvailableReactions() {
        return availableReactions != null && !availableReactions.isEmpty();
    }
    
    /**
     * Проверяет наличие бизнес-информации (Intro, Location или Opening Hours).
     * @return true, если хотя бы одно из полей задано.
     */
    @JsonIgnore
    public boolean hasBusinessInfo() {
        return businessIntro != null || businessLocation != null || businessOpeningHours != null;
    }
    
    /**
     * Проверяет, привязана ли к чату геолокация.
     * @return true, если location != null.
     */
    @JsonIgnore
    public boolean hasLocation() {
        return location != null;
    }
    
    /**
     * Проверяет, установлен ли кастомный эмодзи-фон для сообщений.
     * @return true, если backgroundCustomEmojiId задан.
     */
    @JsonIgnore
    public boolean hasCustomEmojiBackground() {
        return backgroundCustomEmojiId != null && !backgroundCustomEmojiId.isBlank();
    }
    
    /**
     * Проверяет, установлен ли кастомный эмодзи-фон для профиля.
     * @return true, если profileBackgroundCustomEmojiId задан.
     */
    @JsonIgnore
    public boolean hasProfileCustomEmojiBackground() {
        return profileBackgroundCustomEmojiId != null && !profileBackgroundCustomEmojiId.isBlank();
    }
    
    /**
     * Проверяет, установлен ли эмодзи-статус.
     * @return true, если emojiStatusCustomEmojiId задан.
     */
    @JsonIgnore
    public boolean hasEmojiStatus() {
        return emojiStatusCustomEmojiId != null && !emojiStatusCustomEmojiId.isBlank();
    }
    
    /**
     * Проверяет, истек ли срок действия эмодзи-статуса.
     * @return true, если статус был временным и время вышло.
     */
    @JsonIgnore
    public boolean isEmojiStatusExpired() {
        if (emojiStatusExpirationDate == null) return false;
        return System.currentTimeMillis() / 1000 > emojiStatusExpirationDate;
    }
    
    /**
     * Возвращает человекочитаемое описание типа чата.
     *
     * @return "Private Chat", "Group", "Supergroup" или "Channel".
     */
    @JsonIgnore
    public String getTypeDescription() {
        return switch (type) {
            case "private" -> "Private Chat";
            case "group" -> "Group";
            case "supergroup" -> "Supergroup";
            case "channel" -> "Channel";
            default -> "Unknown";
        };
    }
    
    /**
     * Проверяет, разрешено ли отправлять текстовые сообщения.
     *
     * @return true, если разрешено (или если ограничения не установлены).
     */
    @JsonIgnore
    public boolean canSendMessages() {
        if (permissions == null) return true;
        return permissions.canSendMessages();
    }
    
    /**
     * Проверяет, разрешено ли отправлять медиафайлы (фото, видео, документы и т.д.).
     *
     * @return true, если разрешено.
     */
    @JsonIgnore
    public boolean canSendMediaMessages() {
        if (permissions == null) return true;
        return permissions.canSendMediaMessages();
    }
    
    /**
     * Проверяет, разрешено ли отправлять опросы.
     *
     * @return true, если разрешено.
     */
    @JsonIgnore
    public boolean canSendPolls() {
        if (permissions == null) return true;
        return permissions.canSendPolls();
    }
    
    /**
     * Проверяет, разрешено ли отправлять прочие сообщения (стикеры, игры, анимации).
     *
     * @return true, если разрешено.
     */
    @JsonIgnore
    public boolean canSendOtherMessages() {
        if (permissions == null) return true;
        return permissions.canSendOtherMessages();
    }
    
    /**
     * Проверяет, разрешено ли добавлять превью ссылок (Web Page Preview).
     *
     * @return true, если разрешено.
     */
    @JsonIgnore
    public boolean canAddWebPagePreviews() {
        if (permissions == null) return true;
        return permissions.canAddWebPagePreviews();
    }
    
    /**
     * Проверяет, разрешено ли изменять информацию о чате (название, фото, описание).
     *
     * @return true, если разрешено.
     */
    @JsonIgnore
    public boolean canChangeInfo() {
        if (permissions == null) return true;
        return permissions.canChangeInfo();
    }
    
    /**
     * Проверяет, разрешено ли приглашать новых пользователей.
     *
     * @return true, если разрешено.
     */
    @JsonIgnore
    public boolean canInviteUsers() {
        if (permissions == null) return true;
        return permissions.canInviteUsers();
    }
    
    /**
     * Проверяет, разрешено ли закреплять сообщения.
     *
     * @return true, если разрешено.
     */
    @JsonIgnore
    public boolean canPinMessages() {
        if (permissions == null) return true;
        return permissions.canPinMessages();
    }

    private String escapeHtml(String text) {
        if (text == null) return "";
        return text.replace("&", "&amp;")
                   .replace("<", "&lt;")
                   .replace(">", "&gt;")
                   .replace("\"", "&quot;");
    }
}