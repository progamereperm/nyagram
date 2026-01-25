package com.kaleert.nyagram.api.objects.replykeyboard.buttons;

import lombok.*;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.kaleert.nyagram.api.meta.BotApiObject;
import com.kaleert.nyagram.api.objects.webapp.WebAppInfo;
import com.kaleert.nyagram.api.objects.LoginUrl; 
import com.kaleert.nyagram.api.objects.games.CallbackGame;
import com.kaleert.nyagram.api.objects.inlinequery.SwitchInlineQueryChosenChat;

/**
 * Представляет одну кнопку внутри Inline-клавиатуры.
 * <p>
 * <b>Важно:</b> Вы должны использовать ровно одно из опциональных полей (кроме {@code text}).
 * </p>
 *
 * @param text Текст на кнопке.
 * @param url HTTP или tg:// ссылка, которая будет открыта при нажатии.
 * @param callbackData Данные, которые будут отправлены боту в callback_query при нажатии (1-64 байта).
 * @param webApp Описание Web App, которое будет запущено.
 * @param loginUrl URL для авторизации через Telegram Login Widget.
 * @param switchInlineQuery Если задано, нажатие переключит пользователя в inline-режим с этим запросом.
 * @param switchInlineQueryCurrentChat То же, но вставляет запрос в текущий чат.
 * @param switchInlineQueryChosenChat Предлагает выбрать чат, затем вставляет inline-запрос.
 * @param callbackGame Описание игры, которая будет запущена.
 * @param pay Если true, отправляет кнопку оплаты (Pay button). Всегда первая в клавиатуре счета.
 *
 * @since 1.0.0
 */
@Builder
@JsonInclude(JsonInclude.Include.NON_NULL)
@JsonIgnoreProperties(ignoreUnknown = true)
public record InlineKeyboardButton(
    @JsonProperty("text") String text,
    @JsonProperty("url") String url,
    @JsonProperty("callback_data") String callbackData,
    @JsonProperty("web_app") WebAppInfo webApp,
    @JsonProperty("login_url") LoginUrl loginUrl,
    @JsonProperty("switch_inline_query") String switchInlineQuery,
    @JsonProperty("switch_inline_query_current_chat") String switchInlineQueryCurrentChat,
    @JsonProperty("switch_inline_query_chosen_chat") SwitchInlineQueryChosenChat switchInlineQueryChosenChat, 
    @JsonProperty("callback_game") CallbackGame callbackGame,
    @JsonProperty("pay") Boolean pay
) implements BotApiObject {
    
    /**
     * Создает кнопку-ссылку.
     *
     * @param text Текст на кнопке.
     * @param url URL, который будет открыт при нажатии.
     * @return объект кнопки.
     */
    public static InlineKeyboardButton url(String text, String url) {
        return new InlineKeyboardButton(text, url, null, null, null, null, null, null, null, null);
    }

    /**
     * Создает callback-кнопку.
     *
     * @param text Текст на кнопке.
     * @param callbackData Данные (до 64 байт), которые придут боту в {@code CallbackQuery}.
     * @return объект кнопки.
     * @throws IllegalArgumentException если data > 64 байт.
     */
    public static InlineKeyboardButton callback(String text, String callbackData) {
        if (callbackData != null && callbackData.getBytes().length > 64) {
             throw new IllegalArgumentException("Callback data must be <= 64 bytes");
        }
        return new InlineKeyboardButton(text, null, callbackData, null, null, null, null, null, null, null);
    }

    /**
     * Создает кнопку для открытия Web App.
     *
     * @param text Текст на кнопке.
     * @param webAppInfo Информация о Web App.
     * @return объект кнопки.
     */
    public static InlineKeyboardButton webApp(String text, WebAppInfo webAppInfo) {
        return new InlineKeyboardButton(text, null, null, webAppInfo, null, null, null, null, null, null);
    }

    /**
     * Создает кнопку переключения в inline-режим.
     * <p>
     * При нажатии откроется поле ввода в другом чате с именем бота и указанным запросом.
     * </p>
     *
     * @param text Текст на кнопке.
     * @param query Текст inline-запроса.
     * @return объект кнопки.
     */
    public static InlineKeyboardButton switchInlineQuery(String text, String query) {
        return new InlineKeyboardButton(text, null, null, null, null, query, null, null, null, null);
    }
    
    /**
     * Создает кнопку переключения в inline-режим в текущем чате.
     *
     * @param text Текст на кнопке.
     * @param query Текст inline-запроса.
     * @return объект кнопки.
     */
    public static InlineKeyboardButton switchInlineQueryCurrentChat(String text, String query) {
        return new InlineKeyboardButton(text, null, null, null, null, null, query, null, null, null);
    }
    
    /**
     * Создает кнопку авторизации (Telegram Login).
     *
     * @param text Текст на кнопке.
     * @param loginUrl Параметры авторизации.
     * @return объект кнопки.
     */
    public static InlineKeyboardButton login(String text, LoginUrl loginUrl) {
        return new InlineKeyboardButton(text, null, null, null, loginUrl, null, null, null, null, null);
    }

    /**
     * Создает кнопку оплаты (Pay).
     * <p>
     * Должна быть первой кнопкой в клавиатуре, прикрепленной к Invoice.
     * </p>
     *
     * @param text Текст на кнопке (обычно "Заплатить X").
     * @return объект кнопки.
     */
    public static InlineKeyboardButton pay(String text) {
        return new InlineKeyboardButton(text, null, null, null, null, null, null, null, null, true);
    }
    
    /**
     * Создает кнопку выбора чата для inline-режима.
     *
     * @param text Текст на кнопке.
     * @param switchChat Параметры выбора чата.
     * @return объект кнопки.
     */
    public static InlineKeyboardButton switchInlineQueryChosenChat(String text, SwitchInlineQueryChosenChat switchChat) {
        return new InlineKeyboardButton(text, null, null, null, null, null, null, switchChat, null, null);
    }
}