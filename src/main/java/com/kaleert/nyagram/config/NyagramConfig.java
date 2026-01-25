package com.kaleert.nyagram.config;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import com.kaleert.nyagram.core.spi.NyagramBotConfig;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.client.SimpleClientHttpRequestFactory;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;
import org.springframework.web.client.RestClient;
import org.springframework.web.client.RestTemplate;

import java.time.Duration;
import java.util.concurrent.Executor;

/**
 * Конфигурация низкоуровневых компонентов инфраструктуры.
 * <p>
 * Создает бины для:
 * <ul>
 *     <li>{@link com.fasterxml.jackson.databind.ObjectMapper} - JSON процессинг.</li>
 *     <li>{@link org.springframework.web.client.RestClient} - HTTP клиент.</li>
 *     <li>{@link java.util.concurrent.Executor} - пул потоков для обработки апдейтов.</li>
 * </ul>
 * </p>
 *
 * @since 1.0.0
 */
@Slf4j
@Configuration
public class NyagramConfig {
    
    /**
     * Создает билдер для {@link RestClient}.
     * <p>
     * Используется как прототип для создания клиентов с разными базовыми URL или настройками.
     * </p>
     *
     * @return новый билдер RestClient.
     */
    @Bean
    @ConditionalOnMissingBean
    public RestClient.Builder restClientBuilder() {
        return RestClient.builder();
    }
    
    /**
     * Настраивает {@link ObjectMapper} для работы с Telegram API.
     * <p>
     * Конфигурация включает:
     * <ul>
     *     <li>{@link JavaTimeModule} для работы с датами Java 8.</li>
     *     <li>Игнорирование неизвестных полей (для совместимости с новыми версиями API).</li>
     *     <li>Отключение записи дат как timestamp.</li>
     *     <li>Пропуск null-значений при сериализации.</li>
     * </ul>
     * </p>
     *
     * @return настроенный ObjectMapper.
     */
    @Bean
    public ObjectMapper telegramObjectMapper() {
        return new ObjectMapper()
                .registerModule(new JavaTimeModule())
                .configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false)
                .configure(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS, false)
                .setSerializationInclusion(JsonInclude.Include.NON_NULL);
    }
    
    /**
     * Создает основной HTTP-клиент для взаимодействия с Telegram Bot API.
     * <p>
     * Настраивает таймауты подключения и чтения (с учетом времени long polling),
     * а также устанавливает базовый URL API.
     * </p>
     *
     * @param config Конфигурация бота.
     * @param mapper ObjectMapper.
     * @return готовый к использованию RestClient.
     */
    @Bean
    public RestClient telegramRestClient(NyagramBotConfig config, ObjectMapper mapper) {
        SimpleClientHttpRequestFactory factory = new SimpleClientHttpRequestFactory();
        factory.setConnectTimeout((int) Duration.ofSeconds(10).toMillis());
        factory.setReadTimeout((int) Duration.ofSeconds(config.getLongPollingTimeoutSeconds() + 20).toMillis());

        return RestClient.builder()
                .baseUrl(config.getApiUrl())
                .requestFactory(factory)
                .defaultHeader("User-Agent", "NyaGram/1.0 (Spring Boot)")
                .build();
    }
    
    /**
     * Создает пул потоков для асинхронного выполнения задач бота.
     * <p>
     * Используется для обработки команд, если выбран режим {@code CONCURRENT},
     * а также для внутренних асинхронных операций.
     * </p>
     *
     * @return Executor (ThreadPoolTaskExecutor).
     */
    @Bean(name = "botTaskExecutor")
    @ConditionalOnMissingBean(name = "botTaskExecutor")
    public Executor botTaskExecutor() {
        ThreadPoolTaskExecutor executor = new ThreadPoolTaskExecutor();
        executor.setCorePoolSize(Runtime.getRuntime().availableProcessors() * 2);
        executor.setMaxPoolSize(100);
        executor.setQueueCapacity(1000);
        executor.setThreadNamePrefix("nyagram-worker-");
        executor.initialize();
        return executor;
    }
    
    /**
     * Создает {@link RestTemplate} для синхронных HTTP-запросов.
     * <p>
     * В основном используется компонентом {@link com.kaleert.nyagram.core.polling.NyagramPoller}
     * для выполнения long-polling запросов, так как RestClient реактивен, а поллинг требует блокировки.
     * </p>
     *
     * @param config Конфигурация бота.
     * @return настроенный RestTemplate.
     */
    @Bean
    public RestTemplate restTemplate(NyagramBotConfig config) {
        SimpleClientHttpRequestFactory factory = new SimpleClientHttpRequestFactory();
        factory.setConnectTimeout((int) Duration.ofSeconds(10).toMillis());
        factory.setReadTimeout((int) Duration.ofSeconds(config.getLongPollingTimeoutSeconds() + 10).toMillis());
        return new RestTemplate(factory);
    }
}