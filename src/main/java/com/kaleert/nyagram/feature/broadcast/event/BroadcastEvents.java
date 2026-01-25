package com.kaleert.nyagram.feature.broadcast.event;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

/**
 * Контейнер для событий системы рассылок.
 * <p>
 * Используются для уведомления приложения о ходе и результатах массовой отправки сообщений.
 * </p>
 *
 * @since 1.0.0
 */
public class BroadcastEvents {
    
    /**
     * Событие: Бот обнаружен заблокированным у пользователя.
     * <p>
     * Возникает при получении ошибки 403 (Forbidden: bot was blocked by the user) во время рассылки.
     * Полезно для очистки базы данных от неактивных пользователей.
     * </p>
     */
    @Getter
    @RequiredArgsConstructor
    public static class UserBlockedEvent {
        
        /** ID пользователя, заблокировавшего бота. */
        private final Long userId;
    }
    
    /**
     * Событие: Рассылка полностью завершена.
     */
    @Getter
    @RequiredArgsConstructor
    public static class BroadcastCompleteEvent {
        
        /** Общее количество пользователей в списке рассылки. */
        private final long total;
        
        /** Количество успешных отправок. */
        private final long successful;
        
        /** Количество ошибок. */
        private final long failed;
        
        /** Общее время выполнения в миллисекундах. */
        private final long durationMs;
        
    }
}