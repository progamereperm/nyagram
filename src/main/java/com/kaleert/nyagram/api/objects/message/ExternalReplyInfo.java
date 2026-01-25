package com.kaleert.nyagram.api.objects.message;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.kaleert.nyagram.api.meta.BotApiObject;
import com.kaleert.nyagram.api.objects.*;
import com.kaleert.nyagram.api.objects.chat.Chat;
import com.kaleert.nyagram.api.objects.games.Game;
import com.kaleert.nyagram.api.objects.polls.Poll;
import com.kaleert.nyagram.api.objects.stickers.Sticker;
import com.kaleert.nyagram.api.objects.message.origin.MessageOrigin;
import java.util.List;

/**
 * Содержит информацию о сообщении, на которое ответили, если оно пришло из другого чата или топика форума.
 *
 * @param origin Источник сообщения (Message Origin).
 * @param chat Чат, в котором было отправлено оригинальное сообщение.
 * @param messageId Уникальный идентификатор сообщения внутри чата.
 * @param linkPreviewOptions Настройки превью ссылок.
 * @param animation Анимация (если сообщение содержит анимацию).
 * @param audio Аудиофайл (если сообщение содержит аудио).
 * @param document Документ (если сообщение содержит документ).
 * @param photo Фото (если сообщение содержит фото).
 * @param sticker Стикер (если сообщение содержит стикер).
 * @param story История (если сообщение содержит историю).
 * @param video Видео (если сообщение содержит видео).
 * @param videoNote Видеосообщение (если сообщение содержит видеосообщение).
 * @param voice Голосовое сообщение (если сообщение содержит голосовое сообщение).
 * @param hasMediaSpoiler True, если медиа скрыто спойлером.
 * @param contact Контакт (если сообщение содержит контакт).
 * @param dice Игральная кость (если сообщение содержит dice).
 * @param game Игра (если сообщение содержит игру).
 * @param giveaway Розыгрыш (если сообщение содержит розыгрыш).
 * @param giveawayWinners Победители розыгрыша (если сообщение содержит победителей).
 * @param invoice Счет (если сообщение содержит счет).
 * @param location Геолокация (если сообщение содержит локацию).
 * @param poll Опрос (если сообщение содержит опрос).
 * @param venue Место на карте (если сообщение содержит venue).
 *
 * @since 1.0.0
 */
@JsonIgnoreProperties(ignoreUnknown = true)
public record ExternalReplyInfo(
    @JsonProperty("origin") MessageOrigin origin,
    @JsonProperty("chat") Chat chat,
    @JsonProperty("message_id") Integer messageId,
    @JsonProperty("link_preview_options") LinkPreviewOptions linkPreviewOptions,
    @JsonProperty("animation") Animation animation,
    @JsonProperty("audio") Audio audio,
    @JsonProperty("document") Document document,
    @JsonProperty("photo") List<PhotoSize> photo,
    @JsonProperty("sticker") Sticker sticker,
    @JsonProperty("story") Object story,
    @JsonProperty("video") Video video,
    @JsonProperty("video_note") VideoNote videoNote,
    @JsonProperty("voice") Voice voice,
    @JsonProperty("has_media_spoiler") Boolean hasMediaSpoiler,
    @JsonProperty("contact") Contact contact,
    @JsonProperty("dice") Dice dice,
    @JsonProperty("game") Game game,
    @JsonProperty("giveaway") Object giveaway,
    @JsonProperty("giveaway_winners") Object giveawayWinners,
    @JsonProperty("invoice") Object invoice,
    @JsonProperty("location") Location location,
    @JsonProperty("poll") Poll poll,
    @JsonProperty("venue") Venue venue
) implements BotApiObject {}