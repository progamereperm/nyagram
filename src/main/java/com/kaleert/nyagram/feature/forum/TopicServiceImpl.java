package com.kaleert.nyagram.feature.forum;

import com.kaleert.nyagram.api.methods.forum.*;
import com.kaleert.nyagram.api.objects.forum.ForumIconColor;
import com.kaleert.nyagram.api.objects.forum.ForumTopic;
import com.kaleert.nyagram.client.NyagramClient;
import com.kaleert.nyagram.feature.forum.spi.TopicIdCache;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.concurrent.CompletableFuture;

/**
 * Стандартная реализация {@link TopicService}.
 * <p>
 * Использует {@link com.kaleert.nyagram.client.NyagramClient} для выполнения запросов
 * и {@link com.kaleert.nyagram.feature.forum.spi.TopicIdCache} для кэширования ID топиков.
 * </p>
 *
 * @since 1.0.0
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class TopicServiceImpl implements TopicService {

    private final NyagramClient client;
    private final TopicIdCache topicCache;

    @Override
    public CompletableFuture<ForumTopic> createTopic(Long chatId, String name, ForumIconColor color) {
        return client.executeAsync(CreateForumTopic.builder()
                .chatId(chatId.toString())
                .name(name)
                .iconColor(color != null ? color.getValue() : null)
                .build())
                .thenApply(topic -> {
                    topicCache.saveTopicId(chatId, name, topic.getMessageThreadId());
                    return topic;
                });
    }

    @Override
    public CompletableFuture<ForumTopic> getOrCreateTopic(Long chatId, String name, ForumIconColor color) {
        return CompletableFuture.supplyAsync(() -> topicCache.getTopicId(chatId, name))
            .thenCompose(cachedIdOpt -> {
                if (cachedIdOpt.isPresent()) {
                    ForumTopic existingTopic = new ForumTopic(
                        cachedIdOpt.get(),
                        name,
                        color != null ? color.getValue() : 0,
                        null
                    );
                    return CompletableFuture.completedFuture(existingTopic);
                }
                return createTopic(chatId, name, color);
            });
    }

    @Override
    public CompletableFuture<Boolean> editTopic(Long chatId, Integer messageThreadId, String newName, String customEmojiId) {
        EditForumTopic.EditForumTopicBuilder builder = EditForumTopic.builder()
                .chatId(chatId.toString())
                .messageThreadId(messageThreadId);

        if (newName != null) builder.name(newName);
        if (customEmojiId != null) builder.iconCustomEmojiId(customEmojiId);

        return client.executeAsync(builder.build());
    }

    @Override
    public CompletableFuture<Boolean> closeTopic(Long chatId, Integer messageThreadId) {
        return client.executeAsync(CloseForumTopic.builder()
                .chatId(chatId.toString())
                .messageThreadId(messageThreadId)
                .build());
    }

    @Override
    public CompletableFuture<Boolean> reopenTopic(Long chatId, Integer messageThreadId) {
        return client.executeAsync(ReopenForumTopic.builder()
                .chatId(chatId.toString())
                .messageThreadId(messageThreadId)
                .build());
    }

    @Override
    public CompletableFuture<Boolean> deleteTopic(Long chatId, Integer messageThreadId) {
        return client.executeAsync(DeleteForumTopic.builder()
                .chatId(chatId.toString())
                .messageThreadId(messageThreadId)
                .build())
                .thenApply(result -> {
                    if (Boolean.TRUE.equals(result)) {
                        topicCache.evictById(chatId, messageThreadId);
                    }
                    return result;
                });
    }

    @Override
    public CompletableFuture<Boolean> unpinAllMessages(Long chatId, Integer messageThreadId) {
        return client.executeAsync(UnpinAllForumTopicMessages.builder()
                .chatId(chatId.toString())
                .messageThreadId(messageThreadId)
                .build());
    }
}