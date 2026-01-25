package com.kaleert.nyagram.util;

import lombok.experimental.UtilityClass;
import com.kaleert.nyagram.api.objects.message.Message;

/**
 * Утилитарный класс для анализа содержимого сообщений.
 *
 * @since 1.0.0
 */
@UtilityClass
public class MessageUtils {
    
    /**
     * Проверяет, является ли сообщение служебным (Service Message).
     * <p>
     * Служебные сообщения (вход участника, закреп, смена названия и т.д.)
     * обычно не содержат пользовательского текста и обрабатываются иначе.
     * </p>
     *
     * @param message Сообщение для проверки.
     * @return true, если сообщение служебное.
     */
    public static boolean isServiceMessage(Message message) {
        if (message == null) return false;

        return message.getNewChatMembers() != null ||
               message.getLeftChatMember() != null ||
               message.getNewChatTitle() != null ||
               message.getNewChatPhoto() != null ||
               message.getPinnedMessage() != null ||
               message.getGroupChatCreated() != null ||
               message.getForumTopicCreated() != null ||
               message.getForumTopicEdited() != null ||
               message.getForumTopicReopened() != null ||
               message.getForumTopicClosed() != null ||
               message.getDeleteChatPhoto() != null ||
               message.getMigrateToChatId() != null ||
               message.getMigrateFromChatId() != null ||
               message.getVideoChatStarted() != null ||
               message.getVideoChatEnded() != null ||
               message.getVideoChatScheduled() != null;
    }
}