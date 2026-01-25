package com.kaleert.nyagram.api.objects.system;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.kaleert.nyagram.api.meta.BotApiObject;
import com.kaleert.nyagram.api.objects.User;

/**
 * Представляет служебное сообщение о том, что пользователь приблизился к другому пользователю.
 * <p>
 * Срабатывает, если включена Live Location и установлен радиус оповещения.
 * </p>
 *
 * @param traveler Пользователь, который инициировал оповещение (кто шел).
 * @param watcher Пользователь, который установил оповещение (кто ждал).
 * @param distance Расстояние между пользователями в метрах.
 *
 * @since 1.0.0
 */
@JsonIgnoreProperties(ignoreUnknown = true)
public record ProximityAlertTriggered(
    @JsonProperty("traveler") User traveler,
    @JsonProperty("watcher") User watcher,
    @JsonProperty("distance") Integer distance
) implements BotApiObject {}