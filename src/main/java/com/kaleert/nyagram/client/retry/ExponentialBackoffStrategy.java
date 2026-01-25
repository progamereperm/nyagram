package com.kaleert.nyagram.client.retry;

import org.springframework.stereotype.Component;
import java.util.concurrent.ThreadLocalRandom;

/**
 * Реализация стратегии экспоненциальной задержки (Exponential Backoff) с джиттером.
 * <p>
 * Время ожидания увеличивается с каждой попыткой (например: 0.5с -> 0.75с -> 1.1с),
 * чтобы снизить нагрузку на сервер при сбоях. Случайный джиттер добавляется для предотвращения
 * проблемы "thundering herd" (синхронных повторов).
 * </p>
 *
 * @since 1.0.0
 */
@Component
public class ExponentialBackoffStrategy implements BackoffStrategy {

    private static final long INITIAL_INTERVAL_MS = 500;
    private static final long MAX_INTERVAL_MS = 30000;
    private static final double MULTIPLIER = 1.5;
    private static final double JITTER = 0.2;

    @Override
    public long nextBackoff(int attempt) {
        if (attempt <= 0) return INITIAL_INTERVAL_MS;

        double nextInterval = INITIAL_INTERVAL_MS * Math.pow(MULTIPLIER, attempt - 1);
        
        double jitterRange = nextInterval * JITTER;
        double jitter = ThreadLocalRandom.current().nextDouble(-jitterRange, jitterRange);
        
        long finalInterval = (long) (nextInterval + jitter);
        
        return Math.min(finalInterval, MAX_INTERVAL_MS);
    }
}