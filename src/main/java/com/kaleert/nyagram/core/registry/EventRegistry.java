package com.kaleert.nyagram.core.registry;

import com.kaleert.nyagram.event.EventType;
import com.kaleert.nyagram.event.NyagramEventHandler;
import com.kaleert.nyagram.meta.EventMeta;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.config.BeanPostProcessor; // Важный импорт
import org.springframework.core.annotation.AnnotatedElementUtils;
import org.springframework.stereotype.Component;

import java.lang.invoke.MethodHandle;
import java.lang.invoke.MethodHandles;
import java.lang.reflect.Method;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;

/**
 * Реестр обработчиков событий (не команд).
 * <p>
 * Сканирует бины на наличие методов, помеченных {@link NyagramEventHandler}.
 * Группирует обработчики по типу события ({@link EventType}), например:
 * {@code MESSAGE_REACTION}, {@code CHAT_MEMBER}, {@code PRE_CHECKOUT_QUERY}.
 * </p>
 *
 * @since 1.0.0
 */
@Slf4j
@Component
public class EventRegistry implements BeanPostProcessor { // Реализуем этот интерфейс

    private final Map<EventType, List<EventMeta>> eventMap = new ConcurrentHashMap<>();

    @Override
    public Object postProcessAfterInitialization(Object bean, String beanName) {
        registerBeanEvents(bean);
        return bean;
    }

    private void registerBeanEvents(Object bean) {
        Class<?> targetClass = bean.getClass();
        
        if (targetClass.getName().contains("$$")) {
            targetClass = targetClass.getSuperclass();
        }

        for (Method method : targetClass.getDeclaredMethods()) {
            NyagramEventHandler annotation = AnnotatedElementUtils.findMergedAnnotation(method, NyagramEventHandler.class);
            if (annotation != null) {
                EventType type = annotation.value();
                
                MethodHandle handle = null;
                try {
                    method.setAccessible(true);
                    handle = MethodHandles.lookup().unreflect(method).bindTo(bean);
                } catch (IllegalAccessException e) {
                    log.error("Failed to create MethodHandle for event handler: {}", method.getName(), e);
                }

                EventMeta meta = new EventMeta(bean, method, handle);
                
                eventMap.computeIfAbsent(type, k -> new ArrayList<>()).add(meta);
                log.debug("Registered event handler for {} in {}", type, bean.getClass().getSimpleName());
            }
        }
    }
    
    /**
     * Возвращает список обработчиков для указанного типа события.
     *
     * @param type Тип события (например, {@link EventType#MESSAGE_REACTION}).
     * @return Список метаданных обработчиков.
     */
    public List<EventMeta> getHandlers(EventType type) {
        return eventMap.getOrDefault(type, Collections.emptyList());
    }
}