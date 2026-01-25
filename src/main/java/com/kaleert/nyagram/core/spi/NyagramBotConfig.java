package com.kaleert.nyagram.core.spi;

import java.util.List;

/**
 * Основной интерфейс конфигурации бота.
 * <p>
 * Обычно реализуется через {@code application.yml} и класс свойств Spring Boot,
 * но может быть переопределен вручную.
 * </p>
 *
 * @since 1.0.0
 */
public interface NyagramBotConfig {

    /**
     * Токен бота, полученный от @BotFather.
     *
     * @return токен.
     */
    String getBotToken();
    
    /**
     * Имя пользователя бота (без @).
     *
     * @return username.
     */
    String getBotUsername();
    
    /**
     * Режим работы бота (Polling или Webhook).
     *
     * @return режим работы.
     */
    default BotMode getMode() { return BotMode.POLLING; }
    
    /**
     * Публичный URL для вебхука (требуется для {@link BotMode#WEBHOOK}).
     * Должен быть HTTPS.
     *
     * @return URL или null.
     */
    default String getWebhookUrl() { return null; }
    
    /**
     * Внутренний путь, на который бот будет слушать POST-запросы от Telegram.
     *
     * @return путь (по умолчанию "/callback/telegram").
     */
    default String getWebhookPath() { return "/callback/telegram"; }
    
    /**
     * Секретный токен для верификации входящих запросов от Telegram (X-Telegram-Bot-Api-Secret-Token).
     *
     * @return токен или null.
     */
    default String getWebhookSecretToken() { return null; }
    
    /**
     * Таймаут длинного опроса в секундах (для {@link BotMode#POLLING}).
     *
     * @return секунды (по умолчанию 50).
     */
    default int getLongPollingTimeoutSeconds() { return 50; }
    
    /**
     * Задержка перед повторным подключением при ошибке сети (в секундах).
     *
     * @return секунды (по умолчанию 5).
     */
    default int getPollingRetryDelaySeconds() { return 5; }
    
    /**
     * Максимальная задержка (backoff) при повторяющихся ошибках сети.
     *
     * @return секунды (по умолчанию 60).
     */
    default int getPollingMaxBackoffSeconds() { return 60; }
    
    /**
     * Количество потоков-воркеров для обработки обновлений.
     *
     * @return количество потоков.
     */
    default int getWorkerThreadCount() { return Runtime.getRuntime().availableProcessors() * 2; }
    
    /**
     * Префикс имен потоков-воркеров.
     *
     * @return префикс (по умолчанию "nyagram-worker-").
     */
    default String getWorkerThreadPrefix() { return "nyagram-worker-"; }
    
    /**
     * Время жизни простаивающего потока в пуле.
     *
     * @return миллисекунды.
     */
    default long getWorkerKeepAliveTimeMs() { return 60_000L; }    
    
    /**
     * Список типов обновлений, которые бот хочет получать.
     * Если null, Telegram будет присылать все типы по умолчанию.
     *
     * @return список типов или null.
     */
    default List<String> getAllowedUpdates() { return null; }
    
    /**
     * URL API сервера Telegram.
     * Можно изменить для использования локального Bot API сервера.
     *
     * @return URL API.
     */
    default String getApiUrl() {  return "https://api.telegram.org"; }
    
    /**
     * Режимы работы бота.
     */
    enum BotMode {
        /** Бот сам опрашивает сервер Telegram (getUpdates). */
        POLLING, 
        /** Telegram отправляет запросы на сервер бота. */
        WEBHOOK
    }
}