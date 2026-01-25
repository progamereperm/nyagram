package com.kaleert.nyagram.feature.broadcast.spi;

import java.util.stream.Stream;

/**
 * Интерфейс поставщика целевой аудитории для рассылок.
 * <p>
 * Пользователь библиотеки должен реализовать этот интерфейс, чтобы указать {@link com.kaleert.nyagram.feature.broadcast.BroadcastManager},
 * кому отправлять сообщения.
 * </p>
 *
 * @since 1.0.0
 */
public interface BroadcastTargetProvider {
    
    /**
     * Возвращает поток идентификаторов чатов (пользователей), которым нужно отправить сообщение.
     * <p>
     * Рекомендуется использовать {@link Stream} вместо {@link java.util.List} для ленивой загрузки
     * данных из базы, чтобы не занимать много памяти при больших объемах пользователей.
     * </p>
     *
     * @return Поток ID чатов.
     */
    Stream<Long> getTargetChatIds();
}