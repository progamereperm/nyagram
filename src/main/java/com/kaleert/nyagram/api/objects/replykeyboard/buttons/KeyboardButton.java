package com.kaleert.nyagram.api.objects.replykeyboard.buttons;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.kaleert.nyagram.api.meta.BotApiObject;
import com.kaleert.nyagram.api.objects.webapp.WebAppInfo;

/**
 * Представляет кнопку в нижней клавиатуре (Reply Keyboard).
 * <p>
 * Может отправлять текст или запрашивать контакт/локацию/опрос/пользователя.
 * </p>
 *
 * @param text Текст кнопки. Будет отправлен как сообщение при нажатии.
 * @param requestContact Если true, нажатие отправит номер телефона пользователя.
 * @param requestLocation Если true, нажатие отправит текущую геолокацию.
 * @param requestPoll Если задано, пользователю предложат создать опрос.
 * @param webApp Если задано, откроет указанное Web App.
 * @param requestUser Если задано, откроет диалог выбора пользователя.
 * @param requestChat Если задано, откроет диалог выбора чата.
 *
 * @since 1.0.0
 */
@JsonInclude(JsonInclude.Include.NON_NULL)
@JsonIgnoreProperties(ignoreUnknown = true)
public record KeyboardButton(
    @JsonProperty("text") String text,
    @JsonProperty("request_contact") Boolean requestContact,
    @JsonProperty("request_location") Boolean requestLocation,
    @JsonProperty("request_poll") KeyboardButtonPollType requestPoll,
    @JsonProperty("web_app") WebAppInfo webApp,
    @JsonProperty("request_user") KeyboardButtonRequestUser requestUser,
    @JsonProperty("request_chat") KeyboardButtonRequestChat requestChat
) implements BotApiObject {

    /**
     * Создает простую текстовую кнопку.
     *
     * @param text Текст на кнопке.
     * @return объект кнопки.
     */
    public static KeyboardButton text(String text) {
        return new KeyboardButton(text, null, null, null, null, null, null);
    }

    /**
     * Создает кнопку запроса контакта пользователя.
     * <p>
     * При нажатии пользователь отправит свой номер телефона.
     * </p>
     *
     * @param text Текст на кнопке.
     * @return объект кнопки.
     */
    public static KeyboardButton requestContact(String text) {
        return new KeyboardButton(text, true, null, null, null, null, null);
    }

    /**
     * Создает кнопку запроса текущей геолокации.
     *
     * @param text Текст на кнопке.
     * @return объект кнопки.
     */
    public static KeyboardButton requestLocation(String text) {
        return new KeyboardButton(text, null, true, null, null, null, null);
    }

    /**
     * Создает кнопку выбора пользователя.
     * <p>
     * При нажатии открывается список пользователей, которых можно "пошерить" с ботом.
     * </p>
     *
     * @param text Текст на кнопке.
     * @param requestId Уникальный идентификатор запроса (вернется в {@code UsersShared}).
     * @param bot Если true, можно выбирать только ботов. Если false — только людей.
     * @return объект кнопки.
     */
    public static KeyboardButton requestUser(String text, int requestId, boolean bot) {
        return new KeyboardButton(text, null, null, null, null, 
            new KeyboardButtonRequestUser(requestId, bot, null), null);
    }
    
    /**
     * Создает кнопку выбора чата (канала или группы).
     * <p>
     * При нажатии открывается список чатов, которыми пользователь может поделиться.
     * </p>
     *
     * @param text Текст на кнопке.
     * @param requestId Уникальный идентификатор запроса (вернется в {@code ChatShared}).
     * @param channel Если true, можно выбирать только каналы. Если false — только группы.
     * @return объект кнопки.
     */
    public static KeyboardButton requestChat(String text, int requestId, boolean channel) {
        return new KeyboardButton(text, null, null, null, null, null, 
            new KeyboardButtonRequestChat(requestId, channel, null, null, null, null, null, null));
    }
    
    /**
     * Тип опроса, который будет предложено создать пользователю.
     *
     * @param type Тип опроса: "quiz" (викторина) или "regular" (обычный).
     */
    public record KeyboardButtonPollType(@JsonProperty("type") String type) {}
}