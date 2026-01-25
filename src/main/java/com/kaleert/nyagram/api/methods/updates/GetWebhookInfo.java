package com.kaleert.nyagram.api.methods.updates;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.kaleert.nyagram.api.exception.TelegramApiRequestException;
import com.kaleert.nyagram.api.meta.BotApiMethod;
import com.kaleert.nyagram.api.objects.WebhookInfo;
import lombok.Builder;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

/**
 * Используйте этот метод для получения текущего статуса вебхука.
 * <p>
 * Возвращает объект {@link WebhookInfo}.
 * </p>
 *
 * @since 1.0.0
 */
@Data
@Builder
@NoArgsConstructor
@EqualsAndHashCode(callSuper = false)
@JsonIgnoreProperties(ignoreUnknown = true)
@JsonInclude(JsonInclude.Include.NON_NULL)
public class GetWebhookInfo extends BotApiMethod<WebhookInfo> {
    
    /** Имя метода в Telegram Bot API. */
    public static final String PATH = "getWebhookInfo";

    @Override
    public String getMethod() {
        return PATH;
    }

    @Override
    public WebhookInfo deserializeResponse(String answer) throws TelegramApiRequestException {
        return deserializeResponse(answer, WebhookInfo.class);
    }
    
    @Override
    public void validate() {
    }
}