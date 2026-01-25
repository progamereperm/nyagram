package com.kaleert.nyagram.core.resolver;

import com.kaleert.nyagram.core.ArgumentResolver;
import lombok.NonNull;

import java.lang.reflect.Parameter;
import java.util.Set;

/**
 * Расширенный интерфейс резолвера, который явно декларирует поддерживаемые типы.
 * <p>
 * Позволяет оптимизировать поиск подходящего резолвера через Map, вместо перебора всех бинов.
 * </p>
 *
 * @param <T> Тип возвращаемого значения.
 * @since 1.0.0
 */
public interface TypedArgumentResolver<T> extends ArgumentResolver<T> {
    
    /**
     * Возвращает набор классов, которые этот резолвер может обработать.
     * @return Set классов.
     */
    @NonNull
    Set<Class<?>> getSupportedTypes();
    
    @Override
    default boolean supports(Parameter parameter) {
        return getSupportedTypes().contains(parameter.getType());
    }
}