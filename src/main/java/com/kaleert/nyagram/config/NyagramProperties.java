package com.kaleert.nyagram.config;

import com.kaleert.nyagram.core.spi.NyagramBotConfig;
import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

import java.util.List;

/**
 * Класс свойств конфигурации для привязки настроек из {@code application.yml} (префикс {@code nyagram}).
 * <p>
 * Реализует интерфейс {@link NyagramBotConfig}, предоставляя доступ к токену, режиму работы
 * и другим параметрам бота.
 * </p>
 *
 * @since 1.0.0
 */
@Data
@ConfigurationProperties(prefix = "nyagram")
public class NyagramProperties implements NyagramBotConfig {

    private String botToken;
    private String botUsername;
    private BotMode mode = BotMode.POLLING;
    
    private String webhookUrl;
    private String webhookPath = "/callback/telegram";
    private String webhookSecretToken;
    
    private int longPollingTimeoutSeconds = 50;
    private int pollingRetryDelaySeconds = 5;
    private int pollingMaxBackoffSeconds = 60;
    
    private int workerThreadCount = Runtime.getRuntime().availableProcessors() * 2;
    private String workerThreadPrefix = "nyagram-worker-";
    private long workerKeepAliveTimeMs = 60_000L;
    
    private List<String> allowedUpdates;
    
    private String apiUrl = "https://api.telegram.org";
}