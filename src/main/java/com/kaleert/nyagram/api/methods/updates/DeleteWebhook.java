package com.kaleert.nyagram.api.methods.updates;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.kaleert.nyagram.api.meta.BotApiMethodBoolean;
import lombok.*;

/**
 * Используйте этот метод для удаления интеграции через вебхук и переключения обратно
 * в режим получения обновлений через getUpdates (Polling).
 * <p>
 * Возвращает {@code True} в случае успеха.
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
public class DeleteWebhook extends BotApiMethodBoolean {
    
    /** Имя метода в Telegram Bot API. */
    public static final String PATH = "deleteWebhook";

    /**
     * Передайте True, чтобы удалить все ожидающие обновления.
     */
    @JsonProperty("drop_pending_updates")
    private Boolean dropPendingUpdates;

    @Override
    public String getMethod() {
        return PATH;
    }

    @Override
    public void validate() {
    }
    
    /**
     * Создает запрос на удаление вебхука с удалением всех накопившихся обновлений.
     * <p>
     * Полезно при переключении бота на другой сервер или при сбоях, чтобы не обрабатывать старые сообщения.
     * </p>
     *
     * @return готовый объект запроса.
     */
    public static DeleteWebhook dropPending() {
        return DeleteWebhook.builder().dropPendingUpdates(true).build();
    }
    
    /**
     * Создает запрос на немедленное удаление вебхука без удаления обновлений.
     * <p>
     * Накопившиеся обновления можно будет получить через {@link GetUpdates}.
     * </p>
     *
     * @return готовый объект запроса.
     */
    public static DeleteWebhook now() {
        return DeleteWebhook.builder().dropPendingUpdates(false).build();
    }
}