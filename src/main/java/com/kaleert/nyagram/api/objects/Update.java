package com.kaleert.nyagram.api.objects;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.Accessors;

import com.kaleert.nyagram.api.objects.boost.*;
import com.kaleert.nyagram.api.objects.chat.Chat;
import com.kaleert.nyagram.api.objects.message.Message;
import com.kaleert.nyagram.api.objects.chatmember.ChatMember;

import java.io.Serializable;
import java.util.List;
import java.util.Optional;

/**
 * Основной объект, представляющий входящее событие (обновление) от Telegram.
 * <p>
 * В любой момент времени установлено только одно из опциональных полей
 * (например, либо {@code message}, либо {@code callbackQuery}).
 * </p>
 * 
 * @since 1.0.0
 */
@Data
@NoArgsConstructor
@Accessors(chain = true)
@JsonIgnoreProperties(ignoreUnknown = true)
public class Update implements Serializable {
    
    /** Уникальный идентификатор обновления. */
    @JsonProperty("update_id")
    private Long updateId;
    
    /** Новое входящее сообщение любого типа. */
    @JsonProperty("message")
    private Message message;
    
    /** Новая версия сообщения, которое было отредактировано. */
    @JsonProperty("edited_message")
    private Message editedMessage;
    
    /** Новый пост в канале. */
    @JsonProperty("channel_post")
    private Message channelPost;

    /** Отредактированный пост в канале. */
    @JsonProperty("edited_channel_post")
    private Message editedChannelPost;

    @JsonProperty("business_connection")
    private BusinessConnection businessConnection;

    @JsonProperty("business_message")
    private Message businessMessage;

    @JsonProperty("edited_business_message")
    private Message editedBusinessMessage;

    @JsonProperty("deleted_business_messages")
    private BusinessMessagesDeleted deletedBusinessMessages;

    @JsonProperty("callback_query")
    private CallbackQuery callbackQuery;

    @JsonProperty("inline_query")
    private InlineQuery inlineQuery;

    @JsonProperty("chosen_inline_result")
    private ChosenInlineResult chosenInlineResult;

    @JsonProperty("shipping_query")
    private ShippingQuery shippingQuery;

    @JsonProperty("pre_checkout_query")
    private PreCheckoutQuery preCheckoutQuery;

    @JsonProperty("purchased_paid_media")
    private PaidMediaPurchased purchasedPaidMedia;

    @JsonProperty("poll")
    private Poll poll;

    @JsonProperty("poll_answer")
    private PollAnswer pollAnswer;

    @JsonProperty("my_chat_member")
    private ChatMemberUpdated myChatMember;

    @JsonProperty("chat_member")
    private ChatMemberUpdated chatMember;

    @JsonProperty("chat_join_request")
    private ChatJoinRequest chatJoinRequest;

    @JsonProperty("chat_boost")
    private ChatBoostUpdated chatBoost;

    @JsonProperty("removed_chat_boost")
    private ChatBoostRemoved removedChatBoost;

    @JsonProperty("message_reaction")
    private MessageReactionUpdated messageReaction;

    @JsonProperty("message_reaction_count")
    private MessageReactionCountUpdated messageReactionCount;
    
    /**
     * Возвращает строковое представление типа обновления.
     *
     * @return строка типа (например, "message", "callback_query", "unknown").
     */
    public String getType() {
        if (message != null) return "message";
        if (callbackQuery != null) return "callback_query";
        if (editedMessage != null) return "edited_message";
        if (channelPost != null) return "channel_post";
        if (inlineQuery != null) return "inline_query";
        
        if (myChatMember != null) return "my_chat_member";
        if (chatMember != null) return "chat_member";
        if (messageReaction != null) return "message_reaction";
        
        if (businessMessage != null) return "business_message";
        if (editedBusinessMessage != null) return "edited_business_message";
        
        if (pollAnswer != null) return "poll_answer";
        if (preCheckoutQuery != null) return "pre_checkout_query";
        if (chatJoinRequest != null) return "chat_join_request";
        
        if (editedChannelPost != null) return "edited_channel_post";
        if (shippingQuery != null) return "shipping_query";
        if (chosenInlineResult != null) return "chosen_inline_result";
        if (chatBoost != null) return "chat_boost";
        if (removedChatBoost != null) return "removed_chat_boost";
        if (poll != null) return "poll";
        if (purchasedPaidMedia != null) return "purchased_paid_media";
        if (deletedBusinessMessages != null) return "deleted_business_messages";
        if (businessConnection != null) return "business_connection";
        if (messageReactionCount != null) return "message_reaction_count";

        return "unknown";
    }
    
