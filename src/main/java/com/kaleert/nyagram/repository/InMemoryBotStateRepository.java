package com.kaleert.nyagram.repository;

import com.kaleert.nyagram.core.spi.BotStateRepository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.stereotype.Component;

import java.util.concurrent.atomic.AtomicLong;

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
@Component
@ConditionalOnProperty(name = "nyagram.state-repository.type", havingValue = "memory", matchIfMissing = true)
public class InMemoryBotStateRepository implements BotStateRepository {

    private final AtomicLong lastUpdateId = new AtomicLong(0);

    @Override
    public long getLastUpdateId() {
        return lastUpdateId.get();
    }

    @Override
    public void saveLastUpdateId(long updateId) {
        lastUpdateId.set(updateId);
        // log.debug("Update ID saved in memory: {}", updateId);
    }
}