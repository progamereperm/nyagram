package com.kaleert.nyagram.webhook;

import com.kaleert.nyagram.api.objects.Update;
import com.kaleert.nyagram.core.UpdateProcessor;
import com.kaleert.nyagram.core.spi.NyagramBotConfig;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RestController;

/**
 * REST-контроллер для приема входящих обновлений от Telegram через Webhook.
 * <p>
 * Слушает POST-запросы на настроенном пути (по умолчанию {@code /callback/telegram}).
 * Проверяет секретный токен (если настроен) и передает обновление в {@link UpdateProcessor}.
 * Активируется только в режиме {@code WEBHOOK}.
 * </p>
 *
 * @since 1.0.0
 */
@Slf4j
@RestController
@RequiredArgsConstructor
public class NyagramWebhookController {

    private final NyagramBotConfig botConfig;
    private final UpdateProcessor updateProcessor;
    
    /**
     * Обрабатывает входящий POST-запрос с обновлением от серверов Telegram.
     * <p>
     * Метод проверяет наличие и корректность заголовка {@code X-Telegram-Bot-Api-Secret-Token}
     * (если он настроен), после чего передает объект {@link Update} в {@link UpdateProcessor}
     * для асинхронной обработки.
     * </p>
     *
     * @param update Тело запроса (JSON), десериализованное в объект Update.
     * @param secretToken Секретный токен из заголовка для верификации отправителя.
     * @return {@code 200 OK}, если запрос принят. {@code 403 Forbidden}, если токен неверен.
     */
    @PostMapping("${nyagram.webhook.path:/callback/telegram}")
    public ResponseEntity<String> onUpdateReceived(
            @RequestBody Update update,
            @RequestHeader(value = "X-Telegram-Bot-Api-Secret-Token", required = false) String secretToken
    ) {
        String configSecret = botConfig.getWebhookSecretToken();
        if (configSecret != null && !configSecret.isBlank()) {
            if (!configSecret.equals(secretToken)) {
                log.warn("⚠️ Webhook received with invalid secret token. Ignored.");
                return ResponseEntity.status(HttpStatus.FORBIDDEN).body("Invalid Secret Token");
            }
        }

        try {
            updateProcessor.processAsync(update);
        } catch (Exception e) {
            log.error("Error submitting webhook update {}", update.getUpdateId(), e);
        }

        return ResponseEntity.ok("ok");
    }
}