    /**
     * Универсальный метод для получения ID чата из обновления.
     * <p>
     * Автоматически определяет тип обновления (Message, CallbackQuery, ChatMemberUpdated и т.д.)
     * и извлекает соответствующий {@code chat_id}.
     * </p>
     *
     * @return ID чата или null, если обновление не связано с конкретным чатом (например, InlineQuery).
     */
    public Long getChatId() {
        if (message != null) return message.getChat().getId();
        if (callbackQuery != null && callbackQuery.getMessage() != null) return callbackQuery.getMessage().getChat().getId();
        if (editedMessage != null) return editedMessage.getChat().getId();
        if (channelPost != null) return channelPost.getChat().getId();
        if (editedChannelPost != null) return editedChannelPost.getChat().getId();
        
        if (businessMessage != null) return businessMessage.getChat().getId();
        if (editedBusinessMessage != null) return editedBusinessMessage.getChat().getId();
        if (deletedBusinessMessages != null) return deletedBusinessMessages.getChat().getId();
        if (businessConnection != null) return businessConnection.getUserChatId();

        if (myChatMember != null) return myChatMember.getChat().getId();
        if (chatMember != null) return chatMember.getChat().getId();
        if (chatJoinRequest != null) return chatJoinRequest.getChat().getId();
        if (messageReaction != null) return messageReaction.getChat().getId();
        if (messageReactionCount != null) return messageReactionCount.getChat().getId();
        
        if (chatBoost != null) return chatBoost.getChat().getId();
        if (removedChatBoost != null) return removedChatBoost.getChat().getId();

        return null;
    }
    
    /**
     * Универсальный метод для получения ID пользователя (инициатора события).
     *
     * @return ID пользователя или null.
     */
    public Long getFromId() {
        if (message != null && message.getFrom() != null) return message.getFrom().getId();
        if (callbackQuery != null) return callbackQuery.getFrom().getId();
        if (inlineQuery != null) return inlineQuery.getFrom().getId();
        if (editedMessage != null && editedMessage.getFrom() != null) return editedMessage.getFrom().getId();
        
        if (myChatMember != null) return myChatMember.getFrom().getId();
        if (chatMember != null) return chatMember.getFrom().getId();
        if (chatJoinRequest != null) return chatJoinRequest.getFrom().getId();
        
        if (messageReaction != null && messageReaction.getUser() != null) return messageReaction.getUser().getId();
        if (pollAnswer != null && pollAnswer.getUser() != null) return pollAnswer.getUser().getId();
        if (chosenInlineResult != null) return chosenInlineResult.getFrom().getId();
        
        if (preCheckoutQuery != null) return preCheckoutQuery.getFrom().getId();
        if (shippingQuery != null) return shippingQuery.getFrom().getId();
        if (purchasedPaidMedia != null) return purchasedPaidMedia.getFrom().getId();
        if (businessMessage != null && businessMessage.getFrom() != null) return businessMessage.getFrom().getId();
        if (businessConnection != null) return businessConnection.getUser().getId();

        return null;
    }
    
    /**
     * Получает полезную нагрузку (текст, данные кнопки, запрос) из обновления.
     * <p>
     * Удобно для логирования или быстрого анализа содержимого.
     * </p>
     *
     * @return Строка с данными или null.
     */
    public String getPayload() {
        if (message != null) return message.getTextOrCaption();
        if (callbackQuery != null) return callbackQuery.getData();
        if (inlineQuery != null) return inlineQuery.getQuery();
        if (editedMessage != null) return editedMessage.getTextOrCaption();
        if (channelPost != null) return channelPost.getTextOrCaption();
        if (businessMessage != null) return businessMessage.getTextOrCaption();
        if (chosenInlineResult != null) return chosenInlineResult.getQuery();
        if (preCheckoutQuery != null) return preCheckoutQuery.getInvoicePayload();
        if (shippingQuery != null) return shippingQuery.getInvoicePayload();
        if (purchasedPaidMedia != null) return purchasedPaidMedia.getPaidMediaPayload();
        return null;
    }
    
