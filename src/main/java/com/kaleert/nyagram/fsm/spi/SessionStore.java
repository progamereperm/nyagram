package com.kaleert.nyagram.fsm.spi;

import com.kaleert.nyagram.fsm.UserSession;
import java.util.Optional;

/**
 * Интерфейс хранилища сессий (Persistence Layer).
 * <p>
 * Определяет контракт для сохранения, загрузки и удаления сессий пользователей.
 * Реализации могут хранить данные в памяти ({@link com.kaleert.nyagram.fsm.store.InMemorySessionStore}),
 * в Redis ({@link com.kaleert.nyagram.fsm.store.RedisSessionStore}) или в SQL базе данных.
 * </p>
 *
 * @since 1.0.0
 */
public interface SessionStore {
    void save(UserSession session);
    Optional<UserSession> get(Long userId);
    void delete(Long userId);
    
    /**
     * Удаляет устаревшие сессии.
     * @param timeoutMinutes Время жизни сессии в минутах.
     */
    void cleanupExpired(int timeoutMinutes);
}