package com.kaleert.nyagram.callback;

import com.kaleert.nyagram.callback.annotation.Callback;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.config.BeanPostProcessor;
import org.springframework.core.annotation.AnnotatedElementUtils;
import org.springframework.stereotype.Component;

import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

/**
 * Реестр обработчиков callback-запросов.
 * <p>
 * Сканирует бины Spring на наличие методов, помеченных аннотацией {@link com.kaleert.nyagram.callback.annotation.Callback},
 * и сохраняет их для последующего использования в диспетчере.
 * Поддерживает сортировку обработчиков по специфичности шаблона.
 * </p>
 *
 * @since 1.0.0
 */
@Slf4j
@Component
@RequiredArgsConstructor
public class CallbackRegistry implements BeanPostProcessor { // <-- Реализуем интерфейс

    private final CallbackPatternParser parser;
    private final List<CallbackMeta> handlers = new ArrayList<>();

    @Override
    public Object postProcessAfterInitialization(Object bean, String beanName) {
        Class<?> targetClass = bean.getClass();

        if (targetClass.getName().contains("$$")) {
            targetClass = targetClass.getSuperclass();
        }

        for (Method method : targetClass.getDeclaredMethods()) {
            Callback ann = AnnotatedElementUtils.findMergedAnnotation(method, Callback.class);
            if (ann != null) {
                String template = ann.value();
                
                try {
                    method.setAccessible(true);
                    
                    CallbackMeta meta = CallbackMeta.builder()
                            .bean(bean)
                            .method(method)
                            .pattern(parser.compile(template))
                            .originalTemplate(template)
                            .build();
                    
                    handlers.add(meta);
                    
                    handlers.sort(Comparator
                            .comparingInt((CallbackMeta m) -> m.getPattern().varNames().size())
                            .thenComparingInt((CallbackMeta m) -> -m.getOriginalTemplate().length())
                    );

                    log.info("Registered Callback Handler: '{}' -> {}#{}", template, bean.getClass().getSimpleName(), method.getName());
                } catch (Exception e) {
                    log.error("Failed to register callback handler: {}", template, e);
                }
            }
        }
        return bean;
    }
    
    /**
     * Ищет наиболее подходящий обработчик для переданных данных.
     * <p>
     * Перебирает зарегистрированные шаблоны и возвращает первый совпавший.
     * Список отсортирован так, что шаблоны с большим количеством переменных или длиннее
     * проверяются раньше (принцип Longest/Most Specific Match).
     * </p>
     *
     * @param data Строка callback_data.
     * @return Метаданные обработчика или null, если совпадений нет.
     */
    public CallbackMeta findMatch(String data) {
        return handlers.stream()
                .filter(meta -> meta.getPattern().matches(data))
                .findFirst()
                .orElse(null);
    }
}