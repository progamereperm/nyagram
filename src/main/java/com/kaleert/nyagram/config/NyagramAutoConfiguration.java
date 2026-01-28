package com.kaleert.nyagram.config;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.kaleert.nyagram.client.AdvancedMultipartBuilder;
import com.kaleert.nyagram.client.NyagramClient;
import com.kaleert.nyagram.client.retry.ExponentialBackoffStrategy;
import com.kaleert.nyagram.core.UpdateProcessor;
import com.kaleert.nyagram.core.concurrency.BotConcurrencyStrategy;
import com.kaleert.nyagram.core.concurrency.DefaultConcurrencyStrategy;
import com.kaleert.nyagram.core.concurrency.NyagramExecutor;
import com.kaleert.nyagram.core.impl.DefaultMissingArgumentHandler;
import com.kaleert.nyagram.core.polling.NyagramPoller;
import com.kaleert.nyagram.core.spi.BotStateRepository;
import com.kaleert.nyagram.core.spi.MissingArgumentHandler;
import com.kaleert.nyagram.core.spi.NyagramBotConfig;
import com.kaleert.nyagram.feature.broadcast.spi.BroadcastTargetProvider;
import com.kaleert.nyagram.feature.forum.impl.InMemoryTopicIdCache;
import com.kaleert.nyagram.feature.forum.spi.TopicIdCache;
import com.kaleert.nyagram.security.spi.UserLevelProvider;
import com.kaleert.nyagram.security.spi.UserPermissionProvider;
import com.kaleert.nyagram.webhook.NyagramWebhookController;
import com.kaleert.nyagram.webhook.WebhookLifecycleService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.autoconfigure.condition.ConditionalOnBean;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.client.RestTemplate;
import jakarta.annotation.PostConstruct;

import java.util.Collections;
import java.util.stream.Stream;

/**
 * Основной конфигурационный класс Spring Boot для библиотеки Nyagram.
 * <p>
 * Автоматически настраивает бины в зависимости от свойств в {@code application.yml}:
 * <ul>
 *     <li>Создает {@link com.kaleert.nyagram.client.NyagramClient}.</li>
 *     <li>Настраивает {@link com.kaleert.nyagram.core.polling.NyagramPoller} или Webhook контроллер.</li>
 *     <li>Регистрирует дефолтные провайдеры безопасности и обработчики, если пользователь не предоставил свои.</li>
 * </ul>
 * </p>
 *
 * @since 1.0.0
 */
@Slf4j
@Configuration
@ComponentScan(basePackages = "com.kaleert.nyagram")
@EnableConfigurationProperties(NyagramProperties.class)
public class NyagramAutoConfiguration {
  
    /**
    * Логирует инициализацию
    *
    * @since 1.1.1
    **/
    @PostConstruct
    public void init() {
        log.info("Nyagram Library Initialized");
    }
    
      /**
     * Создает конфигурацию по умолчанию.
     * @deprecated Этот метод вызывает дублирование бинов, так как NyagramProperties уже является бином.
     * Оставлен для обратной совместимости API, но отключен по умолчанию.
     */
    @Bean
    @ConditionalOnMissingBean(NyagramBotConfig.class)
    @Deprecated(since = "1.1.1", forRemoval = true)
    @ConditionalOnProperty(name = "nyagram.internal.enable-legacy-config", havingValue = "true", matchIfMissing = false)
    public NyagramBotConfig defaultNyagramConfig(NyagramProperties properties) {
        return properties;
    }
    
    /**
     * Создает исполнитель задач для обработки обновлений.
     * Параметры пула потоков берутся из конфигурации.
     *
     * @param botConfig Конфигурация бота для настройки пула потоков.
     * @return Исполнитель задач Nyagram.
     */
    @Bean
    @ConditionalOnMissingBean
    public NyagramExecutor nyagramTaskExecutor(NyagramBotConfig botConfig) {
        return new NyagramExecutor(botConfig);
    }
    
    /**
     * Создает основной HTTP-клиент для взаимодействия с Telegram API.
     *
     * @param objectMapper Маппер для JSON сериализации/десериализации.
     * @param botConfig Конфигурация бота.
     * @param taskExecutor Исполнитель асинхронных задач.
     * @param multipartBuilder Строитель multipart-запросов.
     * @param backoffStrategy Стратегия повторных попыток запросов.
     * @return Клиент Nyagram.
     */
    @Bean
    @ConditionalOnMissingBean
    public NyagramClient nyagramClient(
            ObjectMapper objectMapper, 
            NyagramBotConfig botConfig, 
            NyagramExecutor taskExecutor,
            AdvancedMultipartBuilder multipartBuilder,
            ExponentialBackoffStrategy backoffStrategy
    ) {
        return new NyagramClient(botConfig, objectMapper, multipartBuilder, taskExecutor, backoffStrategy);
    }
    
