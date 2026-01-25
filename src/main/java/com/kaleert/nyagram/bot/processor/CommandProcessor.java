package com.kaleert.nyagram.bot.processor;

import com.kaleert.nyagram.api.meta.BotApiMethod;
import com.kaleert.nyagram.api.objects.Update;

import java.io.Serializable;
import java.util.Optional;

/**
 * Интерфейс для обработки входящих обновлений и формирования ответа.
 * <p>
 * Реализации этого интерфейса инкапсулируют логику преобразования входящего {@link Update}
 * в исходящий метод API (например, отправку сообщения). Используется в архитектуре
 * бота для абстрагирования логики обработки.
 * </p>
 *
 * @since 1.0.0
 */
public interface CommandProcessor {
    
    /**
     * Обрабатывает обновление и возвращает опциональный метод для выполнения.
     *
     * @param update Входящее обновление.
     * @return Optional с методом API, который нужно выполнить в ответ, или empty.
     */
    Optional<? extends BotApiMethod<? extends Serializable>> process(Update update);
}
