package com.kaleert.nyagram.core.resolver;

import com.kaleert.nyagram.api.objects.Update;
import com.kaleert.nyagram.api.objects.User;
import com.kaleert.nyagram.api.objects.message.Message;
import com.kaleert.nyagram.client.NyagramClient;
import com.kaleert.nyagram.command.CommandContext;
import com.kaleert.nyagram.exception.ArgumentParseException;
import org.springframework.stereotype.Component;

import java.lang.reflect.Parameter;
import java.util.Set;

/**
 * Контейнерный класс, содержащий стандартные реализации {@link ArgumentResolver}
 * для базовых типов Java.
 *
 * @since 1.0.0
 */
@Component
public class StandardResolvers {
    
    /**
     * Резолвер для {@code Integer}.
     */
    @Component
    public static class IntegerResolver implements TypedArgumentResolver<Integer> {
        @Override
        public Set<Class<?>> getSupportedTypes() {
            return Set.of(Integer.class, int.class);
        }

        @Override
        public Integer resolve(CommandContext context, Parameter parameter, String rawValue) {
            try {
                return Integer.parseInt(rawValue);
            } catch (NumberFormatException e) {
                throw new ArgumentParseException("Аргумент должен быть целым числом: " + rawValue);
            }
        }
    }
    
    /**
     * Резолвер для {@code Long}.
     */
    @Component
    public static class LongResolver implements TypedArgumentResolver<Long> {
        @Override
        public Set<Class<?>> getSupportedTypes() {
            return Set.of(Long.class, long.class);
        }

        @Override
        public Long resolve(CommandContext context, Parameter parameter, String rawValue) {
            try {
                return Long.parseLong(rawValue);
            } catch (NumberFormatException e) {
                throw new ArgumentParseException("Аргумент должен быть числом (Long): " + rawValue);
            }
        }
    }
    
    /**
     * Резолвер для {@code Double}. Поддерживает запятую и точку.
     */
    @Component
    public static class DoubleResolver implements TypedArgumentResolver<Double> {
        @Override
        public Set<Class<?>> getSupportedTypes() {
            return Set.of(Double.class, double.class);
        }

        @Override
        public Double resolve(CommandContext context, Parameter parameter, String rawValue) {
            try {
                return Double.parseDouble(rawValue.replace(",", "."));
            } catch (NumberFormatException e) {
                throw new ArgumentParseException("Аргумент должен быть дробным числом: " + rawValue);
            }
        }
    }
    
    /**
     * Резолвер для {@code Boolean}.
     * Понимает "true", "yes", "1", "+", "on" и их отрицания.
     */
    @Component
    public static class BooleanResolver implements TypedArgumentResolver<Boolean> {
        private static final Set<String> TRUE_VALUES = Set.of("true", "1", "yes", "on", "y", "да", "+");
        private static final Set<String> FALSE_VALUES = Set.of("false", "0", "no", "off", "n", "нет", "-");

        @Override
        public Set<Class<?>> getSupportedTypes() {
            return Set.of(Boolean.class, boolean.class);
        }

        @Override
        public Boolean resolve(CommandContext context, Parameter parameter, String rawValue) {
            String normalized = rawValue.toLowerCase().trim();
            if (TRUE_VALUES.contains(normalized)) return true;
            if (FALSE_VALUES.contains(normalized)) return false;
            throw new ArgumentParseException("Аргумент должен быть логическим (да/нет): " + rawValue);
        }
    }
    
    /**
     * Резолвер для {@code String}. Просто возвращает токен как есть.
     */
    @Component
    public static class StringResolver implements TypedArgumentResolver<String> {
        @Override
        public Set<Class<?>> getSupportedTypes() {
            return Set.of(String.class);
        }

        @Override
        public String resolve(CommandContext context, Parameter parameter, String rawValue) {
            return rawValue;
        }
    }
    
    /**
     * Резолвер для {@code Enum}.
     * Ищет константу по имени (case-insensitive).
     */
    @Component
    public static class EnumResolver implements TypedArgumentResolver<Enum<?>> {
        @Override
        public Set<Class<?>> getSupportedTypes() {
            return Set.of(Enum.class);
        }

        @Override
        @SuppressWarnings({"unchecked", "rawtypes"})
        public Enum<?> resolve(CommandContext context, Parameter parameter, String rawValue) {
            if (rawValue == null) return null;
            
            Class<? extends Enum> enumClass = (Class<? extends Enum>) parameter.getType();
            String normalized = rawValue.trim();

            for (Enum<?> constant : enumClass.getEnumConstants()) {
                if (constant.name().equalsIgnoreCase(normalized)) {
                    return constant;
                }
            }
            throw new ArgumentParseException("Неверное значение для " + enumClass.getSimpleName());
        }
    }
    
    /**
     * Специальный резолвер для инъекции контекстных объектов.
     * <p>
     * Позволяет добавлять в аргументы метода объекты:
     * {@link CommandContext}, {@link Update}, {@link Message}, {@link User}, {@link NyagramClient}.
     * Не потребляет токены из текста команды.
     * </p>
     */
    @Component
    public static class ContextInjectionResolver implements TypedArgumentResolver<Object> {
        @Override
        public Set<Class<?>> getSupportedTypes() {
            return Set.of(
                CommandContext.class, 
                Update.class, 
                Message.class, 
                User.class, 
                NyagramClient.class
            );
        }

        @Override
        public Object resolve(CommandContext context, Parameter parameter, String rawValue) {
            Class<?> type = parameter.getType();
            if (type.isAssignableFrom(CommandContext.class)) return context;
            if (type.isAssignableFrom(Update.class)) return context.getUpdate();
            if (type.isAssignableFrom(Message.class)) return context.getMessage().orElse(null);
            if (type.isAssignableFrom(User.class)) return context.getTelegramUser();
            if (type.isAssignableFrom(NyagramClient.class)) return context.getClient();
            return null;
        }

        @Override
        public boolean isTokenRequired() {
            return false;
        }
    }
}