    /**
     * Определяет стратегию конкурентности. По умолчанию используется {@link DefaultConcurrencyStrategy},
     * обеспечивающая последовательную обработку для одного пользователя (FSM-safe).
     *
     * @param executor Кастомный исполнитель задач Nyagram.
     * @return Стратегия конкурентности.
     */
    @Bean
    @ConditionalOnMissingBean(BotConcurrencyStrategy.class)
    public BotConcurrencyStrategy botConcurrencyStrategy(NyagramExecutor executor) {
        return new DefaultConcurrencyStrategy(executor);
    }
    
    /**
     * Инициализирует Polling (длинный опрос), если выбран режим {@code POLLING}.
     * Запускает фоновый поток для получения обновлений.
     *
     * @param botConfig Конфигурация бота.
     * @param stateRepository Репозиторий состояния бота (для хранения offset).
     * @param updateProcessor Процессор обработки обновлений.
     * @param restTemplate Синхронный HTTP-клиент.
     * @param objectMapper Маппер для JSON.
     * @return Объект пуллера.
     */
    @Bean(initMethod = "start", destroyMethod = "stop")
    @ConditionalOnProperty(name = "nyagram.mode", havingValue = "POLLING", matchIfMissing = true)
    public NyagramPoller nyagramPoller(
            NyagramBotConfig botConfig,
            BotStateRepository stateRepository,
            UpdateProcessor updateProcessor,
            RestTemplate restTemplate,
            ObjectMapper objectMapper) {
        
        log.info("Initializing Nyagram in POLLING mode");
        return new NyagramPoller(botConfig, stateRepository, updateProcessor, restTemplate, objectMapper);
    }
    
    /**
     * Регистрирует REST-контроллер для обработки вебхуков, если выбран режим {@code WEBHOOK}.
     *
     * @param botConfig Конфигурация бота (для проверки секретного токена).
     * @param updateProcessor Процессор обработки обновлений.
     * @return Контроллер вебхуков.
     */
    @Bean
    @ConditionalOnBean(NyagramBotConfig.class)
    @ConditionalOnProperty(name = "nyagram.mode", havingValue = "WEBHOOK")
    public NyagramWebhookController nyagramWebhookController(
            NyagramBotConfig botConfig,
            UpdateProcessor updateProcessor) {
        
        log.info("Initializing Nyagram in WEBHOOK mode");
        return new NyagramWebhookController(botConfig, updateProcessor);
    }
    
    /**
     * Создает сервис для автоматической установки и удаления вебхука при старте/остановке приложения.
     *
     * @param client HTTP-клиент для выполнения запросов к API (setWebhook, deleteWebhook).
     * @param config Конфигурация бота (содержит URL вебхука и секрет).
     * @return Сервис управления жизненным циклом вебхука.
     */
    @Bean
    @ConditionalOnBean(NyagramBotConfig.class)
    @ConditionalOnProperty(name = "nyagram.mode", havingValue = "WEBHOOK")
    public WebhookLifecycleService webhookLifecycleService(
            NyagramClient client,
            NyagramBotConfig config) {
        return new WebhookLifecycleService(client, config);
    }
    
    /**
     * Регистрирует заглушку для провайдера уровней доступа (возвращает 0 для всех),
     * если пользователь не реализовал свой.
     */
    @Bean
    @ConditionalOnMissingBean(UserLevelProvider.class)
    public UserLevelProvider defaultUserLevelProvider() {
        log.debug("Wiring default UserLevelProvider (Always 0)");
        return user -> 0;
    }
    
    /**
     * Регистрирует заглушку для провайдера прав (возвращает пустой набор),
     * если пользователь не реализовал свой.
     */
    @Bean
    @ConditionalOnMissingBean(UserPermissionProvider.class)
    public UserPermissionProvider defaultUserPermissionProvider() {
        log.debug("Wiring default UserPermissionProvider (No permissions)");
        return user -> Collections.emptySet();
    }
    
    /**
     * Регистрирует заглушку для провайдера целевой аудитории рассылок (пустой поток),
     * если пользователь не реализовал свой.
     */
    @Bean
    @ConditionalOnMissingBean(BroadcastTargetProvider.class)
    public BroadcastTargetProvider defaultBroadcastProvider() {
        log.debug("Wiring default BroadcastTargetProvider (Empty stream)");
        return Stream::empty;
    }
    
    /**
     * Регистрирует стандартный обработчик отсутствующих аргументов,
     * который отправляет пользователю сообщение об ошибке с синтаксисом команды.
     */
    @Bean
    @ConditionalOnMissingBean(MissingArgumentHandler.class)
    public MissingArgumentHandler defaultMissingArgumentHandler() {
        log.debug("Wiring default MissingArgumentHandler");
        return new DefaultMissingArgumentHandler();
    }
    
    /**
     * Регистрирует кэш ID топиков в памяти по умолчанию.
     */
    @Bean
    @ConditionalOnMissingBean(TopicIdCache.class)
    public TopicIdCache topicIdCache() {
        return new InMemoryTopicIdCache();
    }
}