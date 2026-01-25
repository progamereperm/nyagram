package com.kaleert.nyagram.fsm.store;

import com.kaleert.nyagram.fsm.UserSession;
import com.kaleert.nyagram.fsm.spi.SessionStore;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;
import java.util.Map;
import java.util.Optional;
import java.util.concurrent.ConcurrentHashMap;

/**
 * Реализация хранилища сессий в оперативной памяти (ConcurrentHashMap).
 * <p>
 * Используется по умолчанию, если не настроен Redis или БД.
 * <b>Внимание:</b> Данные теряются при перезапуске приложения. Не подходит для продакшена,
 * если требуется сохранение состояния между редеплоями.
 * </p>
 *
 * @since 1.0.0
 */
@Component
public class InMemorySessionStore implements SessionStore {
    private final Map<Long, UserSession> storage = new ConcurrentHashMap<>();

    @Override
    public void save(UserSession session) {
        storage.put(session.getUserId(), session);
    }

    @Override
    public Optional<UserSession> get(Long userId) {
        return Optional.ofNullable(storage.get(userId));
    }

    @Override
    public void delete(Long userId) {
        storage.remove(userId);
    }

    @Override
    public void cleanupExpired(int timeoutMinutes) {
        LocalDateTime threshold = LocalDateTime.now().minusMinutes(timeoutMinutes);
        storage.entrySet().removeIf(entry -> 
            entry.getValue().getLastUpdatedAt().isBefore(threshold)
        );
    }
}