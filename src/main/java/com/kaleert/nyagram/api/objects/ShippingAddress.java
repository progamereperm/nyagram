package com.kaleert.nyagram.api.objects;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.kaleert.nyagram.api.meta.BotApiObject;

/**
 * Представляет адрес доставки, предоставленный пользователем при оформлении заказа.
 *
 * @param countryCode Двухбуквенный ISO 3166-1 alpha-2 код страны.
 * @param state Штат, область или регион (если применимо).
 * @param city Город.
 * @param streetLine1 Первая строка адреса (улица).
 * @param streetLine2 Вторая строка адреса (квартира, офис).
 * @param postCode Почтовый индекс.
 *
 * @since 1.0.0
 */
@JsonIgnoreProperties(ignoreUnknown = true)
public record ShippingAddress(
    @JsonProperty("country_code") String countryCode,
    @JsonProperty("state") String state,
    @JsonProperty("city") String city,
    @JsonProperty("street_line1") String streetLine1,
    @JsonProperty("street_line2") String streetLine2,
    @JsonProperty("post_code") String postCode
) implements BotApiObject {}