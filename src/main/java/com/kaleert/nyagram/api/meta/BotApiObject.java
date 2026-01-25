package com.kaleert.nyagram.api.meta;

import java.io.Serializable;

/**
 * Маркерный интерфейс для всех объектов Telegram Bot API.
 * <p>
 * Все объекты, отправляемые в Telegram или получаемые от него (User, Message, Update и т.д.),
 * должны реализовывать этот интерфейс. Он обеспечивает базовую типизацию и гарантирует
 * сериализуемость объектов.
 * </p>
 *
 * @since 1.0.0
 */
public interface BotApiObject extends Serializable {
}