    /**
     * Проверяет, является ли обновление командой.
     * <p>
     * Возвращает true, если текст сообщения или данные callback-запроса начинаются с "/".
     * </p>
     *
     * @return true, если это команда.
     */
    public boolean isCommand() {
        String txt = getPayload();
        return txt != null && txt.startsWith("/");
    }
    
    /**
     * Проверяет, содержит ли обновление новое сообщение.
     * @return true, если это message.
     */
    public boolean hasMessage() { return message != null; }
    
    /**
     * Проверяет, содержит ли обновление inline-запрос.
     * @return true, если это inline_query.
     */
    public boolean hasCallbackQuery() { return callbackQuery != null; }
    
    /**
     * Проверяет, содержит ли обновление входящий Inline-запрос.
     * <p>
     * Срабатывает, когда пользователь пишет {@code @botname ...} в поле ввода.
     * </p>
     *
     * @return true, если это inline_query.
     */
    public boolean hasInlineQuery() { return inlineQuery != null; }
    
    /**
     * Проверяет, содержит ли обновление отредактированное сообщение.
     * @return true, если это edited_message.
     */
    public boolean hasEditedMessage() { return editedMessage != null; }
    
    /**
     * Проверяет, содержит ли обновление пост из канала.
     * @return true, если это channel_post.
     */
    public boolean hasChannelPost() { return channelPost != null; }
    
    /**
     * Проверяет, содержит ли обновление сообщение от бизнес-аккаунта.
     * @return true, если это business_message.
     */
    public boolean hasBusinessMessage() { return businessMessage != null; }
    
    /**
     * Проверяет, содержит ли обновление изменение статуса самого бота.
     * (например, бот стал админом или был удален).
     * @return true, если это my_chat_member.
     */
    public boolean hasMyChatMember() { return myChatMember != null; }
    
    /**
     * Проверяет, содержит ли обновление изменение статуса другого участника.
     * @return true, если это chat_member.
     */
    public boolean hasChatMember() { return chatMember != null; }
    
    /**
     * Проверяет, содержит ли обновление реакцию на сообщение.
     * @return true, если это message_reaction.
     */
    public boolean hasMessageReaction() { return messageReaction != null; }
    
    /**
     * Описывает подключение бота к бизнес-аккаунту.
     *
     * @since 1.0.0
     */
    @Data @NoArgsConstructor @JsonIgnoreProperties(ignoreUnknown = true)
    public static class BusinessConnection implements Serializable {
        /** Уникальный идентификатор бизнес-соединения. */
        private String id;
        
        /** Пользователь бизнес-аккаунта, создавший соединение. */
        private User user;
        
        /** Идентификатор приватного чата с пользователем, создавшим соединение. */
        @JsonProperty("user_chat_id") private Long userChatId;
        
        /** Дата установления соединения (Unix timestamp). */
        private Long date;
        
        /** True, если бот может отвечать на сообщения от имени бизнес-аккаунта. */
        @JsonProperty("can_reply") private Boolean canReply;
        
        /** True, если соединение активно. */
        @JsonProperty("is_enabled") private Boolean isEnabled;
    }
    
    /**
     * Этот объект представляет собой уведомление об удалении сообщений в подключенном бизнес-чате.
     *
     * @since 1.0.0
     */
    @Data @NoArgsConstructor @JsonIgnoreProperties(ignoreUnknown = true)
    public static class BusinessMessagesDeleted implements Serializable {
        
        /** Уникальный идентификатор бизнес-соединения. */
        @JsonProperty("business_connection_id") private String businessConnectionId;
        
        /** Чат, в котором были удалены сообщения. */
        private Chat chat;
        
        /** Список идентификаторов удаленных сообщений в чате. */
        @JsonProperty("message_ids") private List<Integer> messageIds;
    }
    
