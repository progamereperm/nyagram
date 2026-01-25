package com.kaleert.nyagram.command;

import com.kaleert.nyagram.api.methods.send.SendMessage;
import com.kaleert.nyagram.api.methods.updatingmessages.DeleteMessage;
import com.kaleert.nyagram.api.objects.Update;
import com.kaleert.nyagram.api.objects.User;
import com.kaleert.nyagram.api.objects.message.Message;
import com.kaleert.nyagram.api.objects.message.MaybeInaccessibleMessage;
import com.kaleert.nyagram.api.objects.replykeyboard.ReplyKeyboard;
import com.kaleert.nyagram.client.NyagramClient;
import com.kaleert.nyagram.api.objects.forum.ForumIconColor;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import java.util.Optional;
import java.util.concurrent.CompletableFuture;

/**
 * Предоставляет доступ к текущему обновлению (Update) и утилитарным методам для взаимодействия.
 * <p>
 * Этот объект создается для каждого входящего обновления и передается в обработчики.
 * Он кэширует разрешенные сущности (User, Chat, Message), чтобы избежать повторного парсинга.
 * </p>
 * 
 * @since 1.0.0
 */
@Slf4j
@Getter
@RequiredArgsConstructor
public class CommandContext {
    
    /**
     * Сырой объект Update, полученный от Telegram API.
     */
    private final Update update;
    
    /**
     * HTTP-клиент, используемый для отправки запросов к Telegram API.
     */
    private final NyagramClient client;
    
    private Message cachedMessage;
    private User cachedUser;
    private Long cachedChatId;
    
    /**
     * Возвращает сообщение (Message), связанное с этим обновлением.
     * Обрабатывает стандартные сообщения, посты в каналах, отредактированные сообщения и сообщения из callback-запросов.
     * 
     * @return Optional, содержащий сообщение, или empty, если тип обновления не содержит сообщения.
     */
    public Optional<Message> getMessage() {
        if (cachedMessage != null) {
            return Optional.of(cachedMessage);
        }
        
        if (update.getMessage() != null) {
            cachedMessage = update.getMessage();
        } else if (update.getCallbackQuery() != null) {
            MaybeInaccessibleMessage maybeMessage = update.getCallbackQuery().getMessage();
            if (maybeMessage instanceof Message msg) {
                cachedMessage = msg;
            }
        } else if (update.getEditedMessage() != null) {
            cachedMessage = update.getEditedMessage();
        } else if (update.getChannelPost() != null) {
            cachedMessage = update.getChannelPost();
        } else if (update.getEditedChannelPost() != null) {
            cachedMessage = update.getEditedChannelPost();
        }
        
        return Optional.ofNullable(cachedMessage);
    }
    
    /**
     * Возвращает ID чата, в котором произошло это обновление.
     * <p>
     * Работает для личных чатов, групп, супергрупп и каналов.
     * </p>
     *
     * @return ID чата (Long).
     * @throws IllegalStateException если ID чата невозможно определить из обновления.
     */
    public Long getChatId() {
        if (cachedChatId != null) {
            return cachedChatId;
        }
        
        Long chatId = update.getChatId();
        if (chatId != null) {
            cachedChatId = chatId;
            return chatId;
        }
        
        throw new IllegalStateException("Cannot determine Chat ID from this update");
    }
    
    /**
     * Возвращает пользователя (User), инициировавшего это обновление.
     * <p>
     * Извлекает пользователя из Message, Callback, InlineQuery, ChatMember и других типов обновлений.
     * </p>
     *
     * @return объект User.
     * @throws IllegalStateException если пользователь не может быть определен (маловероятно).
     */
    public User getTelegramUser() {
        if (cachedUser != null) {
            return cachedUser;
        }
        
        Long fromId = update.getFromId();
        
        if (update.getMessage() != null) cachedUser = update.getMessage().getFrom();
        else if (update.getCallbackQuery() != null) cachedUser = update.getCallbackQuery().getFrom();
        else if (update.getEditedMessage() != null) cachedUser = update.getEditedMessage().getFrom();
        else if (update.getMyChatMember() != null) cachedUser = update.getMyChatMember().getFrom();
        else if (update.getChatMember() != null) cachedUser = update.getChatMember().getFrom();
        else if (update.getMessageReaction() != null) cachedUser = update.getMessageReaction().getUser();
        else if (update.getInlineQuery() != null) cachedUser = update.getInlineQuery().getFrom();
        else if (update.getChannelPost() != null) cachedUser = update.getChannelPost().getFrom();
        
        if (cachedUser == null) {
            if (fromId != null) {
                cachedUser = User.builder().id(fromId).firstName("Unknown").isBot(false).build();
            } else {
                throw new IllegalStateException("Cannot determine User from this update type: " + update.getType());
            }
        }
        
        return cachedUser;
    }
    
