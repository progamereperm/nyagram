package com.kaleert.nyagram.feature.payment;

import com.kaleert.nyagram.api.methods.invoices.AnswerPreCheckoutQuery;
import com.kaleert.nyagram.api.methods.invoices.AnswerShippingQuery;
import com.kaleert.nyagram.api.methods.invoices.CreateInvoiceLink;
import com.kaleert.nyagram.api.methods.invoices.SendInvoice;
import com.kaleert.nyagram.api.objects.message.Message;
import com.kaleert.nyagram.api.objects.payments.LabeledPrice;
import com.kaleert.nyagram.api.objects.payments.ShippingOption;
import com.kaleert.nyagram.client.NyagramClient;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.concurrent.CompletableFuture;

/**
 * Сервис-помощник для работы с платежами (Payments 2.0 и Telegram Stars).
 * <p>
 * Упрощает отправку счетов (invoices), обработку пред-проверок (pre-checkout)
 * и запросов доставки (shipping queries).
 * </p>
 *
 * @since 1.0.0
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class PaymentService {

    private final NyagramClient client;
    
    /**
     * Отправляет счет (Invoice) пользователю в чат.
     *
     * @param chatId Уникальный идентификатор чата, куда будет отправлен счет.
     * @param title Название продукта (1-32 символа).
     * @param description Описание продукта (1-255 символов).
     * @param payload Полезная нагрузка (внутренний ID заказа), не отображается пользователю.
     * @param providerToken Токен провайдера платежей (оставьте пустым для Telegram Stars).
     * @param currency Трехбуквенный код валюты (ISO 4217), например "USD", или "XTR" для Stars.
     * @param prices Список компонентов цены (разбивка стоимости).
     * @return Future с отправленным сообщением.
     */
    public CompletableFuture<Message> sendInvoice(Long chatId, String title, String description,
                                                  String payload, String providerToken,
                                                  String currency, List<LabeledPrice> prices) {
        SendInvoice invoice = SendInvoice.builder()
            .chatId(chatId.toString())
            .title(title)
            .description(description)
            .payload(payload)
            .providerToken(providerToken == null ? "" : providerToken)
            .currency(currency)
            .prices(prices)
            .startParameter("pay")
            .build();
        return client.executeAsync(invoice);
    }
    
    /**
     * Создает ссылку на оплату счета (для отправки в ЛС или QR-кода).
     *
     * @param title Название продукта (1-32 символа).
     * @param description Описание продукта (1-255 символов).
     * @param payload Полезная нагрузка (внутренний ID заказа), не отображается пользователю.
     * @param providerToken Токен провайдера платежей (оставьте пустым для Telegram Stars).
     * @param currency Трехбуквенный код валюты (ISO 4217), например "USD", или "XTR" для Stars.
     * @param prices Список компонентов цены (разбивка стоимости).
     * @return Future со строкой ссылки.
     */
    public CompletableFuture<String> createInvoiceLink(String title, String description, 
                                                       String payload, String providerToken, 
                                                       String currency, List<LabeledPrice> prices) {
        CreateInvoiceLink link = CreateInvoiceLink.builder()
            .title(title)
            .description(description)
            .payload(payload)
            .providerToken(providerToken == null ? "" : providerToken)
            .currency(currency)
            .prices(prices)
            .build();

        return client.executeAsync(link);
    }
    
    /**
     * Подтверждает готовность к оплате (Pre-Checkout Approve).
     * Должно быть вызвано в течение 10 секунд после получения запроса.
     *
     * @param preCheckoutQueryId Уникальный идентификатор запроса (pre_checkout_query_id), полученный в апдейте.
     * @return Future с результатом (true/false).
     */
    public CompletableFuture<Boolean> approvePreCheckout(String preCheckoutQueryId) {
        AnswerPreCheckoutQuery answer = AnswerPreCheckoutQuery.builder()
            .preCheckoutQueryId(preCheckoutQueryId)
            .ok(true)
            .build();
        return client.executeAsync(answer);
    }
    
    /**
     * Отклоняет оплату с указанием причины (например, товара нет в наличии).
     *
     * @param preCheckoutQueryId Уникальный идентификатор запроса (pre_checkout_query_id).
     * @param errorMessage Сообщение об ошибке, которое будет показано пользователю во всплывающем окне.
     * @return Future с результатом.
     */
    public CompletableFuture<Boolean> rejectPreCheckout(String preCheckoutQueryId, String errorMessage) {
        AnswerPreCheckoutQuery answer = AnswerPreCheckoutQuery.builder()
            .preCheckoutQueryId(preCheckoutQueryId)
            .ok(false)
            .errorMessage(errorMessage)
            .build();
        return client.executeAsync(answer);
    }
    
    /**
     * Отвечает на запрос доставки (Shipping Query), подтверждая возможность доставки.
     *
     * @param shippingQueryId ID запроса доставки.
     * @param options Список доступных вариантов доставки.
     * @return Future с результатом.
     */
    public CompletableFuture<Boolean> answerShipping(String shippingQueryId, List<ShippingOption> options) {
        AnswerShippingQuery answer = AnswerShippingQuery.builder()
            .shippingQueryId(shippingQueryId)
            .ok(true)
            .shippingOptions(options)
            .build();
        return client.executeAsync(answer);
    }
    
    /**
     * Отвечает на запрос доставки отказом (например, если доставка в этот регион невозможна).
     *
     * @param shippingQueryId ID запроса доставки.
     * @param errorMessage Сообщение об ошибке, которое увидит пользователь.
     * @return Future с результатом.
     */
    public CompletableFuture<Boolean> rejectShipping(String shippingQueryId, String errorMessage) {
        AnswerShippingQuery answer = AnswerShippingQuery.builder()
            .shippingQueryId(shippingQueryId)
            .ok(false)
            .errorMessage(errorMessage)
            .build();
        return client.executeAsync(answer);
    }
}