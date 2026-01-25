package com.kaleert.nyagram.exception;

import com.kaleert.nyagram.command.CommandContext;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.ApplicationContext;
import org.springframework.stereotype.Component;

import jakarta.annotation.PostConstruct;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.HashMap;
import java.util.Map;

/**
 * Центральный компонент обработки исключений в Nyagram.
 * <p>
 * Сканирует контекст Spring на наличие бинов {@link BotControllerAdvice},
 * регистрирует их методы-обработчики и маршрутизирует возникающие исключения
 * в соответствующий метод на основе типа исключения.
 * </p>
 *
 * @since 1.0.0
 */
@Slf4j
@Component
@RequiredArgsConstructor
public class GlobalExceptionHandler {

    private final ApplicationContext context;
    private final Map<Class<? extends Throwable>, HandlerMethod> handlers = new HashMap<>();
    
    /**
     * Инициализирует обработчик, собирая методы из {@code @BotControllerAdvice}.
     */
    @PostConstruct
    public void init() {
        Map<String, Object> adviceBeans = context.getBeansWithAnnotation(BotControllerAdvice.class);
        
        if (adviceBeans.isEmpty()) {
            log.info("No @BotControllerAdvice beans found.");
            return;
        }

        for (Object bean : adviceBeans.values()) {
            for (Method method : bean.getClass().getDeclaredMethods()) {
                if (method.isAnnotationPresent(BotExceptionHandler.class)) {
                    BotExceptionHandler ann = method.getAnnotation(BotExceptionHandler.class);
                    for (Class<? extends Throwable> exClass : ann.value()) {
                        
                        if (handlers.containsKey(exClass)) {
                            log.warn("Duplicate exception handler for {}. Overwriting with {}#{}", 
                                    exClass.getSimpleName(), bean.getClass().getSimpleName(), method.getName());
                        }
                        
                        handlers.put(exClass, new HandlerMethod(bean, method));
                        log.debug("Registered exception handler for {} in {}", 
                                exClass.getSimpleName(), bean.getClass().getSimpleName());
                    }
                }
            }
        }
        log.info("Registered {} global exception handlers.", handlers.size());
    }
    
    /**
     * Пытается найти и вызвать кастомный обработчик для возникшего исключения.
     *
     * @param ex Исключение.
     * @param cmdContext Контекст команды, в которой возникло исключение.
     * @return {@code true}, если исключение было успешно обработано.
     */
    public boolean handle(Throwable ex, CommandContext cmdContext) {
        Throwable cause = ex instanceof InvocationTargetException ? ex.getCause() : ex;
        
        Class<?> clazz = cause.getClass();
        
        while (clazz != Throwable.class) {
            if (handlers.containsKey(clazz)) {
                try {
                    HandlerMethod handler = handlers.get(clazz);
                    handler.method.invoke(handler.bean, cause, cmdContext);
                    return true;
                } catch (Exception e) {
                    log.error("Error executing ExceptionHandler for {}", cause.getClass().getSimpleName(), e);
                    return false; 
                }
            }
            clazz = clazz.getSuperclass();
        }
        
        return false;
    }
    
    /**
     * Внутренний рекорд для хранения метаинформации о методе обработки исключения.
     *
     * @param bean Экземпляр бина (контроллера), содержащего метод обработки.
     * @param method Метод, который будет вызван для обработки исключения.
     */
    record HandlerMethod(Object bean, Method method) {}
}