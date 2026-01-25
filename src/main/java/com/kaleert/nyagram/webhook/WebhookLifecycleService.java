package com.kaleert.nyagram.webhook;

import com.kaleert.nyagram.api.methods.updates.DeleteWebhook;
import com.kaleert.nyagram.api.methods.updates.SetWebhook;
import com.kaleert.nyagram.client.NyagramClient;
import com.kaleert.nyagram.core.spi.NyagramBotConfig;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.SmartLifecycle;

/**
 * Сервис управления жизненным циклом вебхука.
 * <p>
 * Автоматически устанавливает вебхук при запуске приложения (через {@code setWebhook})
 * и удаляет его при остановке (через {@code deleteWebhook}).
 * Гарантирует корректную регистрацию URL бота в Telegram API.
 * </p>
 *
 * @since 1.0.0
 */
@Slf4j
@RequiredArgsConstructor
public class WebhookLifecycleService implements SmartLifecycle {

    private final NyagramClient client;
    private final NyagramBotConfig config;
    private boolean running = false;

    @Override
    public void start() {
        String url = config.getWebhookUrl();
        String path = config.getWebhookPath();
        
        if (url == null || url.isBlank()) {
            throw new IllegalStateException("Webhook URL is not configured but Bot Mode is WEBHOOK");
        }

        String fullUrl = url.endsWith("/") ? url + path.substring(1) : url + path;
        
        log.info("Setting webhook to: {}", fullUrl);
        
        try {
            SetWebhook setWebhook = SetWebhook.builder()
                    .url(fullUrl)
                    .secretToken(config.getWebhookSecretToken())
                    .dropPendingUpdates(false)
                    .build();
            
            Boolean result = client.execute(setWebhook);
            if (Boolean.TRUE.equals(result)) {
                log.info("Webhook set successfully!");
                running = true;
            } else {
                log.error("Failed to set webhook (API returned false)");
            }
        } catch (Exception e) {
            log.error("Error setting webhook", e);
            throw new RuntimeException("Could not start webhook mode", e);
        }
    }

    @Override
    public void stop() {
        log.info("Deleting webhook...");
        try {
            client.execute(DeleteWebhook.builder().dropPendingUpdates(false).build());
            log.info("Webhook deleted.");
        } catch (Exception e) {
            log.warn("Failed to delete webhook on shutdown: {}", e.getMessage());
        }
        running = false;
    }

    @Override
    public boolean isRunning() {
        return running;
    }
}