    /**
     * Содержит информацию о покупке платного медиаконтента (за Telegram Stars).
     *
     * @since 1.0.0
     */
    @Data @NoArgsConstructor @JsonIgnoreProperties(ignoreUnknown = true)
    public static class PaidMediaPurchased implements Serializable {
        private User from;
        @JsonProperty("paid_media_payload") private String paidMediaPayload;
    }
    
    /**
     * Представляет изменение статуса буста в чате (добавление или изменение уровня).
     *
     * @since 1.0.0
     */
    @Data @NoArgsConstructor @JsonIgnoreProperties(ignoreUnknown = true)
    public static class ChatBoostUpdated implements Serializable {
        private Chat chat;
        private ChatBoost boost;
    }
    
    /**
     * Представляет собой уведомление об удалении буста из чата.
     *
     * @since 1.0.0
     */
    @Data @NoArgsConstructor @JsonIgnoreProperties(ignoreUnknown = true)
    public static class ChatBoostRemoved implements Serializable {
        private Chat chat;
        @JsonProperty("boost_id") private String boostId;
        @JsonProperty("remove_date") private Long removeDate;
        private ChatBoostSource source;
    }
    
    /**
     * Содержит информацию о бусте (Boost), добавленном чату.
     *
     * @since 1.0.0
     */
    @Data @NoArgsConstructor @JsonIgnoreProperties(ignoreUnknown = true)
    public static class ChatBoost implements Serializable {
        @JsonProperty("boost_id") private String boostId;
        @JsonProperty("add_date") private Long addDate;
        @JsonProperty("expiration_date") private Long expirationDate;
        private ChatBoostSource source;
    }
    
    /**
     * Описывает источник буста (откуда он пришел).
     * Это может быть премиум-пользователь, подарочный код или розыгрыш.
     *
     * @since 1.0.0
     */
    @Data @NoArgsConstructor @JsonIgnoreProperties(ignoreUnknown = true)
    public static class ChatBoostSource implements Serializable {
        private String source;
        private User user;
        @JsonProperty("giveaway_message_id") private Integer giveawayMessageId;
        @JsonProperty("prize_star_count") private Integer prizeStarCount;
        @JsonProperty("is_unclaimed") private Boolean isUnclaimed;
    }
    
    /**
     * Этот объект представляет входящий callback-запрос, отправленный при нажатии на inline-кнопку.
     * <p>
     * При получении этого обновления бот должен обязательно ответить на него методом
     * {@link com.kaleert.nyagram.api.methods.AnswerCallbackQuery}, иначе у пользователя
     * будет вечно крутиться индикатор загрузки.
     * </p>
     *
     * @since 1.0.0
     */
    @Data @NoArgsConstructor @JsonIgnoreProperties(ignoreUnknown = true)
    public static class CallbackQuery implements Serializable {
        
        /** Уникальный идентификатор запроса. */
        private String id;
        
        /** Пользователь, нажавший кнопку. */
        private User from;
        
        /** 
         * Сообщение, к которому была привязана клавиатура. 
         * Может быть null, если сообщение слишком старое.
         */
        private Message message;
        
        /** Идентификатор inline-сообщения, если кнопка была в сообщении, отправленном через inline-режим. */
        @JsonProperty("inline_message_id") private String inlineMessageId;
        
        /** 
         * Глобальный идентификатор, уникально соответствующий чату, в который было отправлено сообщение.
         * Полезен для ведения статистики в inline-режиме.
         */
        @JsonProperty("chat_instance") private String chatInstance;
        
        /** 
         * Данные, привязанные к кнопке (callback_data). 
         * Присутствует, если кнопка не является кнопкой игры.
         */
        private String data;
        
        /** 
         * Короткое имя игры, которую нужно вернуть. 
         * Присутствует только если это кнопка игры.
         */
        @JsonProperty("game_short_name") private String gameShortName;
    }
    
    /**
     * Представляет входящий Inline-запрос.
     * Генерируется, когда пользователь вводит @botname в поле ввода текста.
     *
     * @since 1.0.0
     */
    @Data @NoArgsConstructor @JsonIgnoreProperties(ignoreUnknown = true)
    public static class InlineQuery implements Serializable {
        private String id;
        private User from;
        private String query;
        private String offset;
        @JsonProperty("chat_type") private String chatType;
        private Location location;
    }
    