    /**
     * Вспомогательный метод для получения ID пользователя.
     *
     * @return ID пользователя.
     */
    public Long getUserId() {
        return getTelegramUser().getId();
    }
    
    /**
     * Возвращает идентификатор топика (Message Thread ID), если сообщение отправлено в ветку форума.
     * <p>
     * Если чат не является форумом или сообщение находится в "General" (и он не скрыт), может вернуть null.
     * </p>
     *
     * @return ID топика или null.
     */
    public Integer getTopicId() {
        return getMessage()
                .filter(m -> Boolean.TRUE.equals(m.getIsTopicMessage()))
                .map(Message::getMessageThreadId)
                .orElse(null);
    }
    
   /**
     * Получает текстовое содержимое сообщения (или подпись, если это медиа).
     *
     * @return текст сообщения или пустая строка.
     */
    public String getText() {
        return getMessage()
                .map(Message::getTextOrCaption)
                .orElse("");
    }
    
    /**
     * Проверяет, является ли чат личным (Private).
     *
     * @return true, если это личный чат с пользователем.
     */
    public boolean isPrivateChat() {
        return getMessage()
                .map(Message::isUserChat)
                .orElse(false);
    }
    
    /**
     * Проверяет, является ли чат группой или супергруппой.
     *
     * @return true, если это групповой чат.
     */
    public boolean isGroupChat() {
        return getMessage()
                .map(Message::isGroupChat)
                .orElse(false);
    }
    
    /**
     * Отправляет текстовый ответ в текущий чат.
     * По умолчанию использует режим парсинга HTML.
     * 
     * @param text Текст сообщения для отправки.
     * @return Future, который завершится отправленным сообщением.
     */
    public CompletableFuture<Message> reply(String text) {
        return reply(text, "HTML", null, null);
    }
    
    /**
     * Отправляет текстовый ответ с настраиваемым режимом парсинга.
     * 
     * @param text Текст сообщения.
     * @param parseMode "HTML", "MarkdownV2" или null.
     * @return Future, который завершится отправленным сообщением.
     */
    public CompletableFuture<Message> reply(String text, String parseMode) {
        return reply(text, parseMode, null, null);
    }
    
    /**
     * Полный метод отправки сообщения с клавиатурой и ответом на конкретное сообщение.
     *
     * @param text Текст сообщения.
     * @param parseMode Режим парсинга ("HTML", "MarkdownV2").
     * @param replyToMessageId ID сообщения, на которое нужно ответить (или null).
     * @param replyMarkup Клавиатура (Inline или Reply) или null.
     * @return Future, который завершится отправленным сообщением.
     */
    public CompletableFuture<Message> reply(String text, String parseMode, 
                                            Integer replyToMessageId, 
                                            ReplyKeyboard replyMarkup) {
        var msgBuilder = SendMessage.builder()
                .chatId(getChatId().toString())
                .text(text)
                .parseMode(parseMode)
                .replyToMessageId(replyToMessageId)
                .replyMarkup(replyMarkup);
        
        if (update.getMessage() != null && Boolean.TRUE.equals(update.getMessage().getIsTopicMessage())) {
            msgBuilder.messageThreadId(update.getMessage().getMessageThreadId());
        }
        
        return client.executeAsync(msgBuilder.build());
    }
    
    /**
     * Удаляет конкретное сообщение в текущем чате.
     * 
     * @param messageId ID сообщения для удаления. Если null, пытается удалить сообщение команды.
     * @return true, если удаление прошло успешно.
     */
    public boolean deleteMessage(Integer messageId) {
        try {
            Long msgIdLong = messageId != null ? messageId.longValue() : 
                    getMessage().map(Message::getMessageId).orElse(null);
            
            if (msgIdLong == null) return false;

            DeleteMessage deleteMessage = new DeleteMessage(getChatId().toString(), msgIdLong.intValue());
            return Boolean.TRUE.equals(client.execute(deleteMessage));
            
        } catch (Exception e) {
            log.warn("Failed to delete message: {}", e.getMessage());
            return false;
        }
    }
}