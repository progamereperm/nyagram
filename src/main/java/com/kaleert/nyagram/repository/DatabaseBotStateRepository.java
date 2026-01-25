package com.kaleert.nyagram.repository;

import com.kaleert.nyagram.core.spi.BotStateRepository;
import jakarta.persistence.*;
import lombok.Data;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;

/**
 * Реализация репозитория состояния бота на основе JPA (Database).
 * <p>
 * Активируется свойством {@code nyagram.state-repository.type=database}.
 * Сохраняет {@code offset} обновлений в таблицу базы данных, что обеспечивает надежность
 * при перезапусках приложения.
 * </p>
 *
 * @since 1.0.0
 */
@Repository
@ConditionalOnProperty(name = "nyagram.state-repository.type", havingValue = "database")
public interface DatabaseBotStateRepository extends JpaRepository<DatabaseBotStateRepository.BotState, String>, BotStateRepository {

    @Override
    default long getLastUpdateId() {
        return findById("nyagram-main")
                .map(BotState::getLastUpdateId)
                .orElse(0L);
    }

    @Override
    @Transactional
    default void saveLastUpdateId(long updateId) {
        BotState state = findById("nyagram-main").orElseGet(() -> {
            BotState newState = new BotState();
            newState.setBotId("nyagram-main");
            return newState;
        });
        state.setLastUpdateId(updateId);
        state.setLastUpdated(LocalDateTime.now());
        save(state);
    }

    @Modifying
    @Transactional
    @Query("DELETE FROM BotState s WHERE s.lastUpdated < :cutoff AND s.botId != 'nyagram-main'")
    void deleteExpiredStates(LocalDateTime cutoff);
    
    /**
     * Сущность JPA для хранения состояния бота.
     */
    @Entity
    @Table(name = "bot_states")
    @Data
    class BotState {
        
        /** Уникальный идентификатор бота (для поддержки мульти-бота в одной БД). */
        @Id
        @Column(name = "bot_id", length = 100)
        private String botId;
        
        /** ID последнего обработанного обновления. */
        @Column(name = "last_update_id")
        private long lastUpdateId;
        
        
        @Column(name = "last_updated")
        private LocalDateTime lastUpdated;
    }
}