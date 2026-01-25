package com.kaleert.nyagram.core.resolver;

import com.kaleert.nyagram.core.ArgumentResolver;
import com.kaleert.nyagram.command.CommandContext;
import com.kaleert.nyagram.exception.ArgumentParseException;
import org.springframework.stereotype.Component;

import java.lang.reflect.Parameter;

/**
 * Резолвер для аргументов переменной длины (varargs).
 * <p>
 * Позволяет методам команд принимать неограниченное количество аргументов
 * в виде массива (например, {@code String... args} или {@code int... numbers}).
 * "Съедает" все оставшиеся токены в сообщении.
 * </p>
 *
 * @since 1.0.0
 */
@Component
public class VarArgsArgumentResolver implements ArgumentResolver<Object[]> {

    @Override
    public boolean supports(Parameter parameter) {
        return parameter.isVarArgs();
    }

    @Override
    public Object[] resolve(CommandContext context, Parameter parameter, String rawValue) throws ArgumentParseException {
        throw new UnsupportedOperationException(
            "VarArgsArgumentResolver.resolve() should not be called directly. " +
            "Varargs are handled specially in CommandDispatcherImpl."
        );
    }

    @Override
    public boolean isTokenRequired() {
        return true;
    }
}