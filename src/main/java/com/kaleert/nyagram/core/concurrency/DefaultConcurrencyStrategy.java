package com.kaleert.nyagram.core.concurrency;

import com.kaleert.nyagram.api.objects.Update;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

/**
 * Стратегия конкурентности по умолчанию.
 * <p>
 * Использует {@link NyagramExecutor} для обеспечения последовательной обработки
 * сообщений от одного и того же источника (User ID или Chat ID).
 * Это критично для корректной работы Машины Состояний (FSM), чтобы сообщения не обгоняли друг друга.
 * </p>
 *
 * @since 1.0.0
 */
@Slf4j
@RequiredArgsConstructor 
public class DefaultConcurrencyStrategy implements BotConcurrencyStrategy {

    private final NyagramExecutor executor;

    @Override
    public void execute(Update update, Runnable task) {
        Long shardKey = extractShardKey(update);
        executor.execute(shardKey, task);
    }

    private Long extractShardKey(Update update) {
        if (update.getMessage() != null) return update.getMessage().getFrom().getId();
        if (update.getCallbackQuery() != null) return update.getCallbackQuery().getFrom().getId();
        if (update.getEditedMessage() != null) return update.getEditedMessage().getFrom().getId();
        if (update.getMyChatMember() != null) return update.getMyChatMember().getFrom().getId();
        if (update.getChatMember() != null) return update.getChatMember().getFrom().getId();
        if (update.getMessageReaction() != null) return update.getMessageReaction().getUser().getId();
        if (update.getInlineQuery() != null) return update.getInlineQuery().getFrom().getId();
        
        if (update.getChannelPost() != null) return update.getChannelPost().getChat().getId();
        if (update.getEditedChannelPost() != null) return update.getEditedChannelPost().getChat().getId();
        
        if (update.getPreCheckoutQuery() != null) return update.getPreCheckoutQuery().getFrom().getId();
        if (update.getShippingQuery() != null) return update.getShippingQuery().getFrom().getId();
        
        return null;
    }
}