package com.kaleert.nyagram.api.methods.invoices;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.kaleert.nyagram.api.exception.TelegramApiRequestException;
import com.kaleert.nyagram.api.exception.TelegramApiValidationException;
import com.kaleert.nyagram.api.meta.BotApiMethod;
import com.kaleert.nyagram.api.objects.message.Message;
import com.kaleert.nyagram.api.objects.payments.LabeledPrice;
import com.kaleert.nyagram.api.objects.replykeyboard.InlineKeyboardMarkup;
import lombok.*;

import java.util.ArrayList;
import java.util.List;

/**
 * Используйте этот метод для отправки счета на оплату (Invoice) прямо в чат.
 * <p>
 * Поддерживает как обычные валюты, так и Telegram Stars ("XTR").
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
public class SendInvoice extends BotApiMethod<Message> {
    
    /** Имя метода в Telegram Bot API. */
    public static final String PATH = "sendInvoice";

    /**
     * Уникальный идентификатор чата.
     */
    @JsonProperty("chat_id")
    private String chatId;

    /**
     * Название продукта (1-32 символа).
     */
    @JsonProperty("title")
    private String title;

    /**
     * Описание продукта (1-255 символов).
     */
    @JsonProperty("description")
    private String description;

    /**
     * Полезная нагрузка (Payload), которая вернется боту при успешной оплате.
     * Не видна пользователю.
     */
    @JsonProperty("payload")
    private String payload;

    /**
     * Токен провайдера платежей. Оставьте пустым для Telegram Stars.
     */
    @JsonProperty("provider_token")
    private String providerToken;

    /**
     * Валюта (например, "USD", "RUB" или "XTR" для звезд).
     */
    @JsonProperty("currency")
    private String currency;

    /**
     * Разбивка цены (например: Товар - 100, Налог - 20).
     */
    @JsonProperty("prices")
    private List<LabeledPrice> prices;

    /**
     * Параметр для Deep-link (например, если счет открыт по ссылке).
     */
    @JsonProperty("start_parameter")
    private String startParameter;

    /**
     * URL фотографии товара.
     */
    @JsonProperty("photo_url")
    private String photoUrl;

    /**
     * Inline-клавиатура. Если не задана, будет автоматически добавлена кнопка "Оплатить".
     * Если задаете свою, ПЕРВАЯ кнопка должна быть типа {@code pay}.
     */
    @JsonProperty("reply_markup")
    private InlineKeyboardMarkup replyMarkup;

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
        if (chatId == null) throw new TelegramApiValidationException("ChatId required", PATH, "chat_id");
        if (title == null) throw new TelegramApiValidationException("Title required", PATH, "title");
        if (payload == null) throw new TelegramApiValidationException("Payload required", PATH, "payload");
        if (currency == null) throw new TelegramApiValidationException("Currency required", PATH, "currency");
        if (prices == null || prices.isEmpty()) throw new TelegramApiValidationException("Prices required", PATH, "prices");
        
        if (!"XTR".equals(currency) && (providerToken == null || providerToken.isEmpty())) {
             throw new TelegramApiValidationException("Provider token required for fiat currency", PATH, "provider_token");
        }
    }
    
    /**
     * Создает счет для оплаты в Telegram Stars ("XTR").
     * <p>
     * Для звезд не нужен токен провайдера.
     * </p>
     *
     * @param chatId ID чата.
     * @param title Название товара.
     * @param description Описание.
     * @param payload Полезная нагрузка (скрытые данные).
     * @param amount Количество звезд.
     * @return готовый объект запроса.
     */
    public static SendInvoice forStars(Long chatId, String title, String description, String payload, int amount) {
        return SendInvoice.builder()
                .chatId(chatId.toString())
                .title(title)
                .description(description)
                .payload(payload)
                .currency("XTR")
                .providerToken("")
                .prices(List.of(new LabeledPrice("Price", amount)))
                .build();
    }
    
    /**
     * Добавляет компонент цены в счет.
     *
     * @param label Название (например, "Товар", "Доставка").
     * @param amount Цена в минимальных единицах (копейки, центы, звезды).
     * @return текущий билдер.
     */
    public SendInvoice addPrice(String label, int amount) {
        if (this.prices == null) this.prices = new ArrayList<>();
        this.prices.add(new LabeledPrice(label, amount));
        return this;
    }
}