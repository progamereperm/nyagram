package com.kaleert.nyagram.core.resolver;

import com.kaleert.nyagram.core.ArgumentResolver;
import com.kaleert.nyagram.command.CommandContext;
import com.kaleert.nyagram.exception.ArgumentParseException;
import org.springframework.stereotype.Component;

import java.lang.reflect.Parameter;
import java.math.BigDecimal;
import java.math.BigInteger;
import java.util.Set;

/**
 * Универсальный резолвер для примитивных типов и их оберток.
 * <p>
 * Обрабатывает {@code int}, {@code long}, {@code double}, {@code float},
 * {@code boolean}, {@code byte}, {@code short}, а также {@code BigInteger} и {@code BigDecimal}.
 * </p>
 * <p>
 * Для boolean поддерживает различные варианты: "true/false", "1/0", "yes/no", "+/-".
 * </p>
 *
 * @since 1.0.0
 */
@Component
public class PrimitiveArgumentResolver implements ArgumentResolver<Object> {

    private static final Set<Class<?>> SUPPORTED_TYPES = Set.of(
            String.class,
            Integer.class, int.class,
            Long.class, long.class,
            Double.class, double.class,
            Float.class, float.class,
            Boolean.class, boolean.class,
            Byte.class, byte.class,
            Short.class, short.class,
            BigInteger.class,
            BigDecimal.class
    );

    private static final Set<String> TRUE_VALUES = Set.of("true", "1", "yes", "on", "y", "да", "+");
    private static final Set<String> FALSE_VALUES = Set.of("false", "0", "no", "off", "n", "нет", "-");

    @Override
    public boolean supports(Parameter parameter) {
        return SUPPORTED_TYPES.contains(parameter.getType());
    }

    @Override
    public Object resolve(CommandContext context, Parameter parameter, String rawValue) throws ArgumentParseException {
        Class<?> type = parameter.getType();

        if (rawValue == null && !type.isPrimitive()) {
            return null;
        }
        if (rawValue == null) {
            throw new ArgumentParseException("Аргумент не может быть null для примитивного типа " + type.getSimpleName());
        }

        try {
            if (type.equals(String.class)) {
                return rawValue;
            }
            if (type.equals(Integer.class) || type.equals(int.class)) {
                return Integer.parseInt(rawValue);
            }
            if (type.equals(Long.class) || type.equals(long.class)) {
                return Long.parseLong(rawValue);
            }
            if (type.equals(Double.class) || type.equals(double.class)) {
                return Double.parseDouble(rawValue.replace(",", "."));
            }
            if (type.equals(Float.class) || type.equals(float.class)) {
                return Float.parseFloat(rawValue.replace(",", "."));
            }
            if (type.equals(Boolean.class) || type.equals(boolean.class)) {
                return parseBoolean(rawValue);
            }
            if (type.equals(Byte.class) || type.equals(byte.class)) {
                return Byte.parseByte(rawValue);
            }
            if (type.equals(Short.class) || type.equals(short.class)) {
                return Short.parseShort(rawValue);
            }
            if (type.equals(BigInteger.class)) {
                return new BigInteger(rawValue);
            }
            if (type.equals(BigDecimal.class)) {
                return new BigDecimal(rawValue.replace(",", "."));
            }
        } catch (NumberFormatException e) {
            throw new ArgumentParseException(
                    String.format("Некорректный формат аргумента '%s'. Ожидался тип: %s", rawValue, type.getSimpleName())
            );
        }

        throw new ArgumentParseException("Неподдерживаемый тип для резолвинга: " + type.getName());
    }

    private boolean parseBoolean(String rawValue) {
        String normalized = rawValue.trim().toLowerCase();
        if (TRUE_VALUES.contains(normalized)) return true;
        if (FALSE_VALUES.contains(normalized)) return false;
        throw new NumberFormatException("Неверное значение булева типа: " + rawValue);
    }
}