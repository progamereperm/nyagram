package com.kaleert.nyagram.fsm.store;

import com.kaleert.nyagram.fsm.UserSession;
import com.kaleert.nyagram.fsm.spi.SessionStore;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Repository;

import java.util.Optional;
import java.util.concurrent.TimeUnit;

/**
 * Реализация хранилища сессий на базе Redis.
 * <p>
 * Активируется при наличии настройки {@code nyagram.state-repository.type=redis}.
 * Обеспечивает персистентность сессий и возможность масштабирования бота (несколько инстансов).
 * Использует TTL механизм Redis для автоматической очистки старых сессий.
 * </p>
 *
 * @since 1.0.0
 */
@Slf4j
@Repository
@RequiredArgsConstructor
@ConditionalOnProperty(name = "nyagram.state-repository.type", havingValue = "redis")
public class RedisSessionStore implements SessionStore {

    private final RedisTemplate<String, UserSession> redisTemplate;

    @Value("${nyagram.fsm.ttl-minutes:30}")
    private int sessionTtlMinutes;

    private static final String KEY_PREFIX = "nyagram:fsm:user:";

    @Override
    public void save(UserSession session) {
        if (session == null || session.getUserId() == null) return;
        
        String key = buildKey(session.getUserId());
        try {
            redisTemplate.opsForValue().set(key, session, sessionTtlMinutes, TimeUnit.MINUTES);
        } catch (Exception e) {
            log.error("Failed to save FSM session for user {}", session.getUserId(), e);
        }
    }

    @Override
    public Optional<UserSession> get(Long userId) {
        String key = buildKey(userId);
        try {
            UserSession session = redisTemplate.opsForValue().get(key);
            if (session != null) {
                redisTemplate.expire(key, sessionTtlMinutes, TimeUnit.MINUTES);
            }
            return Optional.ofNullable(session);
        } catch (Exception e) {
            log.error("Failed to retrieve FSM session for user {}", userId, e);
            return Optional.empty();
        }
    }

    @Override
    public void delete(Long userId) {
        String key = buildKey(userId);
        redisTemplate.delete(key);
    }

    @Override
    public void cleanupExpired(int timeoutMinutes) {
    }

    private String buildKey(Long userId) {
        return KEY_PREFIX + userId;
    }
}