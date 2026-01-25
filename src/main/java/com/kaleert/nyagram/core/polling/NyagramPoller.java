package com.kaleert.nyagram.core.polling;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.kaleert.nyagram.api.dto.UpdateResponse;
import com.kaleert.nyagram.api.objects.Update;
import com.kaleert.nyagram.core.UpdateProcessor; // <-- Внедряем Processor
import com.kaleert.nyagram.core.spi.BotStateRepository;
import com.kaleert.nyagram.core.spi.NyagramBotConfig;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.ResourceAccessException;
import org.springframework.web.client.RestTemplate;

import java.util.Collections;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.RejectedExecutionException;
import java.util.concurrent.atomic.AtomicBoolean;

/**
 * Компонент, отвечающий за получение обновлений методом Long Polling.
 * <p>
 * Работает в бесконечном цикле в отдельном потоке. Выполняет запросы {@code getUpdates}
 * к API Telegram, получает массив обновлений и передает их в {@link com.kaleert.nyagram.core.UpdateProcessor}.
 * </p>
 * <p>
 * Управляет смещением (offset), чтобы не получать одни и те же сообщения дважды,
 * и обрабатывает сетевые ошибки (backoff).
 * </p>
 *
 * @see com.kaleert.nyagram.api.methods.updates.GetUpdates
 * @since 1.0.0
 */
@Slf4j
@RequiredArgsConstructor
public class NyagramPoller {

    private final NyagramBotConfig botConfig;
    private final BotStateRepository stateRepository;
    private final UpdateProcessor updateProcessor; // <-- Используем вместо Dispatcher и Executor
    private final RestTemplate restTemplate;
    private final ObjectMapper objectMapper;

    private final AtomicBoolean isRunning = new AtomicBoolean(false);
    private ExecutorService pollerExecutor;
    
    private static final String TELEGRAM_UPDATE_URL = "https://api.telegram.org/bot%s/getUpdates?timeout=%d&offset=%d&allowed_updates=%s";
    
    /**
     * Запускает процесс Long Polling в отдельном потоке.
     * <p>
     * Проверяет, не запущен ли поллер уже, и создает single-thread executor для цикла опроса.
     * </p>
     */
    public void start() {
        if (isRunning.getAndSet(true)) {
            log.warn("Nyagram Poller is already running.");
            return;
        }

        log.info("Starting Nyagram Poller for bot: @{}", botConfig.getBotUsername());
        
        pollerExecutor = Executors.newSingleThreadExecutor(r -> {
            Thread t = new Thread(r, "nyagram-poller");
            t.setDaemon(false);
            return t;
        });
        
        pollerExecutor.submit(this::pollLoop);
    }
    
    /**
     * Корректно останавливает процесс поллинга.
     * <p>
     * Устанавливает флаг остановки, дожидается завершения текущего цикла опроса
     * и закрывает пул потоков поллера.
     * </p>
     */
    public void stop() {
        if (!isRunning.getAndSet(false)) {
            return;
        }
        
        log.info("Stopping Nyagram Poller gracefully...");
        
        if (pollerExecutor != null) {
            pollerExecutor.shutdown();
            try {
                if (!pollerExecutor.awaitTermination(10, TimeUnit.SECONDS)) {
                    pollerExecutor.shutdownNow();
                }
            } catch (InterruptedException e) {
                pollerExecutor.shutdownNow();
                Thread.currentThread().interrupt();
            }
        }
        log.info("Nyagram Poller stopped successfully");
    }

