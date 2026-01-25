package com.kaleert.nyagram.api.objects.message;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.kaleert.nyagram.api.meta.BotApiObject;
import com.kaleert.nyagram.api.objects.*;
import com.kaleert.nyagram.api.objects.chat.Chat;
import com.kaleert.nyagram.api.objects.forum.ForumTopic;
import com.kaleert.nyagram.api.objects.games.Game;
import com.kaleert.nyagram.api.objects.payments.Invoice;
import com.kaleert.nyagram.api.objects.payments.SuccessfulPayment;
import com.kaleert.nyagram.api.objects.polls.Poll;
import com.kaleert.nyagram.api.objects.stickers.Sticker;
import com.kaleert.nyagram.api.objects.system.*;
import com.kaleert.nyagram.api.objects.system.SystemMessages;
import com.kaleert.nyagram.api.objects.system.SystemMessages.VideoChatScheduled;
import com.kaleert.nyagram.api.objects.system.SystemMessages.VideoChatStarted;
import com.kaleert.nyagram.api.objects.system.SystemMessages.VideoChatEnded;
import com.kaleert.nyagram.api.objects.system.SystemMessages.VideoChatParticipantsInvited;
import com.kaleert.nyagram.api.objects.system.SystemMessages.ForumTopicClosed;
import com.kaleert.nyagram.api.objects.system.SystemMessages.ForumTopicReopened;
import com.kaleert.nyagram.api.objects.system.SystemMessages.ForumTopicEdited;
import com.kaleert.nyagram.api.objects.system.SystemMessages.GeneralForumTopicHidden;
import com.kaleert.nyagram.api.objects.system.SystemMessages.GeneralForumTopicUnhidden;
import com.kaleert.nyagram.api.objects.system.SystemMessages.MessageAutoDeleteTimerChanged;
import com.kaleert.nyagram.api.objects.system.SystemMessages.WriteAccessAllowed;
import com.kaleert.nyagram.api.objects.system.SystemMessages.ChatBoostAdded;
import com.kaleert.nyagram.api.objects.system.SystemMessages.GiveawayCreated;
import com.kaleert.nyagram.api.objects.system.SystemMessages.GiveawayCompleted;
import com.kaleert.nyagram.api.objects.giveaway.Giveaway;
import com.kaleert.nyagram.api.objects.giveaway.GiveawayWinners;
import com.kaleert.nyagram.api.objects.passport.PassportData;
import com.kaleert.nyagram.api.objects.message.origin.MessageOrigin;
import com.kaleert.nyagram.api.objects.webapp.WebAppData;
import com.kaleert.nyagram.api.objects.replykeyboard.InlineKeyboardMarkup;

import lombok.Builder;
import java.util.Collections;
import java.util.List;

/**
 * Представляет сообщение Telegram.
 * <p>
 * Огромный объект, который может содержать текст, медиа (фото, видео),
 * служебную информацию (пользователь вошел/вышел) или платежные данные.
 * </p>
 * 
 * @param messageId Уникальный идентификатор сообщения внутри чата.
 * @param messageThreadId Уникальный идентификатор топика (для форумов) или ID сообщения, на которое отвечает это сообщение (для обычных групп).
 * @param from Отправитель сообщения. Может быть пустым в каналах.
 * @param senderChat Чат-отправитель (для каналов или анонимных администраторов).
 * @param date Дата отправки сообщения (Unix timestamp).
 * @param chat Чат, которому принадлежит сообщение.
 * @param forwardOrigin Информация об источнике пересылки (если сообщение переслано).
 * @param forwardDate Дата пересылки сообщения.
 * @param isTopicMessage True, если сообщение является сервисным сообщением форума.
 * @param isAutomaticForward True, если сообщение было автоматически переслано из канала в привязанную группу.
 * @param replyToMessage Сообщение, на которое ответили (Reply).
 * @param externalReplyInfo Информация о сообщении из другого чата, на которое ответили.
 * @param textQuote Информация о цитируемой части сообщения.
 * @param viaBot Бот, через которого отправлено сообщение.
 * @param editDate Дата последнего редактирования сообщения.
 * @param hasProtectedContent True, если сообщение защищено от пересылки и сохранения.
 * @param mediaGroupId Уникальный идентификатор группы медиа (альбома).
 * @param authorSignature Подпись автора сообщения (в каналах).
 * @param text Текстовое содержимое сообщения.
 * @param entities Список специальных сущностей в тексте (ссылки, жирный текст, команды).
 * @param linkPreviewOptions Настройки превью ссылок.
 * @param animation Анимация (GIF).
 * @param audio Аудиофайл.
 * @param document Документ (файл).
 * @param photo Фотографии (массив разных размеров).
 * @param sticker Стикер.
 * @param story История.
 * @param video Видеофайл.
 * @param videoNote Видеосообщение ("кружочек").
 * @param voice Голосовое сообщение.
 * @param caption Подпись к медиафайлу.
 * @param captionEntities Сущности в подписи.
 * @param hasMediaSpoiler True, если медиа скрыто спойлером.
 * @param contact Контакт.
 * @param dice Игральная кость (или другая анимация).
 * @param game Игра.
 * @param poll Опрос.
 * @param venue Место на карте (Venue).
 * @param location Геолокация.
 * @param newChatMembers Служебное сообщение: новые участники чата.
 * @param leftChatMember Служебное сообщение: участник покинул чат.
 * @param newChatTitle Служебное сообщение: новое название чата.
 * @param newChatPhoto Служебное сообщение: новое фото чата.
 * @param deleteChatPhoto Служебное сообщение: фото чата удалено.
 * @param groupChatCreated Служебное сообщение: группа создана.
 * @param supergroupChatCreated Служебное сообщение: супергруппа создана.
 * @param channelChatCreated Служебное сообщение: канал создан.
 * @param messageAutoDeleteTimerChanged Служебное сообщение: изменен таймер автоудаления.
 * @param migrateToChatId ID супергруппы, в которую мигрировала группа.
 * @param migrateFromChatId ID группы, из которой мигрировала супергруппа.
 * @param pinnedMessage Служебное сообщение: закрепленное сообщение.
 * @param invoice Счет на оплату.
 * @param successfulPayment Служебное сообщение: успешный платеж.
 * @param usersShared Служебное сообщение: пользователь поделился пользователями.
 * @param chatShared Служебное сообщение: пользователь поделился чатом.
 * @param connectedWebsite Домен сайта, если пользователь вошел через Telegram Login.
 * @param writeAccessAllowed Служебное сообщение: пользователь разрешил боту писать ему.
 * @param passportData Данные Telegram Passport.
 * @param proximityAlertTriggered Служебное сообщение: сработало уведомление о близости.
 * @param boostAdded Служебное сообщение: пользователь бустанул чат.
 * @param forumTopicCreated Служебное сообщение: создан топик форума.
 * @param forumTopicEdited Служебное сообщение: топик форума отредактирован.
 * @param forumTopicClosed Служебное сообщение: топик форума закрыт.
 * @param forumTopicReopened Служебное сообщение: топик форума открыт заново.
 * @param generalForumTopicHidden Служебное сообщение: топик "General" скрыт.
 * @param generalForumTopicUnhidden Служебное сообщение: топик "General" показан.
 * @param videoChatScheduled Служебное сообщение: видеочат запланирован.
 * @param videoChatStarted Служебное сообщение: видеочат начался.
 * @param videoChatEnded Служебное сообщение: видеочат завершен.
 * @param videoChatParticipantsInvited Служебное сообщение: приглашены участники видеочата.
 * @param giveawayCreated Служебное сообщение: запланирован розыгрыш.
 * @param giveaway Сообщение с розыгрышем.
 * @param giveawayWinners Сообщение с победителями розыгрыша.
 * @param giveawayCompleted Служебное сообщение: розыгрыш завершен (без публичных победителей).
 * @param webAppData Данные, полученные из Web App.
 * @param replyMarkup Inline-клавиатура под сообщением.
 * 
 * @since 1.0.0
 */
