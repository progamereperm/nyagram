package com.kaleert.nyagram.api.objects.stickers;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.kaleert.nyagram.api.meta.BotApiObject;

/**
 * Описывает позицию, где маска должна быть размещена на лице.
 *
 * @param point Часть лица ("forehead", "eyes", "mouth", или "chin").
 * @param xShift Сдвиг по оси X относительно центра лица.
 * @param yShift Сдвиг по оси Y относительно центра лица.
 * @param scale Масштаб маски.
 *
 * @since 1.0.0
 */
public record MaskPosition(
    @JsonProperty("point") String point,
    @JsonProperty("x_shift") Float xShift,
    @JsonProperty("y_shift") Float yShift,
    @JsonProperty("scale") Float scale
) implements BotApiObject {}