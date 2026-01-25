package com.kaleert.nyagram.repository;

import com.kaleert.nyagram.core.spi.BotStateRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Repository;

import java.time.Duration;
import java.util.concurrent.TimeUnit;

/**
 * Реализация репозитория состояния бота в оперативной памяти.
 * <p>
 * Используется по умолчанию для разработки и тестирования.
 * <b>Внимание:</b> При перезапуске приложения сохраненный {@code offset} теряется,
 * что может привести к повторной обработке старых обновлений или пропуску новых
 * (в зависимости от поведения серверов Telegram).
 * </p>
 *
 * @since 1.0.0
 */
@Slf4j
@Repository
@RequiredArgsConstructor
@ConditionalOnProperty(name = "nyagram.state-repository.type", havingValue = "redis")
public class RedisBotStateRepository implements BotStateRepository {

    private final RedisTemplate<String, String> redisTemplate;
    
    private static final String KEY_PREFIX = "nyagram:state:";
    private static final String UPDATE_ID_KEY = KEY_PREFIX + "last_update_id";
    private static final Duration TTL = Duration.ofDays(30);

    @Override
    public long getLastUpdateId() {
        try {
            String value = redisTemplate.opsForValue().get(UPDATE_ID_KEY);
            if (value != null) {
                return Long.parseLong(value);
            }
            return 0L;
        } catch (Exception e) {
            log.error("Failed to get last update ID from Redis", e);
            return 0L;
        }
    }

    @Override
    public void saveLastUpdateId(long updateId) {
        try {
            redisTemplate.opsForValue().set(
                UPDATE_ID_KEY, 
                String.valueOf(updateId), 
                TTL.toSeconds(), 
                TimeUnit.SECONDS
            );
            
            // Периодическое логирование
            if (updateId % 1000 == 0) {
                log.debug("Saved updateId {} to Redis (TTL: {} days)", updateId, TTL.toDays());
            }
        } catch (Exception e) {
            log.error("Failed to save update ID to Redis", e);
            throw e;
        }
    }
    
    /**
     * Пытается захватить распределенную блокировку в Redis.
     * <p>
     * Использует команду {@code SETNX}. Блокировка автоматически истечет через указанное время.
     * </p>
     *
     * @param lockKey Уникальный ключ блокировки.
     * @param timeout Время жизни блокировки (TTL).
     * @return true, если блокировка успешно захвачена.
     */
    public boolean acquireLock(String lockKey, Duration timeout) {
        String lockValue = String.valueOf(System.currentTimeMillis());
        
        Boolean acquired = redisTemplate.opsForValue().setIfAbsent(
            KEY_PREFIX + "lock:" + lockKey,
            lockValue,
            timeout.toSeconds(),
            TimeUnit.SECONDS
        );
        
        return Boolean.TRUE.equals(acquired);
    }
    
    /**
     * Освобождает распределенную блокировку.
     * <p>
     * Удаляет ключ блокировки из Redis, позволяя другим процессам захватить её.
     * </p>
     *
     * @param lockKey Ключ блокировки.
     */
    public void releaseLock(String lockKey) {
        redisTemplate.delete(KEY_PREFIX + "lock:" + lockKey);
    }
    
    /**
     * Проверяет, является ли текущий инстанс бота "мастером" (лидером).
     * <p>
     * Используется в кластерной среде, чтобы гарантировать, что периодические задачи
     * (например, очистка сессий) выполняются только одним экземпляром приложения.
     * </p>
     *
     * @return true, если текущий инстанс захватил лидерство.
     */
    public boolean isMasterInstance() {
        String lockKey = "master_instance";
        return acquireLock(lockKey, Duration.ofSeconds(30));
    }
}