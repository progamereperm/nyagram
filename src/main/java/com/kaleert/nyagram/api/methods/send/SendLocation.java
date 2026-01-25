package com.kaleert.nyagram.api.methods.send;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.kaleert.nyagram.api.exception.TelegramApiRequestException;
import com.kaleert.nyagram.api.exception.TelegramApiValidationException;
import com.kaleert.nyagram.api.meta.BotApiMethod;
import com.kaleert.nyagram.api.objects.message.Message;
import lombok.*;

/**
 * Используйте этот метод для отправки точки на карте (геолокации).
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
public class SendLocation extends BotApiMethod<Message> {
    
    /** Имя метода в Telegram Bot API. */
    public static final String PATH = "sendLocation";

    /**
     * Уникальный идентификатор чата.
     */
    @JsonProperty("chat_id")
    private String chatId;

    /**
     * Широта (Latitude).
     */
    @JsonProperty("latitude")
    private Double latitude;

    /**
     * Долгота (Longitude).
     */
    @JsonProperty("longitude")
    private Double longitude;

    /**
     * Период в секундах, в течение которого локация будет обновляться (Live Location).
     * (60-86400).
     */
    @JsonProperty("live_period")
    private Integer livePeriod;

    /**
     * Направление движения пользователя в градусах (1-360).
     */
    @JsonProperty("heading")
    private Integer heading;

    /**
     * Радиус предупреждения о приближении в метрах (0-100000).
     */
    @JsonProperty("proximity_alert_radius")
    private Integer proximityAlertRadius;

    @Override
    public String getMethod() {
        return PATH;
    }

    @Override
    public Message deserializeResponse(String answer) throws TelegramApiRequestException {
        return deserializeResponse(answer, Message.class);
    }

    @Override
    public void validate() throws TelegramApiValidationException {
        if (chatId == null) throw new TelegramApiValidationException("ChatId обязателен", PATH, "chat_id");
        if (latitude == null || longitude == null) {
            throw new TelegramApiValidationException("Координаты обязательны", PATH, "lat/lon");
        }
    }
    
    /**
     * Устанавливает период трансляции геопозиции (Live Location).
     *
     * @param seconds Время в секундах (60-86400), в течение которого локация будет обновляться.
     * @return текущий билдер.
     */
    public SendLocation live(int seconds) {
        this.livePeriod = seconds;
        return this;
    }
    
    /**
     * Создает запрос на отправку геопозиции.
     *
     * @param chatId ID чата.
     * @param lat Широта.
     * @param lon Долгота.
     * @return готовый объект запроса.
     */
    public static SendLocation of(Long chatId, double lat, double lon) {
        return SendLocation.builder()
                .chatId(chatId.toString())
                .latitude(lat)
                .longitude(lon)
                .build();
    }
}