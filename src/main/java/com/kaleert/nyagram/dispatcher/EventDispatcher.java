package com.kaleert.nyagram.dispatcher;

import com.kaleert.nyagram.api.objects.Update;
import com.kaleert.nyagram.command.CommandContext;
import com.kaleert.nyagram.core.ArgumentResolver;
import com.kaleert.nyagram.core.registry.EventRegistry;
import com.kaleert.nyagram.client.NyagramClient;
import com.kaleert.nyagram.event.EventType;
import com.kaleert.nyagram.meta.EventMeta;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.lang.reflect.Parameter;
import java.util.List;

/**
 * Диспетчер событий (не команд).
 * <p>
 * Маршрутизирует обновления, которые не являются текстовыми командами (например,
 * нажатия кнопок, добавление бота в чат, платежи, опросы), в методы,
 * помеченные аннотацией {@link com.kaleert.nyagram.event.NyagramEventHandler}.
 * </p>
 *
 * @since 1.0.0
 */
@Slf4j
@Component
@RequiredArgsConstructor
public class EventDispatcher {

    private final EventRegistry eventRegistry;
    private final List<ArgumentResolver<?>> argumentResolvers;
    private final NyagramClient nyagramClient;
    
    /**
     * Обрабатывает входящее обновление как событие.
     * <p>
     * Определяет тип события ({@link com.kaleert.nyagram.event.EventType}), находит
     * список подписчиков в реестре и вызывает их последовательно.
     * </p>
     *
     * @param update Входящее обновление.
     */
    public void dispatch(Update update) {
        EventType type = determineEventType(update);
        if (type == null) return;

        List<EventMeta> handlers = eventRegistry.getHandlers(type);
        if (handlers.isEmpty()) return;

        CommandContext context = new CommandContext(update, nyagramClient);

        for (EventMeta meta : handlers) {
            try {
                Object[] args = resolveArguments(meta, context);

                if (meta.getMethodHandle() != null) {
                    meta.getMethodHandle().invokeWithArguments(args);
                } else {
                    meta.getMethod().invoke(meta.getBean(), args);
                }
                
            } catch (Throwable e) {
                log.error("Error executing event handler for {}", type, e);
            }
        }
    }

    private Object[] resolveArguments(EventMeta meta, CommandContext context) {
        Parameter[] parameters = meta.getMethod().getParameters();
        Object[] args = new Object[parameters.length];

        for (int i = 0; i < parameters.length; i++) {
            Parameter param = parameters[i];
            
            ArgumentResolver<?> resolver = argumentResolvers.stream()
                    .filter(r -> r.supports(param))
                    .findFirst()
                    .orElse(null);

            if (resolver != null) {
                try {
                    args[i] = resolver.resolve(context, param, null);
                } catch (Exception e) {
                    log.warn("Failed to resolve argument {} for event handler", param.getName(), e);
                    args[i] = null;
                }
            } else if (param.getType().isAssignableFrom(Update.class)) {
                args[i] = context.getUpdate();
            } else if (param.getType().isAssignableFrom(CommandContext.class)) {
                args[i] = context;
            } else {
                args[i] = null;
            }
        }
        
        return args;
    }

    private EventType determineEventType(Update update) {
        if (update.getMessage() != null) return EventType.MESSAGE;
        if (update.getCallbackQuery() != null) return EventType.CALLBACK_QUERY;
        if (update.getMyChatMember() != null) return EventType.MY_CHAT_MEMBER;
        if (update.getChatMember() != null) return EventType.CHAT_MEMBER;
        if (update.getEditedMessage() != null) return EventType.EDITED_MESSAGE;
        if (update.getMessageReaction() != null) return EventType.MESSAGE_REACTION;
        if (update.getInlineQuery() != null) return EventType.INLINE_QUERY;
        if (update.getChosenInlineResult() != null) return EventType.CHOSEN_INLINE_RESULT;
        if (update.getChannelPost() != null) return EventType.CHANNEL_POST;
        if (update.getEditedChannelPost() != null) return EventType.EDITED_CHANNEL_POST;
        if (update.getShippingQuery() != null) return EventType.SHIPPING_QUERY;
        if (update.getPreCheckoutQuery() != null) return EventType.PRE_CHECKOUT_QUERY;
        if (update.getPoll() != null) return EventType.POLL;
        if (update.getPollAnswer() != null) return EventType.POLL_ANSWER;
        if (update.getChatJoinRequest() != null) return EventType.CHAT_JOIN_REQUEST;
        return null;
    }
}