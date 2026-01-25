package com.kaleert.nyagram.client.retry;

/**
 * Стратегия расчета задержки перед повторной попыткой запроса.
 * <p>
 * Используется в {@link com.kaleert.nyagram.client.NyagramClient} для обработки
 * сетевых ошибок (5xx) или ограничений скорости (429).
 * </p>
 *
 * @since 1.0.0
 */
public interface BackoffStrategy {
    
    /**
     * Рассчитывает время ожидания в миллисекундах.
     *
     * @param attempt Номер текущей попытки (начиная с 1).
     * @return время задержки в мс.
     */
    long nextBackoff(int attempt);
}