@Builder
@JsonIgnoreProperties(ignoreUnknown = true)
public record Message(
    /** Уникальный идентификатор сообщения внутри чата. */
    @JsonProperty("message_id") Long messageId,
    
    /** Уникальный идентификатор топика (для форумов). */
    @JsonProperty("message_thread_id") Integer messageThreadId,
    
    /** Отправитель сообщения (может быть пустым в каналах). */
    @JsonProperty("from") User from,
    
    /** Чат-отправитель (для каналов или анонимных админов). */
    @JsonProperty("sender_chat") Chat senderChat,
    
    /** Дата отправки (Unix timestamp). */
    @JsonProperty("date") Integer date,
    
    /** Чат, которому принадлежит сообщение. */
    @JsonProperty("chat") Chat chat,
    
    // --- Forwarding & Replies ---
    @JsonProperty("forward_origin") MessageOrigin forwardOrigin,
    @JsonProperty("forward_date") Integer forwardDate,
    @JsonProperty("is_topic_message") Boolean isTopicMessage,
    @JsonProperty("is_automatic_forward") Boolean isAutomaticForward,
    
    /** Сообщение, на которое ответили (Reply). */
    @JsonProperty("reply_to_message") Message replyToMessage,
    @JsonProperty("external_reply") ExternalReplyInfo externalReplyInfo,
    @JsonProperty("quote") TextQuote textQuote,
    
    /** Бот, через которого отправлено сообщение. */
    @JsonProperty("via_bot") User viaBot,
    
    /** Дата последнего редактирования. */
    @JsonProperty("edit_date") Integer editDate,
    @JsonProperty("has_protected_content") Boolean hasProtectedContent,
    @JsonProperty("media_group_id") String mediaGroupId,
    @JsonProperty("author_signature") String authorSignature,
    
    /** Текстовое содержимое сообщения. */
    @JsonProperty("text") String text,
    
    /** Специальные сущности в тексте (ссылки, жирный текст, команды). */
    @JsonProperty("entities") List<MessageEntity> entities,
    @JsonProperty("link_preview_options") LinkPreviewOptions linkPreviewOptions,
    
    // --- Media ---
    @JsonProperty("animation") Animation animation,
    @JsonProperty("audio") Audio audio,
    @JsonProperty("document") Document document,
    
    /** Фото (массив разных размеров). */
    @JsonProperty("photo") List<PhotoSize> photo,
    
    /** Стикер. */
    @JsonProperty("sticker") Sticker sticker,
    @JsonProperty("story") Story story,
    
    /** Видео. */
    @JsonProperty("video") Video video,
    @JsonProperty("video_note") VideoNote videoNote,
    
    /** Голосовое сообщение. */
    @JsonProperty("voice") Voice voice,
    
    /** Подпись к медиафайлу. */
    @JsonProperty("caption") String caption,
    @JsonProperty("caption_entities") List<MessageEntity> captionEntities,
    @JsonProperty("has_media_spoiler") Boolean hasMediaSpoiler,
    
    /** Контакт. */
    @JsonProperty("contact") Contact contact,
    @JsonProperty("dice") Dice dice,
    @JsonProperty("game") Game game,
    @JsonProperty("poll") Poll poll,
    @JsonProperty("venue") Venue venue,
    
    /** Локация. */
    @JsonProperty("location") Location location,
    
    /** Новые участники чата (служебное). */
    @JsonProperty("new_chat_members") List<User> newChatMembers,
    
    /** Участник покинул чат (служебное). */
    @JsonProperty("left_chat_member") User leftChatMember,
    @JsonProperty("new_chat_title") String newChatTitle,
    @JsonProperty("new_chat_photo") List<PhotoSize> newChatPhoto,
    @JsonProperty("delete_chat_photo") Boolean deleteChatPhoto,
    @JsonProperty("group_chat_created") Boolean groupChatCreated,
    @JsonProperty("supergroup_chat_created") Boolean supergroupChatCreated,
    @JsonProperty("channel_chat_created") Boolean channelChatCreated,
    @JsonProperty("message_auto_delete_timer_changed") MessageAutoDeleteTimerChanged messageAutoDeleteTimerChanged,
    
    // --- Migration ---
    @JsonProperty("migrate_to_chat_id") Long migrateToChatId,
    @JsonProperty("migrate_from_chat_id") Long migrateFromChatId,
    
    @JsonProperty("pinned_message") MaybeInaccessibleMessage pinnedMessage,
    
    // --- Payments ---
    @JsonProperty("invoice") Invoice invoice,
    @JsonProperty("successful_payment") SuccessfulPayment successfulPayment,
    
    // --- Sharing & Login ---
    @JsonProperty("users_shared") UsersShared usersShared,
    @JsonProperty("chat_shared") ChatShared chatShared,
    @JsonProperty("connected_website") String connectedWebsite,
    @JsonProperty("write_access_allowed") WriteAccessAllowed writeAccessAllowed,
    @JsonProperty("passport_data") PassportData passportData,
    
    // --- System ---
    @JsonProperty("proximity_alert_triggered") ProximityAlertTriggered proximityAlertTriggered,
    @JsonProperty("boost_added") ChatBoostAdded boostAdded,
    
    // --- Forum ---
    @JsonProperty("forum_topic_created") ForumTopic forumTopicCreated,
    @JsonProperty("forum_topic_edited") ForumTopicEdited forumTopicEdited,
    @JsonProperty("forum_topic_closed") ForumTopicClosed forumTopicClosed,
    @JsonProperty("forum_topic_reopened") ForumTopicReopened forumTopicReopened,
    @JsonProperty("general_forum_topic_hidden") GeneralForumTopicHidden generalForumTopicHidden,
    @JsonProperty("general_forum_topic_unhidden") GeneralForumTopicUnhidden generalForumTopicUnhidden,
    
    // --- Video Chat ---
    @JsonProperty("video_chat_scheduled") VideoChatScheduled videoChatScheduled,
    @JsonProperty("video_chat_started") VideoChatStarted videoChatStarted,
    @JsonProperty("video_chat_ended") VideoChatEnded videoChatEnded,
    @JsonProperty("video_chat_participants_invited") VideoChatParticipantsInvited videoChatParticipantsInvited,
    
    // --- Giveaways ---
    @JsonProperty("giveaway_created") GiveawayCreated giveawayCreated,
    @JsonProperty("giveaway") Giveaway giveaway,
    @JsonProperty("giveaway_winners") GiveawayWinners giveawayWinners,
    @JsonProperty("giveaway_completed") GiveawayCompleted giveawayCompleted,
    
    // --- Web App ---
    @JsonProperty("web_app_data") WebAppData webAppData,
    
    /** Инлайн-клавиатура под сообщением. */
    @JsonProperty("reply_markup") InlineKeyboardMarkup replyMarkup
) implements MaybeInaccessibleMessage {

    // --- Getters ---
    
    /**
     * Возвращает уникальный идентификатор сообщения.
     * @return ID (Long).
     */
    public Long getMessageId() { return messageId; }
    
    /**
     * Возвращает ID топика (Message Thread ID), к которому относится сообщение.
     *
     * @return ID топика или null.
     */
    public Integer getMessageThreadId() { return messageThreadId; }
    
    /**
     * Возвращает отправителя сообщения.
     * <p>
     * Может быть null в каналах.
     * </p>
     *
     * @return объект User или null.
     */
    public User getFrom() { return from; }
    
    /**
     * Возвращает чат-отправитель (для каналов или анонимных админов).
     *
     * @return объект Chat или null.
     */
    public Chat getSenderChat() { return senderChat; }
    
    /**
     * Возвращает дату отправки сообщения.
     *
     * @return Unix timestamp.
     */
    public Integer getDate() { return date; }
    
    /**
     * Возвращает объект чата, в который было отправлено сообщение.
     *
     * @return объект Chat.
     */
    public Chat getChat() { return chat; }
    
    // Origin
    
    /**
     * Возвращает информацию об источнике пересылки.
     *
     * @return объект MessageOrigin или null.
     */
    public MessageOrigin getForwardOrigin() { return forwardOrigin; }
    
    /**
     * Возвращает дату пересылки сообщения (для пересланных сообщений).
     *
     * @return Unix timestamp или null.
     */
    public Integer getForwardDate() { return forwardDate; }
    
    /**
     * Возвращает true, если сообщение является системным сообщением топика форума.
     *
     * @return Boolean.
     */
    public Boolean getIsTopicMessage() { return isTopicMessage; }
    
    /**
     * Возвращает true, если сообщение отправлено автоматически (например, при пересылке из канала в группу).
     *
     * @return Boolean.
     */
    public Boolean getIsAutomaticForward() { return isAutomaticForward; }
    
    /**
     * Возвращает сообщение, на которое это сообщение является ответом (Reply).
     *
     * @return объект Message или null.
     */
    public Message getReplyToMessage() { return replyToMessage; }
    
    /**
     * Возвращает информацию о сообщении из другого чата, на которое ответили.
     *
     * @return объект ExternalReplyInfo или null.
     */
    public ExternalReplyInfo getExternalReplyInfo() { return externalReplyInfo; }
    
    /**
     * Возвращает цитату (часть сообщения), на которую ответили.
     *
     * @return объект TextQuote или null.
     */
    public TextQuote getTextQuote() { return textQuote; }
    
    /**
     * Возвращает бота, через которого было отправлено сообщение (inline-бот).
     *
     * @return объект User или null.
     */
    public User getViaBot() { return viaBot; }
    
    /**
     * Возвращает дату последнего редактирования сообщения.
     *
     * @return Unix timestamp или null.
     */
    public Integer getEditDate() { return editDate; }
    
    /**
     * Возвращает true, если сообщение защищено от пересылки и сохранения.
     *
     * @return Boolean.
     */
    public Boolean getHasProtectedContent() { return hasProtectedContent; }
    
    /**
     * Возвращает ID группы медиа (для альбомов).
     * Сообщения с одинаковым media_group_id принадлежат одному альбому.
     *
     * @return строковый ID или null.
     */
    public String getMediaGroupId() { return mediaGroupId; }
    
    /**
     * Возвращает подпись автора сообщения (в каналах).
     * @return текст подписи или null.
     */
    public String getAuthorSignature() { return authorSignature; }
    
    /**
     * Возвращает текст сообщения.
     * Для медиафайлов используйте {@link #getCaption()}.
     *
     * @return текст или null.
     */
    public String getText() { return text; }
    
    /**
     * Возвращает список сущностей в тексте сообщения (для текстовых сообщений).
     *
     * @return список сущностей или null.
     */
    public List<MessageEntity> getEntities() { return entities; }
    
    /**
     * Возвращает настройки превью ссылок.
     *
     * @return объект LinkPreviewOptions или null.
     */
    public LinkPreviewOptions getLinkPreviewOptions() { return linkPreviewOptions; }
    
    // Media
    
    /**
     * Возвращает анимацию (GIF), содержащуюся в сообщении.
     * @return объект Animation или null.
     */
    public Animation getAnimation() { return animation; }
    
    /**
     * Возвращает аудиофайл, содержащийся в сообщении.
     * @return объект Audio или null.
     */
    public Audio getAudio() { return audio; }
    
    /**
     * Возвращает документ (файл).
     *
     * @return объект Document или null.
     */
    public Document getDocument() { return document; }
    
    /**
     * Возвращает список фото (разных размеров) для сообщения с фото.
     *
     * @return список PhotoSize или null.
     */
    public List<PhotoSize> getPhoto() { return photo; }
    
    /**
     * Возвращает стикер.
     *
     * @return объект Sticker или null.
     */
    public Sticker getSticker() { return sticker; }
    
    /**
     * Возвращает пересланную историю.
     *
     * @return объект Story или null.
     */
    public Story getStory() { return story; }
    
    /**
     * Возвращает видеофайл.
     *
     * @return объект Video или null.
     */
    public Video getVideo() { return video; }
    
    /**
     * Возвращает голосовое сообщение.
     *
     * @return объект Voice или null.
     */
    public Voice getVoice() { return voice; }
    
    /**
     * Возвращает видеосообщение ("кружочек").
     *
     * @return объект VideoNote или null.
     */
    public VideoNote getVideoNote() { return videoNote; }
    
    /**
     * Возвращает подпись к медиафайлу (фото, видео, документу).
     * Для текстовых сообщений используйте {@link #getText()}.
     *
     * @return текст подписи или null.
     */
    public String getCaption() { return caption; }
    
    /**
     * Возвращает список специальных сущностей (ссылки, жирный текст, меншены) в подписи.
     *
     * @return список сущностей или null.
     */
    public List<MessageEntity> getCaptionEntities() { return captionEntities; }
    
    /**
     * Возвращает true, если медиа скрыто под спойлером.
     *
     * @return Boolean.
     */
    public Boolean getHasMediaSpoiler() { return hasMediaSpoiler; }
    
    // Interactive
    
    /**
     * Возвращает объект контакта (телефонный номер).
     *
     * @return объект Contact или null.
     */
    public Contact getContact() { return contact; }
    
    /**
     * Возвращает объект игральной кости (или другой анимированной эмодзи).
     *
     * @return объект Dice или null.
     */
    public Dice getDice() { return dice; }
    
    /**
     * Возвращает игру.
     *
     * @return объект Game или null.
     */
    public Game getGame() { return game; }
    
    /**
     * Возвращает опрос.
     *
     * @return объект Poll или null.
     */
    public Poll getPoll() { return poll; }
    
    /**
     * Возвращает место (Venue) на карте.
     *
     * @return объект Venue или null.
     */
    public Venue getVenue() { return venue; }
    
    /**
     * Возвращает геолокацию.
     *
     * @return объект Location или null.
     */
    public Location getLocation() { return location; }
    
    // Service
    
    /**
     * Возвращает список новых участников чата.
     * Служебное сообщение.
     *
     * @return список пользователей или null.
     */
    public List<User> getNewChatMembers() { return newChatMembers; }
    
    /**
     * Возвращает пользователя, покинувшего чат.
     * Служебное сообщение.
     *
     * @return объект User или null.
     */
    public User getLeftChatMember() { return leftChatMember; }
    
    /**
     * Возвращает новое название чата.
     * Служебное сообщение.
     *
     * @return строка заголовка или null.
     */
    public String getNewChatTitle() { return newChatTitle; }
    
    /**
     * Возвращает новые фото чата.
     * Служебное сообщение.
     *
     * @return список PhotoSize или null.
     */
    public List<PhotoSize> getNewChatPhoto() { return newChatPhoto; }
    
    /**
     * Возвращает дату удаления фото чата.
     * Служебное сообщение.
     *
     * @return true, если фото удалено.
     */
    public Boolean getDeleteChatPhoto() { return deleteChatPhoto; }
    
    /**
     * Возвращает true, если создана группа (служебное сообщение).
     *
     * @return Boolean.
     */
    public Boolean getGroupChatCreated() { return groupChatCreated; }
    
    /**
     * Возвращает true, если создана супергруппа (служебное сообщение).
     *
     * @return Boolean.
     */
    public Boolean getSupergroupChatCreated() { return supergroupChatCreated; }
    
    /**
     * Возвращает true, если это служебное сообщение о создании канала.
     *
     * @return Boolean.
     */
    public Boolean getChannelChatCreated() { return channelChatCreated; }
    
    /**
     * Возвращает информацию об изменении таймера автоудаления сообщений.
     * Служебное сообщение.
     *
     * @return объект MessageAutoDeleteTimerChanged или null.
     */
    public MessageAutoDeleteTimerChanged getMessageAutoDeleteTimerChanged() { return messageAutoDeleteTimerChanged; }
    
    /**
     * Возвращает ID нового чата, если группа была преобразована в супергруппу.
     * <p>
     * Это сообщение находится в <b>старой</b> группе и является последним сообщением в ней.
     * </p>
     *
     * @return ID новой супергруппы или null.
     */
    public Long getMigrateToChatId() { return migrateToChatId; }
    
    /**
     * Возвращает ID предыдущего чата, если группа была преобразована в супергруппу.
     * <p>
     * Это сообщение находится в <b>новой</b> супергруппе и указывает, откуда она была мигрирована.
     * </p>
     *
     * @return ID старой группы или null.
     */
    public Long getMigrateFromChatId() { return migrateFromChatId; }
    
    /**
     * Возвращает закрепленное сообщение.
     * Служебное сообщение (сервис pin).
     *
     * @return объект MaybeInaccessibleMessage или null.
     */
    public MaybeInaccessibleMessage getPinnedMessage() { return pinnedMessage; }
    
    /**
     * Возвращает счет на оплату.
     *
     * @return объект Invoice или null.
     */
    public Invoice getInvoice() { return invoice; }
    
    /**
     * Возвращает информацию об успешном платеже.
     * Служебное сообщение.
     *
     * @return объект SuccessfulPayment или null.
     */
    public SuccessfulPayment getSuccessfulPayment() { return successfulPayment; }
    
    /**
     * Возвращает информацию о пользователях, которыми поделились.
     * Ответ на нажатие кнопки {@code request_user}.
     *
     * @return объект UsersShared или null.
     */
    public UsersShared getUsersShared() { return usersShared; }
    
    /**
     * Возвращает информацию о чате, которым поделился пользователь.
     * Ответ на нажатие кнопки {@code request_chat}.
     *
     * @return объект ChatShared или null.
     */
    public ChatShared getChatShared() { return chatShared; }
    
    /**
     * Возвращает домен веб-сайта, если пользователь вошел через Telegram Login Widget.
     *
     * @return домен сайта.
     */
    public String getConnectedWebsite() { return connectedWebsite; }
    
    /**
     * Возвращает информацию о том, что пользователь разрешил боту отправлять сообщения.
     * <p>
     * Служебное сообщение. Появляется, когда пользователь запускает Web App,
     * добавляет бота в меню вложений или нажимает кнопку запроса прав.
     * </p>
     *
     * @return объект WriteAccessAllowed или null.
     */
    public WriteAccessAllowed getWriteAccessAllowed() { return writeAccessAllowed; }
    
    /**
     * Возвращает данные паспорта Telegram.
     *
     * @return объект PassportData или null.
     */
    public PassportData getPassportData() { return passportData; }
    
    /**
     * Возвращает информацию о срабатывании уведомления о близости (Live Location).
     * Служебное сообщение.
     *
     * @return объект ProximityAlertTriggered или null.
     */
    public ProximityAlertTriggered getProximityAlertTriggered() { return proximityAlertTriggered; }
    
    /**
     * Возвращает информацию о том, что пользователь добавил буст (Boost) чату.
     * Служебное сообщение.
     *
     * @return объект ChatBoostAdded или null.
     */
    public ChatBoostAdded getBoostAdded() { return boostAdded; }
    
    // Forum
    
    /**
     * Возвращает информацию о создании нового топика форума.
     * Служебное сообщение.
     *
     * @return объект ForumTopic или null.
     */
    public ForumTopic getForumTopicCreated() { return forumTopicCreated; }
    
    /**
     * Возвращает информацию об изменении названия или иконки топика.
     * Служебное сообщение.
     *
     * @return объект ForumTopicEdited или null.
     */
    public ForumTopicEdited getForumTopicEdited() { return forumTopicEdited; }
    
    /**
     * Возвращает информацию о закрытии топика форума.
     * Служебное сообщение.
     *
     * @return объект ForumTopicClosed или null.
     */
    public ForumTopicClosed getForumTopicClosed() { return forumTopicClosed; }
    
    /**
     * Возвращает информацию об открытии ранее закрытого топика.
     * Служебное сообщение.
     *
     * @return объект ForumTopicReopened или null.
     */
    public ForumTopicReopened getForumTopicReopened() { return forumTopicReopened; }
    
    /**
     * Возвращает информацию о скрытии топика "General".
     * Служебное сообщение.
     *
     * @return объект GeneralForumTopicHidden или null.
     */
    public GeneralForumTopicHidden getGeneralForumTopicHidden() { return generalForumTopicHidden; }
    
    /**
     * Возвращает информацию о возвращении видимости топику "General".
     * Служебное сообщение.
     *
     * @return объект GeneralForumTopicUnhidden или null.
     */
    public GeneralForumTopicUnhidden getGeneralForumTopicUnhidden() { return generalForumTopicUnhidden; }
    
    // Video Chat & Giveaways
    
    /**
     * Возвращает информацию о запланированном розыгрыше.
     * Служебное сообщение.
     *
     * @return объект GiveawayCreated или null.
     */
    public GiveawayCreated getGiveawayCreated() { return giveawayCreated; }
    
    /**
     * Возвращает информацию о розыгрыше (в сообщении с анонсом).
     *
     * @return объект Giveaway или null.
     */
    public Giveaway getGiveaway() { return giveaway; }
    
    /**
     * Возвращает информацию о победителях розыгрыша (в сообщении с результатами).
     *
     * @return объект GiveawayWinners или null.
     */
    public GiveawayWinners getGiveawayWinners() { return giveawayWinners; }
    
    /**
     * Возвращает информацию о завершении розыгрыша (выборе победителей).
     * Служебное сообщение.
     *
     * @return объект GiveawayCompleted или null.
     */
    public GiveawayCompleted getGiveawayCompleted() { return giveawayCompleted; }
    
    /**
     * Возвращает информацию о запланированном видеочате.
     * Служебное сообщение.
     *
     * @return объект VideoChatScheduled или null.
     */
    public VideoChatScheduled getVideoChatScheduled() { return videoChatScheduled; }
    
    /**
     * Возвращает информацию о начале видеочата.
     * Служебное сообщение.
     *
     * @return объект VideoChatStarted или null.
     */
    public VideoChatStarted getVideoChatStarted() { return videoChatStarted; }
    
    /**
     * Возвращает информацию о завершении видеочата.
     * Служебное сообщение.
     *
     * @return объект VideoChatEnded или null.
     */
    public VideoChatEnded getVideoChatEnded() { return videoChatEnded; }
    
    /**
     * Возвращает информацию о приглашении участников в видеочат.
     * Служебное сообщение.
     *
     * @return объект VideoChatParticipantsInvited или null.
     */
    public VideoChatParticipantsInvited getVideoChatParticipantsInvited() { return videoChatParticipantsInvited; }
    
    /**
     * Возвращает данные, полученные из Web App.
     *
     * @return объект WebAppData или null.
     */
    public WebAppData getWebAppData() { return webAppData; }
    
    /**
     * Возвращает Inline-клавиатуру, прикрепленную к сообщению.
     *
     * @return объект InlineKeyboardMarkup или null.
     */
    public InlineKeyboardMarkup getReplyMarkup() { return replyMarkup; }
    
    /**
     * Проверяет, содержит ли сообщение текст.
     * <p>
     * Игнорирует подписи к медиа. Для проверки и текста, и подписей используйте {@link #hasTextOrCaption()}.
     * </p>
     *
     * @return true, если поле text не пустое.
     */
    @JsonIgnore
    public boolean hasText() {
        return text != null && !text.isEmpty();
    }
    
    /**
     * Проверяет, содержит ли сообщение подпись к медиа.
     *
     * @return true, если поле caption не пустое.
     */
    @JsonIgnore
    public boolean hasCaption() {
        return caption != null && !caption.isEmpty();
    }
    
    /**
     * Проверяет наличие любого текстового контента (текст сообщения ИЛИ подпись к медиа).
     *
     * @return true, если есть текст или подпись.
     */
    @JsonIgnore
    public boolean hasTextOrCaption() {
        return hasText() || hasCaption();
    }
    
    /**
     * Универсальный геттер: возвращает текст ИЛИ подпись.
     *
     * @return содержимое сообщения или null.
     */
    @JsonIgnore
    public String getTextOrCaption() {
        if (hasText()) return text;
        if (hasCaption()) return caption;
        return null;
    }
    
    /**
     * Вспомогательный метод: возвращает сущности либо из текста, либо из подписи.
     * Гарантированно возвращает список (возможно пустой), но не null.
     *
     * @return список сущностей.
     */
    @JsonIgnore
    public List<MessageEntity> getEntitiesOrCaptionEntities() {
        if (entities != null && !entities.isEmpty()) return entities;
        if (captionEntities != null && !captionEntities.isEmpty()) return captionEntities;
        return Collections.emptyList();
    }
    
    /**
     * Проверяет, есть ли в тексте сообщения специальные сущности (ссылки, меншены и т.д.).
     *
     * @return true, если список entities не пуст.
     */
    @JsonIgnore
    public boolean hasEntities() {
        return entities != null && !entities.isEmpty();
    }
    
    /**
     * Проверяет, есть ли в подписи специальные сущности.
     *
     * @return true, если список captionEntities не пуст.
     */
    @JsonIgnore
    public boolean hasCaptionEntities() {
        return captionEntities != null && !captionEntities.isEmpty();
    }
    
    /**
     * Проверяет, является ли сообщение командой бота (например, "/start").
     * <p>
     * Проверка выполняется строго: сообщение должно начинаться с сущности типа {@code bot_command}.
     * </p>
     *
     * @return true, если это валидная команда.
     */
    @JsonIgnore
    public boolean isCommand() {
        if (!hasTextOrCaption()) return false;
        List<MessageEntity> currentEntities = getEntitiesOrCaptionEntities();
        
        if (currentEntities.isEmpty()) return false;
        
        MessageEntity firstEntity = currentEntities.get(0);
        return firstEntity.offset() == 0 && MessageEntity.BOT_COMMAND.equals(firstEntity.type());
    }
    
    /**
     * Извлекает команду из текста сообщения (например, "/start").
     * <p>
     * Работает, только если сообщение начинается с сущности {@code bot_command}.
     * </p>
     *
     * @return текст команды (включая слэш) или null.
     */
    @JsonIgnore
    public String getCommand() {
        if (!isCommand()) return null;
        String content = getTextOrCaption();
        MessageEntity commandEntity = getEntitiesOrCaptionEntities().get(0);
        return commandEntity.getText(content);
    }
    
    /**
     * Извлекает аргументы команды (текст после команды).
     * <p>
     * Если сообщение {@code /ban @user 1d}, вернет {@code @user 1d}.
     * </p>
     *
     * @return аргументы строкой или пустая строка.
     */
    @JsonIgnore
    public String getCommandArgs() {
        if (!isCommand()) return "";
        String content = getTextOrCaption();
        MessageEntity commandEntity = getEntitiesOrCaptionEntities().get(0);
        
        int commandEndIndex = commandEntity.offset() + commandEntity.length();
        if (commandEndIndex >= content.length()) return "";
        
        return content.substring(commandEndIndex).trim();
    }
    
    /**
     * Вспомогательный метод для получения ID чата.
     *
     * @return ID чата или null, если объект чата отсутствует.
     */
    @JsonIgnore
    public Long getChatId() {
        return chat != null ? chat.getId() : null;
    }
    
    /**
     * Вспомогательный метод для получения ID отправителя.
     *
     * @return ID пользователя или null.
     */
    @JsonIgnore
    public Long getFromId() {
        return from != null ? from.getId() : null;
    }
    
    /**
     * Вспомогательный метод для получения ID чата-отправителя.
     * <p>
     * Актуально для сообщений в каналах или анонимных сообщений в группах.
     * </p>
     *
     * @return ID чата или null.
     */
    @JsonIgnore
    public Long getSenderChatId() {
        return senderChat != null ? senderChat.getId() : null;
    }
    
    /**
     * Проверяет, отправлено ли сообщение в групповом чате (группа или супергруппа).
     *
     * @return true, если это группа.
     */
    @JsonIgnore
    public boolean isGroupChat() {
        return chat != null && (chat.isGroupChat() || chat.isSuperGroupChat());
    }
    
    /**
     * Проверяет, отправлено ли сообщение в личном чате (ЛС).
     *
     * @return true, если это приватный чат.
     */
    @JsonIgnore
    public boolean isUserChat() {
        return chat != null && chat.isUserChat();
    }
    
    /**
     * Проверяет, является ли сообщение постом в канале.
     *
     * @return true, если это канал.
     */
    @JsonIgnore
    public boolean isChannelChat() {
        return chat != null && chat.isChannelChat();
    }
    
    /**
     * Проверяет, отправлено ли сообщение в супергруппе.
     *
     * @return true, если это супергруппа.
     */
    @JsonIgnore
    public boolean isSuperGroupChat() {
        return chat != null && chat.isSuperGroupChat();
    }
    
    /**
     * Проверяет, отправлено ли сообщение анонимно.
     * <p>
     * Это происходит, когда пользователь пишет в группе от имени своего канала,
     * или когда админ пишет анонимно. В таком случае поле {@code from} отсутствует,
     * но есть {@code sender_chat}.
     * </p>
     *
     * @return true, если сообщение анонимное.
     */
    @JsonIgnore
    public boolean isAnonymous() {
        return from == null && senderChat != null;
    }
    
    /**
     * Проверяет, отправлено ли сообщение через инлайн-бота.
     *
     * @return true, если поле {@code via_bot} присутствует.
     */
    @JsonIgnore
    public boolean hasViaBot() {
        return viaBot != null;
    }
    
    /**
     * Безопасная проверка: является ли сообщение сервисным сообщением форума ("General topic hidden" и т.д.)
     * или первым сообщением в топике.
     *
     * @return true, если isTopicMessage = true.
     */
    @JsonIgnore
    public Boolean isTopicMessage() {
        return Boolean.TRUE.equals(isTopicMessage);
    }
    
    /**
     * Безопасная проверка: является ли сообщение автоматическим пересылаемым постом из канала.
     *
     * @return true, если isAutomaticForward = true.
     */
    @JsonIgnore
    public Boolean isAutomaticForward() {
        return Boolean.TRUE.equals(isAutomaticForward);
    }
    
    /**
     * Безопасная проверка: защищен ли контент сообщения от копирования и пересылки.
     *
     * @return true, если hasProtectedContent = true.
     */
    @JsonIgnore
    public Boolean hasProtectedContent() {
        return Boolean.TRUE.equals(hasProtectedContent);
    }
    
    /**
     * Безопасная проверка: скрыто ли медиа (фото/видео) под спойлером.
     *
     * @return true, если hasMediaSpoiler = true.
     */
    @JsonIgnore
    public Boolean hasMediaSpoiler() {
        return Boolean.TRUE.equals(hasMediaSpoiler);
    }
    
    /**
     * Проверяет, является ли сообщение служебным (Service Message).
     * <p>
     * Служебные сообщения сообщают о событиях: вход/выход участников,
     * закрепление сообщений, изменение названия чата и т.д.
     * </p>
     *
     * @return true, если это служебное уведомление.
     */
    @JsonIgnore
    public boolean isServiceMessage() {
        return newChatMembers != null || leftChatMember != null ||
               newChatTitle != null || newChatPhoto != null ||
               deleteChatPhoto != null || groupChatCreated != null ||
               supergroupChatCreated != null || channelChatCreated != null ||
               migrateToChatId != null || migrateFromChatId != null ||
               pinnedMessage != null || forumTopicCreated != null ||
               forumTopicClosed != null || forumTopicReopened != null ||
               forumTopicEdited != null || generalForumTopicHidden != null ||
               generalForumTopicUnhidden != null || videoChatScheduled != null ||
               videoChatStarted != null || videoChatEnded != null ||
               videoChatParticipantsInvited != null || messageAutoDeleteTimerChanged != null ||
               successfulPayment != null || giveawayCreated != null ||
               giveaway != null || giveawayWinners != null || giveawayCompleted != null ||
               boostAdded != null;
    }
    
    /**
     * Проверяет, является ли сообщение ответом (Reply) на другое сообщение.
     *
     * @return true, если {@code reply_to_message} присутствует.
     */
    @JsonIgnore
    public boolean isReply() {
        return replyToMessage != null;
    }
    
    /**
     * Проверяет, является ли сообщение пересланным (Forward).
     *
     * @return true, если есть дата пересылки или источник.
     */
    @JsonIgnore
    public boolean isForward() {
        return forwardDate != null || forwardOrigin != null;
    }
    
    /**
     * Проверяет наличие фото в сообщении.
     *
     * @return true, если список фото не пуст.
     */
    @JsonIgnore
    public boolean hasPhoto() {
        return photo != null && !photo.isEmpty();
    }
    
    /**
     * Проверяет наличие документа (файла).
     * @return true, если document != null.
     */
    @JsonIgnore
    public boolean hasDocument() {
        return document != null;
    }
    
   /**
     * Проверяет наличие видео.
     * @return true, если video != null.
     */
    @JsonIgnore
    public boolean hasVideo() {
        return video != null;
    }
    
    /**
     * Проверяет наличие аудио (музыки).
     * @return true, если audio != null.
     */
    @JsonIgnore
    public boolean hasAudio() {
        return audio != null;
    }
    
    /**
     * Проверяет наличие стикера.
     * @return true, если sticker != null.
     */
    @JsonIgnore
    public boolean hasSticker() {
        return sticker != null;
    }
    
    /**
     * Проверяет наличие голосового сообщения.
     * @return true, если voice != null.
     */
    @JsonIgnore
    public boolean hasVoice() {
        return voice != null;
    }
    
    /**
     * Проверяет наличие анимации (GIF).
     * @return true, если animation != null.
     */
    @JsonIgnore
    public boolean hasAnimation() {
        return animation != null;
    }
    
    /**
     * Проверяет наличие видеосообщения ("кружочка").
     * @return true, если videoNote != null.
     */
    @JsonIgnore
    public boolean hasVideoNote() {
        return videoNote != null;
    }
    
    /**
     * Проверяет наличие истории (Story).
     * @return true, если story != null.
     */
    @JsonIgnore
    public boolean hasStory() {
        return story != null;
    }
    
    /**
     * Проверяет наличие контакта.
     * @return true, если contact != null.
     */
    @JsonIgnore
    public boolean hasContact() {
        return contact != null;
    }
    
   /**
     * Проверяет наличие игральной кости (Dice).
     * @return true, если dice != null.
     */
    @JsonIgnore
    public boolean hasDice() {
        return dice != null;
    }

   /**
     * Проверяет наличие игры.
     * @return true, если game != null.
     */
    @JsonIgnore
    public boolean hasGame() {
        return game != null;
    }
    
    /**
     * Проверяет наличие опроса.
     * @return true, если poll != null.
     */
    @JsonIgnore
    public boolean hasPoll() {
        return poll != null;
    }
    
    /**
     * Проверяет наличие места (Venue).
     * @return true, если venue != null.
     */
    @JsonIgnore
    public boolean hasVenue() {
        return venue != null;
    }
    
    /**
     * Проверяет наличие геолокации.
     * @return true, если location != null.
     */
    @JsonIgnore
    public boolean hasLocation() {
        return location != null;
    }
    
    /**
     * Проверяет наличие счета на оплату.
     * @return true, если invoice != null.
     */
    @JsonIgnore
    public boolean hasInvoice() {
        return invoice != null;
    }
    
    /**
     * Проверяет наличие информации об успешном платеже.
     * @return true, если successfulPayment != null.
     */
    @JsonIgnore
    public boolean hasSuccessfulPayment() {
        return successfulPayment != null;
    }
    
    /**
     * Проверяет наличие данных паспорта.
     * @return true, если passportData != null.
     */
    @JsonIgnore
    public boolean hasPassportData() {
        return passportData != null;
    }
    
    /**
     * Проверяет наличие данных из Web App.
     * @return true, если webAppData != null.
     */
    @JsonIgnore
    public boolean hasWebAppData() {
        return webAppData != null;
    }
    
    /**
     * Проверяет, прикреплена ли к сообщению Inline-клавиатура.
     * @return true, если replyMarkup != null.
     */
    @JsonIgnore
    public boolean hasReplyMarkup() {
        return replyMarkup != null;
    }
    
    /**
     * Проверяет, было ли сообщение отредактировано.
     * @return true, если {@code edit_date} присутствует.
     */
    @JsonIgnore
    public boolean isEdited() {
        return editDate != null;
    }
    
    /**
     * Проверяет, является ли сообщение частью альбома (Media Group).
     * @return true, если {@code media_group_id} присутствует.
     */
    @JsonIgnore
    public boolean hasMediaGroup() {
        return mediaGroupId != null && !mediaGroupId.isEmpty();
    }
    
    /**
     * Алиас для {@link #hasMediaGroup()}.
     * @return true, если сообщение часть альбома.
     */
    @JsonIgnore
    public boolean isPartOfAlbum() {
        return hasMediaGroup();
    }
    
    /**
     * Проверяет наличие подписи автора (в каналах).
     * @return true, если authorSignature != null.
     */
    @JsonIgnore
    public boolean hasAuthorSignature() {
        return authorSignature != null && !authorSignature.isEmpty();
    }
    
    /**
     * Проверяет, зашел ли пользователь через Telegram Login Widget.
     * @return true, если connectedWebsite != null.
     */
    @JsonIgnore
    public boolean hasConnectedWebsite() {
        return connectedWebsite != null && !connectedWebsite.isEmpty();
    }
    
    /**
     * Проверяет, является ли сообщение уведомлением о миграции В супергруппу.
     * @return true, если migrateToChatId != null.
     */
    @JsonIgnore
    public boolean isMigrateToMessage() {
        return migrateToChatId != null;
    }
    
    /**
     * Проверяет, является ли сообщение уведомлением о миграции ИЗ группы.
     * @return true, если migrateFromChatId != null.
     */
    @JsonIgnore
    public boolean isMigrateFromMessage() {
        return migrateFromChatId != null;
    }
    
    /**
     * Проверяет, является ли сообщение уведомлением о миграции группы.
     * <p>
     * Возвращает true, если группа была преобразована в супергруппу (migrate_to)
     * или если это сообщение в новой супергруппе о переносе из старой (migrate_from).
     * </p>
     *
     * @return true, если это миграция.
     */
    @JsonIgnore
    public boolean isGroupMigrationMessage() {
        return migrateToChatId != null || migrateFromChatId != null;
    }
    
    /**
     * Находит фотографию наилучшего качества среди доступных вариантов.
     * <p>
     * Telegram присылает фото в нескольких размерах (массив {@code photo}).
     * Этот метод выбирает самый большой по размеру или разрешению.
     * </p>
     *
     * @return объект PhotoSize или null, если фото нет.
     */
    @JsonIgnore
    public PhotoSize getBestPhoto() {
        if (photo == null || photo.isEmpty()) {
            return null;
        }
        return photo.stream()
                .max((p1, p2) -> {
                    int size1 = p1.getFileSize() != null ? p1.getFileSize() : 0;
                    int size2 = p2.getFileSize() != null ? p2.getFileSize() : 0;
                    if (size1 != size2) return Integer.compare(size1, size2);
                    
                    return Integer.compare(p1.getWidth(), p2.getWidth());
                })
                .orElse(photo.get(photo.size() - 1));
    }
    
    /**
     * Возвращает идентификатор файла (file_id) для фото наилучшего качества.
     * <p>
     * Удобно для быстрой загрузки или пересылки изображения.
     * </p>
     *
     * @return строка file_id или null.
     */
    @JsonIgnore
    public String getBestPhotoId() {
        PhotoSize best = getBestPhoto();
        return best != null ? best.getFileId() : null;
    }
    
    /**
     * Проверяет, содержит ли сообщение любые медиафайлы (фото, видео, стикеры и т.д.).
     * Не включает текст и служебные сообщения.
     *
     * @return true, если есть медиа.
     */
    @JsonIgnore
    public boolean hasMedia() {
        return hasPhoto() || hasVideo() || hasDocument() || hasAudio() || 
               hasVoice() || hasSticker() || hasAnimation() || hasVideoNote() ||
               hasContact() || hasDice() || hasGame() || hasPoll() || 
               hasVenue() || hasLocation() || hasStory();
    }
    
    /**
     * Алиас для {@link #hasMedia()}.
     * @return true, если сообщение содержит медиа.
     */
    @JsonIgnore
    public boolean isMediaMessage() {
        return hasMedia();
    }
    
    /**
     * Определяет тип содержимого сообщения в строковом виде.
     * <p>
     * Полезно для логирования или простой маршрутизации.
     * Возможные значения: text, photo, video, document, audio, voice, sticker,
     * animation, video_note, contact, dice, game, poll, venue, location, story, invoice, service.
     * </p>
     *
     * @return строка с типом контента.
     */
    @JsonIgnore
    public String getContentType() {
        if (hasText()) return "text";
        if (hasPhoto()) return "photo";
        if (hasVideo()) return "video";
        if (hasDocument()) return "document";
        if (hasAudio()) return "audio";
        if (hasVoice()) return "voice";
        if (hasSticker()) return "sticker";
        if (hasAnimation()) return "animation";
        if (hasVideoNote()) return "video_note";
        if (hasContact()) return "contact";
        if (hasDice()) return "dice";
        if (hasGame()) return "game";
        if (hasPoll()) return "poll";
        if (hasVenue()) return "venue";
        if (hasLocation()) return "location";
        if (hasStory()) return "story";
        if (hasInvoice()) return "invoice";
        if (isServiceMessage()) return "service";
        return "unknown";
    }
    
    /**
     * Проверяет, можно ли удалить сообщение.
     * <p>
     * Сообщение считается удаляемым, если оно не является служебным и не имеет защиты контента.
     * </p>
     * @return true, если удаление допустимо.
     */
    @JsonIgnore
    public boolean canBeDeleted() {
        return !isServiceMessage() && !hasProtectedContent();
    }
    
    /**
     * Проверяет, можно ли отредактировать сообщение.
     * <p>
     * Редактирование возможно, если сообщение содержит текст или подпись,
     * не является служебным и не имеет защиты контента.
     * </p>
     * @return true, если редактирование допустимо.
     */
    @JsonIgnore
    public boolean canBeEdited() {
        return hasTextOrCaption() && !isServiceMessage() && !hasProtectedContent();
    }
    
    /**
     * Пытается определить реального отправителя сообщения.
     * <p>
     * Если есть {@code from}, возвращает его.
     * Если сообщение анонимное (есть {@code sender_chat}), создает фиктивного пользователя на основе чата-отправителя.
     * </p>
     *
     * @return объект User или null.
     */
    @JsonIgnore
    public User getEffectiveSender() {
        if (from != null) return from;
        if (senderChat != null && senderChat.isUserChat()) {
            return User.builder()
                    .id(senderChat.getId())
                    .firstName(senderChat.getFirstName())
                    .lastName(senderChat.getLastName())
                    .username(senderChat.getUsername())
                    .isBot(false)
                    .build();
        }
        return null;
    }
    
    /**
     * Вспомогательный метод для получения отображаемого имени отправителя.
     * <p>
     * Учитывает {@code from} (пользователь) и {@code sender_chat} (канал/группа).
     * </p>
     *
     * @return имя отправителя.
     */
    @JsonIgnore
    public String getSenderDisplayName() {
        User effectiveSender = getEffectiveSender();
        if (effectiveSender != null) {
            return effectiveSender.getFullName();
        }
        if (senderChat != null) {
            return senderChat.getDisplayName();
        }
        return "Unknown Sender";
    }
    
    /**
     * Вспомогательный метод для получения упоминания отправителя.
     *
     * @return @username или имя.
     */
    @JsonIgnore
    public String getSenderMention() {
        User effectiveSender = getEffectiveSender();
        if (effectiveSender != null) {
            return effectiveSender.getMention();
        }
        if (senderChat != null && senderChat.getUsername() != null) {
            return "@" + senderChat.getUsername();
        }
        return getSenderDisplayName();
    }
}