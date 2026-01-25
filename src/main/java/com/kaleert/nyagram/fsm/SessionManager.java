package com.kaleert.nyagram.fsm;

import com.kaleert.nyagram.fsm.spi.SessionStore;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import java.util.Optional;

/**
 * Сервис управления пользовательскими сессиями (FSM).
 * <p>
 * Предоставляет высокоуровневый API для:
 * <ul>
 *     <li>Начала новой сессии (перехода в начальное состояние).</li>
 *     <li>Получения текущей сессии пользователя.</li>
 *     <li>Обновления состояния (перехода на следующий шаг).</li>
 *     <li>Очистки сессии (завершения диалога).</li>
 * </ul>
 * Делегирует хранение данных реализации {@link SessionStore}.
 * </p>
 *
 * @since 1.0.0
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class SessionManager {

    private final SessionStore sessionStore;

    @Value("${nyagram.fsm.ttl-minutes:30}")
    private int sessionTtlMinutes;
    
    /**
     * Получает активную сессию пользователя.
     *
     * @param userId ID пользователя.
     * @return Объект сессии или {@code null}, если сессия не найдена.
     */
    public UserSession getSession(Long userId) {
        return sessionStore.get(userId).orElse(null);
    }
    
    /**
     * Начинает новую сессию диалога.
     * <p>
     * Создает новую запись в хранилище, перезаписывая старую, если она была.
     * </p>
     *
     * @param userId ID пользователя.
     * @param chatId ID чата, где началась сессия.
     * @param initialState Начальное состояние (State).
     * @return Созданный объект сессии.
     */
    public UserSession startSession(Long userId, Long chatId, String initialState) {
        UserSession session = new UserSession(userId, chatId);
        session.setState(initialState);
        sessionStore.save(session);
        log.debug("Started FSM session for user {} in state {}", userId, initialState);
        return session;
    }
    
    /**
     * Переводит пользователя в новое состояние.
     * <p>
     * Обновляет поле {@code state} в текущей сессии и сбрасывает таймер неактивности.
     * Если сессии нет, ничего не делает.
     * </p>
     *
     * @param userId ID пользователя.
     * @param newState Новое состояние.
     */
    public void updateState(Long userId, String newState) {
        Optional<UserSession> sessionOpt = sessionStore.get(userId);
        if (sessionOpt.isPresent()) {
            UserSession session = sessionOpt.get();
            session.setState(newState);
            session.setLastUpdatedAt(java.time.LocalDateTime.now());
            sessionStore.save(session);
        }
    }
    
    /**
     * Принудительно завершает сессию пользователя.
     * <p>
     * Удаляет все данные сессии из хранилища. Пользователь переходит в состояние "без сессии".
     * </p>
     *
     * @param userId ID пользователя.
     */
    public void clearSession(Long userId) {
        sessionStore.delete(userId);
        log.debug("Cleared FSM session for user {}", userId);
    }
    
    /**
     * Периодическая задача очистки устаревших сессий.
     * <p>
     * Запускается по расписанию (настраивается через {@code nyagram.fsm.cleanup-interval}).
     * Удаляет сессии, которые не обновлялись дольше времени жизни (TTL).
     * </p>
     */
    @Scheduled(fixedRateString = "${nyagram.fsm.cleanup-interval:60000}")
    public void cleanup() {
        sessionStore.cleanupExpired(sessionTtlMinutes);
    }
}