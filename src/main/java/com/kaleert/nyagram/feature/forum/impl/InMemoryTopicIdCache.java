package com.kaleert.nyagram.feature.forum.impl;

import com.kaleert.nyagram.feature.forum.spi.TopicIdCache;
import java.util.Map;
import java.util.Optional;
import java.util.concurrent.ConcurrentHashMap;

/**
 * Реализация кэша ID топиков в оперативной памяти (Map).
 * <p>
 * <b>Внимание:</b> Данные теряются при перезапуске приложения.
 * Для продакшена рекомендуется реализовать кэш на базе БД или Redis.
 * </p>
 *
 * @since 1.0.0
 */
public class InMemoryTopicIdCache implements TopicIdCache {

    private final Map<String, Integer> nameToIdMap = new ConcurrentHashMap<>();
    private final Map<String, String> idToNameMap = new ConcurrentHashMap<>();

    @Override
    public void saveTopicId(Long chatId, String topicName, Integer messageThreadId) {
        String nameKey = getNameKey(chatId, topicName);
        String idKey = getIdKey(chatId, messageThreadId);

        nameToIdMap.put(nameKey, messageThreadId);
        idToNameMap.put(idKey, topicName);
    }

    @Override
    public Optional<Integer> getTopicId(Long chatId, String topicName) {
        return Optional.ofNullable(nameToIdMap.get(getNameKey(chatId, topicName)));
    }

    @Override
    public void evictByName(Long chatId, String topicName) {
        String nameKey = getNameKey(chatId, topicName);
        Integer id = nameToIdMap.remove(nameKey);
        
        if (id != null) {
            idToNameMap.remove(getIdKey(chatId, id));
        }
    }

    @Override
    public void evictById(Long chatId, Integer messageThreadId) {
        String idKey = getIdKey(chatId, messageThreadId);
        String topicName = idToNameMap.remove(idKey);

        if (topicName != null) {
            nameToIdMap.remove(getNameKey(chatId, topicName));
        }
    }

    private String getNameKey(Long chatId, String topicName) {
        return chatId + ":" + topicName.trim();
    }

    private String getIdKey(Long chatId, Integer messageThreadId) {
        return chatId + ":" + messageThreadId;
    }
}