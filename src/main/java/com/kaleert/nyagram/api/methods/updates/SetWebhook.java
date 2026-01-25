package com.kaleert.nyagram.api.methods.updates;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.kaleert.nyagram.api.exception.TelegramApiValidationException;
import com.kaleert.nyagram.api.meta.BotApiMethodBoolean;
import com.kaleert.nyagram.api.objects.InputFile;
import lombok.*;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * Используйте этот метод для настройки вебхука.
 * <p>
 * Каждый раз, когда бот получает обновление, Telegram будет отправлять HTTPS POST запрос
 * на указанный URL.
 * </p>
 *
 * @since 1.0.0
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode(callSuper = false)
@JsonIgnoreProperties(ignoreUnknown = true)
@JsonInclude(JsonInclude.Include.NON_NULL)
public class SetWebhook extends BotApiMethodBoolean {
    
    /** Имя метода в Telegram Bot API. */
    public static final String PATH = "setWebhook";

    /**
     * HTTPS URL для отправки обновлений. Используйте пустую строку для удаления вебхука.
     */
    @JsonProperty("url")
    private String url;

    /**
     * Публичный ключ сертификата, если используется самоподписанный сертификат.
     */
    @JsonProperty("certificate")
    private InputFile certificate;

    /**
     * Фиксированный IP-адрес, который будет использоваться для отправки запросов вебхука
     * вместо разрешения IP через DNS.
     */
    @JsonProperty("ip_address")
    private String ipAddress;

    /**
     * Максимально допустимое количество одновременных HTTPS соединений для доставки обновлений (1-100).
     */
    @JsonProperty("max_connections")
    private Integer maxConnections;

    /**
     * Список типов обновлений, на которые подписывается бот.
     */
    @JsonProperty("allowed_updates")
    private List<String> allowedUpdates;

    /**
     * Передайте True, чтобы отбросить все ожидающие обновления.
     */
    @JsonProperty("drop_pending_updates")
    private Boolean dropPendingUpdates;

    /**
     * Секретный токен (1-256 символов), который будет отправлен в заголовке
     * {@code X-Telegram-Bot-Api-Secret-Token} каждого запроса вебхука.
     */
    @JsonProperty("secret_token")
    private String secretToken;

    @Override
    public String getMethod() {
        return PATH;
    }

    @Override
    public void validate() throws TelegramApiValidationException {
        if (url == null || url.isEmpty()) {
            throw new TelegramApiValidationException("URL cannot be empty", PATH, "url");
        }
        
        if (!url.startsWith("https://")) {
             throw new TelegramApiValidationException("Webhook URL must start with https://", PATH, "url");
        }
        
        if (certificate != null) {
            certificate.validate();
        }
    }
    
    /**
     * Добавляет один тип обновлений в список разрешенных.
     *
     * @param updateType Тип обновления (например, "message").
     * @return текущий билдер.
     */
    public SetWebhook addAllowedUpdate(String updateType) {
        if (this.allowedUpdates == null) {
            this.allowedUpdates = new ArrayList<>();
        }
        this.allowedUpdates.add(updateType);
        return this;
    }
    
    /**
     * Устанавливает полный список разрешенных типов обновлений.
     *
     * @param updates Типы обновлений.
     * @return текущий билдер.
     */
    public SetWebhook allowedUpdates(String... updates) {
        this.allowedUpdates = new ArrayList<>(Arrays.asList(updates));
        return this;
    }

   /**
     * Указывает, что нужно сбросить все накопленные обновления перед установкой вебхука.
     *
     * @return текущий билдер.
     */
    public SetWebhook dropPending() {
        this.dropPendingUpdates = true;
        return this;
    }
    
    /**
     * Устанавливает секретный токен для проверки подлинности запросов от Telegram.
     * <p>
     * Токен будет приходить в заголовке {@code X-Telegram-Bot-Api-Secret-Token}.
     * Должен содержать только символы {@code A-Z}, {@code a-z}, {@code 0-9}, {@code _} и {@code -}.
     * </p>
     *
     * @param token Секретный токен (1-256 символов).
     * @return текущий билдер.
     * @throws IllegalArgumentException если токен содержит недопустимые символы.
     */
    public SetWebhook withSecretToken(String token) {
        if (token != null && !token.matches("[A-Za-z0-9_-]+")) {
             throw new IllegalArgumentException("Secret token contains invalid characters");
        }
        this.secretToken = token;
        return this;
    }
    
    /**
     * Создает запрос на установку вебхука по URL.
     *
     * @param url HTTPS URL вашего бота.
     * @return готовый объект запроса.
     */
    public static SetWebhook of(String url) {
        return SetWebhook.builder().url(url).build();
    }
    
    /**
     * Создает запрос на установку вебхука с самоподписанным сертификатом.
     *
     * @param url HTTPS URL вашего бота.
     * @param certificate Файл публичного ключа сертификата.
     * @return готовый объект запроса.
     */
    public static SetWebhook of(String url, InputFile certificate) {
        return SetWebhook.builder()
                .url(url)
                .certificate(certificate)
                .build();
    }
}