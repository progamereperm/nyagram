package com.kaleert.nyagram.fsm.annotation;

import java.lang.annotation.*;

/**
 * Помечает метод как обработчик определенного состояния FSM.
 * <p>
 * Когда пользователь находится в заданном состоянии, входящие текстовые сообщения
 * (не являющиеся командами) будут направлены в этот метод.
 * </p>
 *
 * @example
 * <pre>
 * {@code @StateAction("WAITING_FOR_NAME")}
 * public void onNameInput(CommandContext ctx, UserSession session) {
 *     // Обработка ввода имени
 * }
 * </pre>
 * 
 * @see com.kaleert.nyagram.fsm.SessionManager
 * @since 1.0.0
 */
@Target(ElementType.METHOD)
@Retention(RetentionPolicy.RUNTIME)
@Documented
public @interface StateAction {
    
    /**
     * Идентификатор состояния, которое обрабатывает этот метод.
     *
     * @return строковое название состояния.
     */
    String value();
    
    /**
     * Нужно ли автоматически очищать сессию после успешного выполнения этого метода.
     * Полезно для финальных шагов диалога.
     *
     * @return true, если сессию нужно удалить.
     */
    boolean clearAfter() default false;
}