    /**
     * Представляет результат выбора пользователем ответа в inline-режиме.
     * Для получения этого обновления необходимо включить inline feedback в @BotFather.
     *
     * @since 1.0.0
     */
    @Data @NoArgsConstructor @JsonIgnoreProperties(ignoreUnknown = true)
    public static class ChosenInlineResult implements Serializable {
        @JsonProperty("result_id") private String resultId;
        private User from;
        private Location location;
        @JsonProperty("inline_message_id") private String inlineMessageId;
        private String query;
    }
    
    /**
     * Представляет изменение реакции пользователя на сообщение.
     * Содержит старую и новую реакцию.
     *
     * @since 1.0.0
     */
    @Data @NoArgsConstructor @JsonIgnoreProperties(ignoreUnknown = true)
    public static class MessageReactionUpdated implements Serializable {
        private Chat chat;
        @JsonProperty("message_id") private Integer messageId;
        private User user;
        @JsonProperty("actor_chat") private Chat actorChat;
        private Long date;
        @JsonProperty("old_reaction") private List<ReactionType> oldReaction;
        @JsonProperty("new_reaction") private List<ReactionType> newReaction;
    }
    
    /**
     * Представляет изменение счетчика реакций на сообщении.
     * Приходит, если в чате включены анонимные реакции.
     *
     * @since 1.0.0
     */
    @Data @NoArgsConstructor @JsonIgnoreProperties(ignoreUnknown = true)
    public static class MessageReactionCountUpdated implements Serializable {
        private Chat chat;
        @JsonProperty("message_id") private Integer messageId;
        private Long date;
        private List<ReactionCount> reactions;
    }

    /**
     * Представляет тип реакции.
     *
     * @since 1.0.0
     */
    @Data @NoArgsConstructor @JsonIgnoreProperties(ignoreUnknown = true)
    public static class ReactionType implements Serializable {
        /** Тип реакции: "emoji", "custom_emoji" или "paid". */
        private String type; 
        
        /** Эмодзи (для type="emoji"). */
        private String emoji;
        
        /** Идентификатор кастомного эмодзи (для type="custom_emoji"). */
        @JsonProperty("custom_emoji_id") private String customEmojiId;
    }

    /**
     * Представляет количество реакций определенного типа.
     *
     * @since 1.0.0
     */
    @Data @NoArgsConstructor @JsonIgnoreProperties(ignoreUnknown = true)
    public static class ReactionCount implements Serializable {
        /** Тип реакции. */
        private ReactionType type;
        
        /** Общее количество таких реакций. */
        @JsonProperty("total_count") private Integer totalCount;
    }
    
    /**
     * Представляет изменение статуса участника чата.
     * Используется для отслеживания вступления, выхода, бана или назначения администраторов.
     *
     * @since 1.0.0
     */
    @Data @NoArgsConstructor @JsonIgnoreProperties(ignoreUnknown = true)
    public static class ChatMemberUpdated implements Serializable {
        private Chat chat;
        private User from;
        private Long date;
        @JsonProperty("old_chat_member") private ChatMember oldChatMember;
        @JsonProperty("new_chat_member") private ChatMember newChatMember;
        @JsonProperty("invite_link") private ChatInviteLink inviteLink;
        @JsonProperty("via_join_request") private Boolean viaJoinRequest;
        @JsonProperty("via_chat_folder_invite_link") private Boolean viaChatFolderInviteLink;
    }
    
    /**
     * Представляет заявку на вступление в чат.
     * Приходит, если в чате включено одобрение заявок администратором.
     *
     * @since 1.0.0
     */
    @Data @NoArgsConstructor @JsonIgnoreProperties(ignoreUnknown = true)
    public static class ChatJoinRequest implements Serializable {
        private Chat chat;
        private User from;
        @JsonProperty("user_chat_id") private Long userChatId;
        private Long date;
        private String bio;
        @JsonProperty("invite_link") private ChatInviteLink inviteLink;
    }

