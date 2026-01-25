package com.kaleert.nyagram.core.spi;

import com.kaleert.nyagram.api.objects.Update;

/**
 * Интерфейс перехватчика обновлений (Interceptor).
 * <p>
 * Позволяет выполнять действия до (preHandle) и после (postHandle) обработки обновления
 * любой бизнес-логикой. Подходит для логирования, сбора метрик, глобальных проверок
 * (например, бан-листов) или управления транзакциями.
 * </p>
 *
 * @since 1.0.0
 */
public interface UpdateInterceptor {

    /**
     * Вызывается ПЕРЕД началом обработки обновления.
     *
     * @param update Входящее обновление.
     * @return {@code true}, чтобы продолжить обработку.
     *         {@code false}, чтобы прервать цепочку (обновление будет отброшено).
     */
    boolean preHandle(Update update);

    /**
     * Вызывается ПОСЛЕ завершения обработки обновления (даже если возникло исключение).
     *
     * @param update Входящее обновление.
     * @param handled {@code true}, если обновление было успешно обработано каким-либо хендлером.
     * @param ex Исключение, если оно возникло в процессе обработки (иначе {@code null}).
     */
    default void postHandle(Update update, boolean handled, Throwable ex) {}
}