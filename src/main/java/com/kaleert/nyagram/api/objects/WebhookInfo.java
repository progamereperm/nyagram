package com.kaleert.nyagram.api.objects;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.kaleert.nyagram.api.meta.BotApiObject;

import java.util.List;

/**
 * Содержит информацию о текущем статусе вебхука.
 *
 * @param url URL вебхука. Если пустой, вебхук не установлен.
 * @param hasCustomCertificate True, если используется самоподписанный сертификат.
 * @param pendingUpdateCount Количество обновлений, ожидающих доставки.
 * @param ipAddress IP-адрес, используемый для доставки обновлений (опционально).
 * @param lastErrorDate Дата последней ошибки доставки (Unix timestamp).
 * @param lastErrorMessage Текст последней ошибки.
 * @param lastSynchronizationErrorDate Дата последней ошибки синхронизации (Unix timestamp).
 * @param maxConnections Максимальное количество одновременных подключений.
 * @param allowedUpdates Список типов обновлений, на которые подписан бот.
 *
 * @since 1.0.0
 */
@JsonIgnoreProperties(ignoreUnknown = true)
public record WebhookInfo(
    @JsonProperty("url") String url,
    @JsonProperty("has_custom_certificate") Boolean hasCustomCertificate,
    @JsonProperty("pending_update_count") Integer pendingUpdateCount,
    @JsonProperty("ip_address") String ipAddress,
    @JsonProperty("last_error_date") Integer lastErrorDate,
    @JsonProperty("last_error_message") String lastErrorMessage,
    @JsonProperty("last_synchronization_error_date") Integer lastSynchronizationErrorDate,
    @JsonProperty("max_connections") Integer maxConnections,
    @JsonProperty("allowed_updates") List<String> allowedUpdates
) implements BotApiObject {
    
    /**
     * Проверяет, установлен ли вебхук.
     * @return true, если URL задан.
     */
    public boolean isSet() {
        return url != null && !url.isBlank();
    }
}