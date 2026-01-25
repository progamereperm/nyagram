package com.kaleert.nyagram.api.objects;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.kaleert.nyagram.api.meta.BotApiObject;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * Содержит информацию о том, почему запрос был неуспешным.
 * <p>
 * Возвращается в случае ошибок 429 (Too Many Requests) или при миграции группы в супергруппу.
 * </p>
 *
 * @since 1.0.0
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ResponseParameters implements BotApiObject {

    private static final String MIGRATE_TO_CHAT_ID_FIELD = "migrate_to_chat_id";
    private static final String RETRY_AFTER_FIELD = "retry_after";

    /**
     * Группа была преобразована в супергруппу. Это новый ID чата.
     * Старый ID больше не валиден.
     */
    @JsonProperty(MIGRATE_TO_CHAT_ID_FIELD)
    private Long migrateToChatId;

    /**
     * Количество секунд, которое нужно подождать перед повтором запроса.
     * (При ошибке 429: Too Many Requests).
     */
    @JsonProperty(RETRY_AFTER_FIELD)
    private Integer retryAfter;
    
    /**
     * Проверяет, произошла ли ошибка из-за миграции группы в супергруппу.
     *
     * @return true, если доступен {@code migrate_to_chat_id}.
     */
    public boolean isMigration() {
        return migrateToChatId != null;
    }
    
    /**
     * Проверяет, произошла ли ошибка из-за превышения лимита запросов (Rate Limit).
     *
     * @return true, если доступен {@code retry_after}.
     */
    public boolean isRateLimit() {
        return retryAfter != null;
    }
}