    /**
     * Содержит информацию о запросе на предварительную проверку перед оплатой (Pre-Checkout Query).
     * Бот должен ответить на этот запрос в течение 10 секунд.
     *
     * @see com.kaleert.nyagram.api.methods.invoices.AnswerPreCheckoutQuery
     * @since 1.0.0
     */
    @Data @NoArgsConstructor @JsonIgnoreProperties(ignoreUnknown = true)
    public static class PreCheckoutQuery implements Serializable {
        /** Уникальный идентификатор запроса. */
        private String id;
        
        /** Пользователь, отправивший запрос. */
        private User from;
        
        /** Трехбуквенный код валюты (ISO 4217). */
        private String currency;
        
        /** Общая цена в минимальных единицах валюты (копейки, центы). */
        @JsonProperty("total_amount") private Integer totalAmount;
        
        /** Полезная нагрузка, указанная ботом при создании счета. */
        @JsonProperty("invoice_payload") private String invoicePayload;
        
        /** Идентификатор выбранной опции доставки (если применимо). */
        @JsonProperty("shipping_option_id") private String shippingOptionId;
        
        /** Информация о заказе, предоставленная пользователем. */
        @JsonProperty("order_info") private OrderInfo orderInfo;
    }

    /**
     * Содержит информацию о запросе на расчет доставки (Shipping Query).
     *
     * @see com.kaleert.nyagram.api.methods.invoices.AnswerShippingQuery
     * @since 1.0.0
     */
    @Data @NoArgsConstructor @JsonIgnoreProperties(ignoreUnknown = true)
    public static class ShippingQuery implements Serializable {
        /** Уникальный идентификатор запроса. */
        private String id;
        
        /** Пользователь, отправивший запрос. */
        private User from;
        
        /** Полезная нагрузка, указанная ботом при создании счета. */
        @JsonProperty("invoice_payload") private String invoicePayload;
        
        /** Адрес доставки, указанный пользователем. */
        @JsonProperty("shipping_address") private ShippingAddress shippingAddress;
    }

    /**
     * Представляет ответ пользователя в неанонимном опросе.
     *
     * @since 1.0.0
     */
    @Data @NoArgsConstructor @JsonIgnoreProperties(ignoreUnknown = true)
    public static class PollAnswer implements Serializable {
        /** Уникальный идентификатор опроса. */
        @JsonProperty("poll_id") private String pollId;
        
        /** Чат, в котором был отправлен голос (если голос от имени чата/канала). */
        @JsonProperty("voter_chat") private Chat voterChat;
        
        /** Пользователь, который проголосовал. */
        private User user;
        
        /** Список индексов выбранных опций (0-based). */
        @JsonProperty("option_ids") private List<Integer> optionIds;
    }
    
    /**
     * Содержит информацию об опросе (состояние опроса).
     * Приходит при изменении состояния (например, закрытии) или обновлении счетчиков голосов.
     *
     * @since 1.0.0
     */
    @Data @NoArgsConstructor @JsonIgnoreProperties(ignoreUnknown = true)
    public static class Poll implements Serializable {
        private String id;
        private String question;
        private List<PollOption> options;
        @JsonProperty("total_voter_count") private Integer totalVoterCount;
        @JsonProperty("is_closed") private Boolean isClosed;
        @JsonProperty("is_anonymous") private Boolean isAnonymous;
        private String type;
        @JsonProperty("allows_multiple_answers") private Boolean allowsMultipleAnswers;
        @JsonProperty("correct_option_id") private Integer correctOptionId;
        @JsonProperty("explanation") private String explanation;
        @JsonProperty("open_period") private Integer openPeriod;
        @JsonProperty("close_date") private Long closeDate;
    }

    /**
     * Представляет информацию о варианте ответа в опросе.
     *
     * @since 1.0.0
     */
    @Data @NoArgsConstructor @JsonIgnoreProperties(ignoreUnknown = true)
    public static class PollOption implements Serializable {
        /** Текст опции (1-100 символов). */
        private String text;
        /** Количество голосов за эту опцию. */
        @JsonProperty("voter_count") private Integer voterCount;
    }
}