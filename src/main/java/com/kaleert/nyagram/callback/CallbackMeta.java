package com.kaleert.nyagram.callback;

import com.kaleert.nyagram.callback.CallbackPatternParser.CompiledPattern;
import lombok.Builder;
import lombok.Getter;

import java.lang.reflect.Method;

/**
 * Метаданные зарегистрированного обработчика callback-запроса.
 * <p>
 * Хранит информацию о бине, методе и скомпилированном шаблоне (regex),
 * который используется для сопоставления входящих данных.
 * </p>
 *
 * @since 1.0.0
 */
@Getter
@Builder
public class CallbackMeta {
    
    /** Бин (экземпляр класса), содержащий метод. */
    private final Object bean;
    
    /** Метод-обработчик. */
    private final Method method;
    
    /** Скомпилированный шаблон пути. */
    private final CompiledPattern pattern;
    
    /** Оригинальная строка шаблона (из аннотации). */
    private final String originalTemplate;
}