    private void pollLoop() {
        long storedId = stateRepository.getLastUpdateId();
        long offset = storedId > 0 ? storedId + 1 : 0;

        int consecutiveNetworkErrors = 0;
        
        String allowedUpdatesJson = "[]";
        try {
            if (botConfig.getAllowedUpdates() != null && !botConfig.getAllowedUpdates().isEmpty()) {
                allowedUpdatesJson = objectMapper.writeValueAsString(botConfig.getAllowedUpdates());
            }
        } catch (Exception e) {
            log.error("Failed to serialize allowed_updates config", e);
        }

        log.info("Nyagram Poller started. Offset: {}", offset);

        while (isRunning.get() && !Thread.currentThread().isInterrupted()) {
            try {
                String url = String.format(TELEGRAM_UPDATE_URL,
                        botConfig.getBotToken(),
                        botConfig.getLongPollingTimeoutSeconds(),
                        offset,
                        allowedUpdatesJson
                );

                ResponseEntity<String> responseEntity = restTemplate.getForEntity(url, String.class);

                if (!responseEntity.getStatusCode().is2xxSuccessful() || responseEntity.getBody() == null) {
                    log.warn("Telegram API HTTP Error: {}", responseEntity.getStatusCode());
                    sleep(botConfig.getPollingRetryDelaySeconds());
                    continue;
                }

                UpdateResponse response = objectMapper.readValue(responseEntity.getBody(), UpdateResponse.class);

                if (Boolean.TRUE.equals(response.getOk())) {
                    consecutiveNetworkErrors = 0;
                    List<Update> updates = response.getResult() != null ? response.getResult() : Collections.emptyList();

                    if (!updates.isEmpty()) {
                        long maxProcessedId = processUpdatesWithBackpressure(updates);

                        if (maxProcessedId >= offset) {
                            offset = maxProcessedId + 1;
                            stateRepository.saveLastUpdateId(maxProcessedId);
                        }
                    }
                } else {
                    handleLogicalError(response);
                    sleep(botConfig.getPollingRetryDelaySeconds());
                }

            } catch (ResourceAccessException e) {
                consecutiveNetworkErrors++;
                int sleepTime = Math.min(consecutiveNetworkErrors * 2, botConfig.getPollingMaxBackoffSeconds());
                log.warn("Network error (Attempt {}). Retrying in {}s. Error: {}", consecutiveNetworkErrors, sleepTime, e.getMessage());
                sleep(sleepTime);
                
            } catch (HttpClientErrorException e) {
                handleHttpError(e);
                
            } catch (Exception e) {
                log.error("Unexpected critical error in Poller loop", e);
                sleep(botConfig.getPollingRetryDelaySeconds());
            }
        }
        
        log.info("Nyagram Poller loop terminated.");
    }

    private void handleHttpError(HttpClientErrorException e) {
        if (e.getStatusCode().value() == 409) {
            log.error("Conflict (409): Terminating. Check other instances.");
            stop();
        } else if (e.getStatusCode().value() == 401) {
            log.error("Unauthorized (401): Invalid Token. Stopping.");
            stop();
        } else {
            log.warn("HTTP Error: {}", e.getStatusCode());
            sleep(botConfig.getPollingRetryDelaySeconds());
        }
    }
    
    private void handleLogicalError(UpdateResponse response) {
        log.error("API Error: {} (Code: {})", response.getDescription(), response.getErrorCode());
        if (Integer.valueOf(409).equals(response.getErrorCode())) {
            stop();
        }
    }

    private void sleep(int seconds) {
        try {
            TimeUnit.SECONDS.sleep(seconds);
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
            isRunning.set(false);
        }
    }
    
    private long processUpdatesWithBackpressure(List<Update> updates) {
        long lastSuccessId = -1;

        for (Update update : updates) {
            if (!isRunning.get()) return lastSuccessId;

            boolean submitted = false;

            while (!submitted && isRunning.get()) {
                try {
                    updateProcessor.processAsync(update);
                    submitted = true;
                    lastSuccessId = update.getUpdateId();

                } catch (RejectedExecutionException e) {
                    log.warn("Worker Pool Saturated! Pausing poller for 100ms to allow workers to catch up...");
                    sleep(100);
                    
                } catch (Exception e) {
                    log.error("Critical error submitting UpdateID {}. Skipping update.", update.getUpdateId(), e);
                    submitted = true;
                    lastSuccessId = update.getUpdateId();
                }
            }
        }
        
        return lastSuccessId;
    }
}