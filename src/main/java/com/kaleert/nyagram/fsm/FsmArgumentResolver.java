package com.kaleert.nyagram.fsm;

import com.kaleert.nyagram.command.CommandContext;
import com.kaleert.nyagram.fsm.annotation.SessionData;
import lombok.RequiredArgsConstructor;
import org.springframework.context.ApplicationContext;
import org.springframework.stereotype.Component;
import org.springframework.beans.BeansException;

import java.lang.reflect.Method;
import java.lang.reflect.Parameter;
import java.util.ArrayList;
import java.util.List;

/**
 * Резолвер аргументов для методов-обработчиков состояний FSM.
 * <p>
 * Отвечает за инъекцию параметров в методы, помеченные аннотацией {@link com.kaleert.nyagram.fsm.annotation.StateAction}.
 * Поддерживает внедрение:
 * <ul>
 *     <li>{@link CommandContext}</li>
 *     <li>{@link UserSession}</li>
 *     <li>Данных сессии через аннотацию {@link SessionData}</li>
 *     <li>Любых Spring-бинов (сервисов, репозиториев)</li>
 * </ul>
 * </p>
 *
 * @since 1.0.0
 */
@Component
@RequiredArgsConstructor
public class FsmArgumentResolver {

    private final ApplicationContext applicationContext;
    
    /**
     * Выполняет разрешение аргументов для метода FSM-хендлера.
     * <p>
     * Анализирует параметры метода и подставляет в них значения:
     * <ul>
     *     <li>{@code CommandContext} и {@code UserSession} — напрямую.</li>
     *     <li>Параметры с {@code @SessionData} — извлекает из map сессии.</li>
     *     <li>Остальные типы — пытается найти соответствующий бин в Spring Context.</li>
     * </ul>
     * </p>
     *
     * @param method Метод, который будет вызван.
     * @param context Текущий контекст команды.
     * @param session Активная сессия пользователя.
     * @return Массив аргументов для вызова метода.
     * @throws IllegalStateException если тип аргумента не поддерживается или бин не найден.
     */
    public Object[] resolve(Method method, CommandContext context, UserSession session) {
        Parameter[] parameters = method.getParameters();
        List<Object> arguments = new ArrayList<>(parameters.length);

        for (Parameter parameter : parameters) {
            Class<?> paramType = parameter.getType();

            if (paramType.equals(CommandContext.class)) {
                arguments.add(context);
            } else if (paramType.equals(UserSession.class)) {
                arguments.add(session);
            } 
            
            else if (parameter.isAnnotationPresent(SessionData.class)) {
                arguments.add(resolveSessionData(parameter, session));
            }

            else if (!isFrameworkClass(paramType)) {
                try {
                    Object beanInstance = applicationContext.getBean(paramType);
                    arguments.add(beanInstance);
                } catch (BeansException e) {
                    throw new IllegalStateException(
                        String.format("Не удалось разрешить аргумент типа %s в методе %s. " + 
                                      "Он должен быть либо Spring-бином, либо помечен @SessionData, " +
                                      "либо быть CommandContext/UserSession.", 
                                      paramType.getName(), method.getName()), e
                    );
                }
            }
            
            else {
                 throw new IllegalStateException(
                    "Неподдерживаемый тип аргумента " + paramType.getName() + 
                    " в методе " + method.getName()
                 );
            }
        }
        return arguments.toArray();
    }
    
    private boolean isFrameworkClass(Class<?> type) {
        return type.equals(CommandContext.class) || type.equals(UserSession.class);
    }
    
    private Object resolveSessionData(Parameter parameter, UserSession session) {
        SessionData annotation = parameter.getAnnotation(SessionData.class);
        String key = annotation.value();
        return session.getData(key, parameter.getType());
    }
}
