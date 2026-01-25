package com.kaleert.nyagram.feature.forum;

import com.kaleert.nyagram.api.objects.forum.ForumTopic;
import com.kaleert.nyagram.api.objects.forum.ForumIconColor;
import java.util.Optional;
import java.util.concurrent.CompletableFuture;

/**
 * Интерфейс сервиса для управления топиками (форумами) в супергруппах.
 * <p>
 * Предоставляет высокоуровневые методы для создания, редактирования и управления жизненным циклом топиков.
 * </p>
 *
 * @since 1.0.0
 */
public interface TopicService {
    
    /**
     * Создает новый топик.
     *
     * @param chatId ID чата (супергруппы).
     * @param name Название топика.
     * @param color Цвет иконки (опционально).
     * @return Future с созданным топиком.
     */
    CompletableFuture<ForumTopic> createTopic(Long chatId, String name, ForumIconColor color);

    /**
     * Получает ID существующего топика по имени или создает новый, если такого нет.
     * <p>
     * Использует кэш для поиска существующих топиков.
     * </p>
     *
     * @param chatId ID чата.
     * @param name Название топика.
     * @param color Цвет (используется только при создании нового).
     * @return Future с топиком.
     */
    CompletableFuture<ForumTopic> getOrCreateTopic(Long chatId, String name, ForumIconColor color);

    /**
     * Редактирует топик (название или иконку).
     *
     * @param chatId ID чата.
     * @param messageThreadId ID топика.
     * @param newName Новое название (или null, если не менять).
     * @param customEmojiId Новый ID кастомного эмодзи (или null).
     * @return Future с результатом (true/false).
     */
    CompletableFuture<Boolean> editTopic(Long chatId, Integer messageThreadId, String newName, String customEmojiId);

    /**
     * Закрывает топик.
     */
    CompletableFuture<Boolean> closeTopic(Long chatId, Integer messageThreadId);

    /**
     * Открывает ранее закрытый топик.
     */
    CompletableFuture<Boolean> reopenTopic(Long chatId, Integer messageThreadId);

    /**
     * Удаляет топик (вместе со всеми сообщениями).
     */
    CompletableFuture<Boolean> deleteTopic(Long chatId, Integer messageThreadId);

    /**
     * Открепляет все закрепленные сообщения в топике.
     */
    CompletableFuture<Boolean> unpinAllMessages(Long chatId, Integer messageThreadId);
}