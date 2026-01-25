package com.kaleert.nyagram.api.objects;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.kaleert.nyagram.api.meta.BotApiObject;

/**
 * Представляет информацию о заказе (предоставляется пользователем при оплате).
 *
 * @param name Имя пользователя.
 * @param phoneNumber Номер телефона пользователя.
 * @param email Email пользователя.
 * @param shippingAddress Адрес доставки.
 *
 * @since 1.0.0
 */
@JsonIgnoreProperties(ignoreUnknown = true)
public record OrderInfo(
    @JsonProperty("name") String name,
    @JsonProperty("phone_number") String phoneNumber,
    @JsonProperty("email") String email,
    @JsonProperty("shipping_address") ShippingAddress shippingAddress
) implements BotApiObject {}