package com.kaleert.nyagram.fsm;

import com.kaleert.nyagram.fsm.annotation.StateAction;
import com.kaleert.nyagram.meta.EventMeta;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.config.BeanPostProcessor;
import org.springframework.core.annotation.AnnotatedElementUtils;
import org.springframework.stereotype.Component;

import java.lang.invoke.MethodHandle;
import java.lang.invoke.MethodHandles;
import java.lang.reflect.Method;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * Реестр обработчиков состояний (State Handlers).
 * <p>
 * При запуске приложения сканирует бины на наличие методов с аннотацией {@link StateAction}
 * и сохраняет маппинг "Название состояния" -> "Метод для выполнения".
 * Используется в {@link com.kaleert.nyagram.middleware.FsmMiddleware} для маршрутизации.
 * </p>
 *
 * @since 1.0.0
 */
@Slf4j
@Component
@RequiredArgsConstructor
public class FsmRegistry implements BeanPostProcessor { // <-- Реализуем этот интерфейс

    private final Map<String, EventMeta> stateHandlers = new ConcurrentHashMap<>();

    @Override
    public Object postProcessAfterInitialization(Object bean, String beanName) {
        Class<?> targetClass = bean.getClass();

        if (targetClass.getName().contains("$$")) {
            targetClass = targetClass.getSuperclass();
        }

        for (Method method : targetClass.getDeclaredMethods()) {
            StateAction annotation = AnnotatedElementUtils.findMergedAnnotation(method, StateAction.class);
            
            if (annotation != null) {
                try {
                    method.setAccessible(true);
                    MethodHandle handle = MethodHandles.lookup().unreflect(method).bindTo(bean);
                    
                    stateHandlers.put(annotation.value(), new EventMeta(bean, method, handle));
                    log.info("Registered FSM Handler for state: '{}' -> {}#{}", 
                             annotation.value(), bean.getClass().getSimpleName(), method.getName());
                             
                } catch (IllegalAccessException e) {
                    log.error("Failed to create MethodHandle for FSM handler: {}", method.getName(), e);
                }
            }
        }
        return bean;
    }
    
    /**
     * Возвращает метаданные обработчика для указанного состояния.
     *
     * @param state Строковый идентификатор состояния (например, "WAITING_FOR_NAME").
     * @return Объект {@link EventMeta} с информацией о методе-обработчике или {@code null}, если обработчик не найден.
     */
    public EventMeta getHandler(String state) {
        return stateHandlers.get(state);
    }
}