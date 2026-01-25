package com.kaleert.nyagram.core.spi;

import com.kaleert.nyagram.api.objects.Update;

/**
 * Интерфейс для низкоуровневой обработки обновлений.
 * <p>
 * Реализации этого интерфейса получают управление ДО того, как обновление
 * попадет в диспетчеры команд или событий. Это позволяет реализовать кастомную
 * логику маршрутизации или перехватить специфичные обновления.
 * </p>
 *
 * @since 1.0.0
 */
public interface RawUpdateHandler {

    /**
     * Обрабатывает входящее обновление.
     *
     * @param update Объект обновления от Telegram.
     * @return {@code true}, если обновление было полностью обработано и дальнейшая
     *         передача в {@link com.kaleert.nyagram.dispatcher.CommandDispatcher}
     *         или {@link com.kaleert.nyagram.dispatcher.EventDispatcher} не требуется.
     *         {@code false}, если нужно продолжить стандартную обработку.
     */
    boolean handle(Update update);
}