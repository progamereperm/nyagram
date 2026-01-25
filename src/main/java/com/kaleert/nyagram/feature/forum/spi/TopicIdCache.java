package com.kaleert.nyagram.feature.forum.spi;

import java.util.Optional;

/**
 * Интерфейс кэша для хранения соответствия "Имя топика" <-> "ID топика".
 * <p>
 * Позволяет избегать создания дубликатов топиков с одинаковыми именами
 * при использовании {@code getOrCreateTopic}.
 * </p>
 *
 * @since 1.0.0
 */
public interface TopicIdCache {
    
    /** Сохраняет ID топика. */
    void saveTopicId(Long chatId, String topicName, Integer messageThreadId);
    
    /** Получает ID топика по имени. */
    Optional<Integer> getTopicId(Long chatId, String topicName);
    
    /** Удаляет запись из кэша по имени. */
    void evictByName(Long chatId, String topicName);
    
    /** Удаляет запись из кэша по ID. */
    void evictById(Long chatId, Integer messageThreadId);
}