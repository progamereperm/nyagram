package com.kaleert.nyagram.middleware;

import com.kaleert.nyagram.command.CommandContext;
import com.kaleert.nyagram.fsm.FsmArgumentResolver;
import com.kaleert.nyagram.fsm.FsmRegistry;
import com.kaleert.nyagram.fsm.SessionManager;
import com.kaleert.nyagram.fsm.UserSession;
import com.kaleert.nyagram.fsm.annotation.StateAction;
import com.kaleert.nyagram.meta.CommandMeta;
import com.kaleert.nyagram.meta.EventMeta;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;

import java.util.concurrent.CompletableFuture;

/**
 * Middleware для поддержки Машины Состояний (FSM).
 * <p>
 * Проверяет, находится ли пользователь в активной сессии диалога.
 * Если да, перехватывает текстовое сообщение и маршрутизирует его в соответствующий
 * {@code @StateAction} метод, минуя стандартный диспетчер команд.
 * Также обрабатывает глобальные команды отмены (/cancel, /stop).
 * </p>
 *
 * @since 1.0.0
 */
@Slf4j
@Component
@Order(-100)
@RequiredArgsConstructor
public class FsmMiddleware implements Middleware {

    private final SessionManager sessionManager;
    private final FsmRegistry fsmRegistry;
    private final FsmArgumentResolver argumentResolver; 

    @Override
    public CompletableFuture<MiddlewareResult> handle(CommandContext context, CommandMeta meta, MiddlewareChain next) {
        Long userId = context.getUserId();
        UserSession session = sessionManager.getSession(userId);

        if (session == null || session.getState() == null) {
            return next.proceed();
        }

        if (context.getText() != null && (context.getText().equals("/cancel") || context.getText().equals("/stop"))) {
            sessionManager.clearSession(userId);
            context.reply("🚫 Действие отменено.");
            return CompletableFuture.completedFuture(MiddlewareResult.stopResult(null));
        }

        EventMeta handler = fsmRegistry.getHandler(session.getState());
        
        if (handler != null) {
            try {
                Object[] args = argumentResolver.resolve(handler.getMethod(), context, session);
                
                if (handler.getMethodHandle() != null) {
                    handler.getMethodHandle().invokeWithArguments(args);
                } else {
                    handler.getMethod().invoke(handler.getBean(), args);
                }
                
                StateAction ann = handler.getMethod().getAnnotation(StateAction.class);
                if (ann != null && ann.clearAfter()) {
                    sessionManager.clearSession(userId);
                }
                
                return CompletableFuture.completedFuture(MiddlewareResult.stopResult(null));
                
            } catch (Throwable e) {
                log.error("Error executing FSM handler for state {}", session.getState(), e);
                return CompletableFuture.completedFuture(MiddlewareResult.errorResult(new RuntimeException(e)));
            }
        }

        return next.proceed();
    }
}