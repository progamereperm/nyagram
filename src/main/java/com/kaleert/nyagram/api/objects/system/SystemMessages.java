package com.kaleert.nyagram.api.objects.system;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.kaleert.nyagram.api.meta.BotApiObject;
import com.kaleert.nyagram.api.objects.User;
import com.kaleert.nyagram.api.objects.webapp.WebAppInfo;
import java.util.List;

/**
 * Контейнерный класс, объединяющий различные типы служебных сообщений.
 * <p>
 * Эти объекты появляются в поле {@code message} при наступлении определенных событий
 * (изменение настроек чата, видеочаты, форумные действия и т.д.).
 * </p>
 *
 * @since 1.0.0
 */
public class SystemMessages {
    
    /**
     * Служебное сообщение: Видеочат запланирован.
     *
     * @param startDate Точка во времени (Unix timestamp), когда видеочат должен начаться.
     */
    @JsonIgnoreProperties(ignoreUnknown = true)
    public record VideoChatScheduled(
        @JsonProperty("start_date") Integer startDate
    ) implements BotApiObject {}
    
    /**
     * Служебное сообщение: Видеочат начался.
     */
    @JsonIgnoreProperties(ignoreUnknown = true)
    public record VideoChatStarted() implements BotApiObject {}
    
    /**
     * Служебное сообщение: Видеочат завершен.
     *
     * @param duration Длительность видеочата в секундах.
     */
    @JsonIgnoreProperties(ignoreUnknown = true)
    public record VideoChatEnded(
        @JsonProperty("duration") Integer duration
    ) implements BotApiObject {}
    
    /**
     * Служебное сообщение: Новые участники приглашены в видеочат.
     *
     * @param users Список приглашенных пользователей.
     */
    @JsonIgnoreProperties(ignoreUnknown = true)
    public record VideoChatParticipantsInvited(
        @JsonProperty("users") List<User> users
    ) implements BotApiObject {}
    
    // --- Forum ---
    
    /**
     * Служебное сообщение: Топик форума закрыт.
     */
    @JsonIgnoreProperties(ignoreUnknown = true)
    public record ForumTopicClosed() implements BotApiObject {}
    
    /**
     * Служебное сообщение: Топик форума открыт (reopened).
     */
    @JsonIgnoreProperties(ignoreUnknown = true)
    public record ForumTopicReopened() implements BotApiObject {}
    
    /**
     * Служебное сообщение: Топик форума отредактирован (изменено имя или иконка).
     *
     * @param name Новое имя топика (если изменено).
     * @param iconCustomEmojiId Новый ID кастомного эмодзи иконки (если изменен).
     */
    @JsonIgnoreProperties(ignoreUnknown = true)
    public record ForumTopicEdited(
        @JsonProperty("name") String name,
        @JsonProperty("icon_custom_emoji_id") String iconCustomEmojiId
    ) implements BotApiObject {}
    
    /**
     * Служебное сообщение: Топик "General" (ID=1) скрыт.
     */
    @JsonIgnoreProperties(ignoreUnknown = true)
    public record GeneralForumTopicHidden() implements BotApiObject {}
    
    /**
     * Служебное сообщение: Топик "General" (ID=1) снова показан.
     */
    @JsonIgnoreProperties(ignoreUnknown = true)
    public record GeneralForumTopicUnhidden() implements BotApiObject {}
    
    /**
     * Служебное сообщение: Изменен таймер автоудаления сообщений.
     *
     * @param messageAutoDeleteTime Новое время автоудаления в секундах.
     */
    @JsonIgnoreProperties(ignoreUnknown = true)
    public record MessageAutoDeleteTimerChanged(
        @JsonProperty("message_auto_delete_time") Integer messageAutoDeleteTime
    ) implements BotApiObject {}
    
    /**
     * Служебное сообщение: Пользователь разрешил боту отправлять сообщения.
     * <p>
     * Срабатывает, когда пользователь запускает Web App, добавляет бота в меню вложений или
     * нажимает кнопку запроса прав.
     * </p>
     *
     * @param fromRequest True, если доступ предоставлен через запрос (request_write_access).
     * @param webAppName Имя Web App, которое запустил пользователь.
     * @param fromAttachmentMenu True, если пользователь добавил бота в меню вложений.
     */
    @JsonIgnoreProperties(ignoreUnknown = true)
    public record WriteAccessAllowed(
        @JsonProperty("from_request") Boolean fromRequest,
        @JsonProperty("web_app_name") String webAppName,
        @JsonProperty("from_attachment_menu") Boolean fromAttachmentMenu
    ) implements BotApiObject {}
    
    /**
     * Служебное сообщение: Пользователь бустанул (boosted) чат.
     *
     * @param boostCount Количество добавленных бустов.
     */
    @JsonIgnoreProperties(ignoreUnknown = true)
    public record ChatBoostAdded(
        @JsonProperty("boost_count") Integer boostCount
    ) implements BotApiObject {}
    
    /**
     * Служебное сообщение: Запланирован розыгрыш (Giveaway).
     *
     * @param prizeStarCount Количество звезд, которые будут разыграны (если это розыгрыш звезд).
     */
    @JsonIgnoreProperties(ignoreUnknown = true)
    public record GiveawayCreated(
        @JsonProperty("prize_star_count") Integer prizeStarCount
    ) implements BotApiObject {}
    
    /**
     * Служебное сообщение: Розыгрыш завершен (победители выбраны).
     *
     * @param winnerCount Количество победителей.
     * @param unclaimedPrizeCount Количество призов, которые не удалось вручить.
     * @param giveawayMessage Сообщение с анонсом розыгрыша (может быть недоступно, если удалено).
     */
    @JsonIgnoreProperties(ignoreUnknown = true)
    public record GiveawayCompleted(
        @JsonProperty("winner_count") Integer winnerCount,
        @JsonProperty("unclaimed_prize_count") Integer unclaimedPrizeCount,
        @JsonProperty("giveaway_message") com.kaleert.nyagram.api.objects.message.Message giveawayMessage
    ) implements BotApiObject {}
}