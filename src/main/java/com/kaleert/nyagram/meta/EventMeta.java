package com.kaleert.nyagram.meta;

import lombok.AllArgsConstructor;
import lombok.Data;
import java.lang.reflect.Method;
import java.lang.invoke.MethodHandle;

/**
 * Метаданные обработчика событий (не команд).
 * <p>
 * Хранит ссылку на бин и метод, который должен быть вызван при наступлении
 * определенного события {@link com.kaleert.nyagram.event.EventType}.
 * </p>
 *
 * @since 1.0.0
 */
@Data
@AllArgsConstructor
public class EventMeta {
    private final Object bean;
    private final Method method;
    private final MethodHandle methodHandle;
}