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

import java.net.URI;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
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
    
    private static final String BASE_URL_FORMAT = "https://api.telegram.org/bot%s/getUpdates?timeout=%d&offset=%d";
    
    /** Аварийная задержка, если конфиг сломан **/
    private static final int EMERGENCY_FALLBACK_DELAY = 3;
    
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
        
        String token = botConfig.getBotToken();
        if (token == null || token.isBlank()) {
            log.error("❌ FATAL: Bot token is EMPTY or NULL! Check your configuration");
            return;
        }
        if (token.contains(" ")) {
            log.warn("⚠️ Warning: Token contains spaces! Attempting to trim...");
        }
        
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
    
    /**
     * Основной цикл опроса сервера.
     */
    private void pollLoop() {
        long offset = stateRepository.getLastUpdateId() + 1;
        String allowedUpdatesParam = buildAllowedUpdatesParam();
        
        String rawToken = botConfig.getBotToken();
        String safeToken = (rawToken != null) ? rawToken.trim() : "";

        log.info("Nyagram Poller started. Offset: {}", offset);

        while (isRunning.get() && !Thread.currentThread().isInterrupted()) {
            try {
                String urlStr = String.format(BASE_URL_FORMAT,
                        safeToken,
                        50, 
                        offset
                ) + allowedUpdatesParam;

                URI uri = URI.create(urlStr);

                ResponseEntity<String> response = restTemplate.getForEntity(uri, String.class);

                if (!response.getStatusCode().is2xxSuccessful()) {
                    log.warn("Telegram API Error: {}", response.getStatusCode());
                    ensureSafeDelay();
                    continue;
                }

                UpdateResponse result = objectMapper.readValue(response.getBody(), UpdateResponse.class);

                if (Boolean.TRUE.equals(result.getOk())) {
                    List<Update> updates = result.getResult() != null ? result.getResult() : Collections.emptyList();
                    if (!updates.isEmpty()) {
                        for (Update u : updates) {
                            try {
                                updateProcessor.processAsync(u);
                                offset = u.getUpdateId() + 1;
                                stateRepository.saveLastUpdateId(u.getUpdateId());
                            } catch (Exception e) {
                                log.error("Error processing update {}", u.getUpdateId(), e);
                            }
                        }
                    }
                } else {
                    log.error("API Logic Error: {} (Code: {})", result.getDescription(), result.getErrorCode());
                    if (Integer.valueOf(409).equals(result.getErrorCode()) || Integer.valueOf(401).equals(result.getErrorCode())) {
                        stop();
                    } else {
                        ensureSafeDelay();
                    }
                }

            } catch (ResourceAccessException e) {
                log.warn("Network timeout. Retrying...");
            } catch (HttpClientErrorException e) {
                if (e.getStatusCode().value() == 404) {
                    log.error("❌ HTTP 404 Not Found. Check your token!");
                    log.error("👉 Configured Token: '{}'", safeToken); 
                    log.error("👉 Token Length: {}", safeToken.length());
                    stop(); 
                } else if (e.getStatusCode().value() == 409 || e.getStatusCode().value() == 401) {
                    log.error("⛔ Fatal Error: {}. Stopping.", e.getStatusCode());
                    stop();
                } else {
                    log.warn("HTTP Error: {}", e.getStatusCode());
                    ensureSafeDelay();
                }
            } catch (Exception e) {
                log.error("Critical Poller Error", e);
                ensureSafeDelay();
            }
        }
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
            ensureSafeDelay(); 
        }
    }
    
    private void handleLogicalError(UpdateResponse response) {
        log.error("API Error: {} (Code: {})", response.getDescription(), response.getErrorCode());
        if (Integer.valueOf(409).equals(response.getErrorCode())) {
            stop();
        }
    }

    /**
     * Выполняет задержку потока перед повторной попыткой запроса.
     * <p>
     * Берет значение из {@link NyagramBotConfig#getPollingRetryDelaySeconds()}.
     * Если конфиг возвращает 0 или меньше (ошибка конфигурации), используется
     * безопасное значение {@link #EMERGENCY_FALLBACK_DELAY}, чтобы избежать блокировки со стороны Telegram.
     * </p>
     */
    private void ensureSafeDelay() {
        int delay = botConfig.getPollingRetryDelaySeconds();
        
        if (delay <= 0) {
            log.warn("⚠️ Configured polling delay is {}s. Using emergency fallback {}s to prevent ban.", 
                    delay, EMERGENCY_FALLBACK_DELAY);
            delay = EMERGENCY_FALLBACK_DELAY;
        }
        
        try {
            TimeUnit.SECONDS.sleep(delay);
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
            isRunning.set(false);
        }
    }
    
    /**
     * Формирует параметр allowed_updates только если он задан.
     * Кодирует JSON, чтобы не ломать URL.
     @since 1.1.1
     */
    private String buildAllowedUpdatesParam() {
        try {
            List<String> updates = botConfig.getAllowedUpdates();
            if (updates != null && !updates.isEmpty()) {
                String json = objectMapper.writeValueAsString(updates);
                return "&allowed_updates=" + URLEncoder.encode(json, StandardCharsets.UTF_8);
            }
        } catch (Exception e) {
            log.error("Failed to serialize allowed_updates config", e);
        }
        return "";
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
    
    /**
     * Старый метод задержки.
     * @param seconds время в секундах.
     * @deprecated Используйте {@link #ensureSafeDelay()}, который гарантирует безопасность и использует конфиг.
     */
    @Deprecated(since = "1.1.1", forRemoval = true)
    private void sleep(int seconds) {
        try {
            TimeUnit.SECONDS.sleep(seconds);
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
            isRunning.set(false);
        }
    }
}