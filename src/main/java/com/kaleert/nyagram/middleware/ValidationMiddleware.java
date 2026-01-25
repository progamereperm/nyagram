package com.kaleert.nyagram.middleware;

import com.kaleert.nyagram.client.NyagramClient;
import com.kaleert.nyagram.api.objects.Update;
import com.kaleert.nyagram.api.objects.User;
import com.kaleert.nyagram.api.objects.message.Message;
import com.kaleert.nyagram.command.CommandContext;
import com.kaleert.nyagram.core.CommandResult;
import com.kaleert.nyagram.meta.CommandMeta;
import com.kaleert.nyagram.util.CommandTokenizer;
import com.kaleert.nyagram.validation.Validation;
import com.kaleert.nyagram.pipeline.*;
import lombok.extern.slf4j.Slf4j;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;

import java.lang.reflect.Parameter;
import java.util.List;
import java.util.Optional;
import java.util.concurrent.CompletableFuture;
import java.util.regex.Pattern;

/**
 * Middleware для валидации аргументов команды.
 * <p>
 * Проверяет входные параметры метода на соответствие аннотациям {@link com.kaleert.nyagram.validation.Validation}
 * (длина, regex и т.д.) и {@link com.kaleert.nyagram.command.CommandArgument} (обязательность).
 * Если валидация не проходит, возвращает ошибку с описанием проблемы.
 * </p>
 *
 * @since 1.0.0
 */
@Slf4j
@Component
@Order(200)
public class ValidationMiddleware implements CommandPreProcessor {

    @Override
    public Optional<CommandResult> process(CommandContext context, CommandMeta commandMeta) {
        try {
            String text = context.getText();
            if (!StringUtils.hasText(text)) {
                return Optional.of(CommandResult.error("Пустое сообщение"));
            }

            String commandPath = commandMeta.getFullCommandPath();
            String argsString = text.substring(commandPath.length()).trim();
            List<String> tokens = CommandTokenizer.tokenize(argsString);

            List<Parameter> parameters = commandMeta.getMethodParameters();
            int tokenIndex = 0;

            int requiredParamsCount = (int) parameters.stream()
                    .filter(p -> {
                        return !isContextParameter(p);
                    })
                    .filter(p -> {
                        com.kaleert.nyagram.command.CommandArgument argAnn = 
                            p.getAnnotation(com.kaleert.nyagram.command.CommandArgument.class);
                        return argAnn == null || argAnn.required();
                    })
                    .count();

            if (tokens.size() < requiredParamsCount) {
                return Optional.of(CommandResult.error(
                    String.format("Недостаточно аргументов. Ожидалось минимум %d, получено %d.\nИспользование: %s",
                        requiredParamsCount, tokens.size(), commandMeta.getUsageSyntax())
                ));
            }

            for (Parameter param : parameters) {
                if (isContextParameter(param)) {
                    continue;
                }

                Validation validation = param.getAnnotation(Validation.class);
                if (validation != null && tokenIndex < tokens.size()) {
                    String value = tokens.get(tokenIndex);
                    Optional<CommandResult> validationResult = validateParameter(param, value, validation);
                    if (validationResult.isPresent()) {
                        return validationResult;
                    }
                }

                if (!isContextParameter(param)) {
                    tokenIndex++;
                }
            }

            return Optional.empty();

        } catch (Exception e) {
            log.warn("Validation error for command {}: {}", 
                    commandMeta.getFullCommandPath(), e.getMessage());
            return Optional.of(CommandResult.error("Ошибка валидации: " + e.getMessage()));
        }
    }

    private boolean isContextParameter(Parameter param) {
        Class<?> type = param.getType();
        return type.isAssignableFrom(CommandContext.class) ||
               type.isAssignableFrom(com.kaleert.nyagram.api.objects.Update.class) ||
               type.isAssignableFrom(com.kaleert.nyagram.api.objects.message.Message.class) ||
               type.isAssignableFrom(com.kaleert.nyagram.api.objects.User.class) ||
               type.isAssignableFrom(com.kaleert.nyagram.client.NyagramClient.class);
    }

    private Optional<CommandResult> validateParameter(Parameter parameter, String value, Validation validation) {
        String paramName = getParameterName(parameter);

        if (validation.minLength() > 0 && value.length() < validation.minLength()) {
            return Optional.of(CommandResult.error(
                String.format("Параметр '%s' слишком короткий. Минимальная длина: %d символов",
                    paramName, validation.minLength())
            ));
        }

        if (validation.maxLength() < Integer.MAX_VALUE && value.length() > validation.maxLength()) {
            return Optional.of(CommandResult.error(
                String.format("Параметр '%s' слишком длинный. Максимальная длина: %d символов",
                    paramName, validation.maxLength())
            ));
        }

        if (!validation.pattern().isEmpty()) {
            try {
                Pattern pattern = Pattern.compile(validation.pattern());
                if (!pattern.matcher(value).matches()) {
                    return Optional.of(CommandResult.error(
                        String.format("Параметр '%s' не соответствует формату",
                            paramName)
                    ));
                }
            } catch (Exception e) {
                log.warn("Invalid regex pattern for parameter {}: {}", paramName, validation.pattern());
            }
        }

        return Optional.empty();
    }

    private String getParameterName(Parameter parameter) {
        com.kaleert.nyagram.command.CommandArgument argAnn = 
            parameter.getAnnotation(com.kaleert.nyagram.command.CommandArgument.class);
        
        if (argAnn != null && !argAnn.value().isEmpty()) {
            return argAnn.value();
        }
        
        return parameter.getName();
    }
    
    /**
     * Проверяет, соответствует ли строка формату юзернейма Telegram.
     * <p>
     * Правила: 5-32 символа, латиница, цифры, подчеркивание. Не может состоять только из цифр.
     * </p>
     *
     * @param username Юзернейм (без @).
     * @return true, если валиден.
     */
    public static boolean isValidUsername(String username) {
        if (username == null || username.length() < 5 || username.length() > 32) {
            return false;
        }
        return username.matches("^[a-zA-Z0-9_]+$") && !username.matches("^[0-9]+$");
    }
    
    /**
     * Проверяет, является ли строка корректным адресом электронной почты.
     *
     * @param email Строка для проверки.
     * @return true, если формат валиден.
     */
    public static boolean isValidEmail(String email) {
        if (email == null) return false;
        return email.matches("^[A-Za-z0-9+_.-]+@[A-Za-z0-9.-]+\\.[A-Za-z]{2,}$");
    }
    
    /**
     * Проверяет, является ли строка корректным номером телефона.
     * <p>
     * Допускает форматы: +7..., 8..., просто цифры.
     * </p>
     *
     * @param phone Строка с номером.
     * @return true, если формат похож на телефонный номер.
     */
    public static boolean isValidPhoneNumber(String phone) {
        if (phone == null) return false;
        return phone.matches("^\\+?[1-9]\\d{1,14}$");
    }
    
    /**
     * Проверяет, является ли строка валидным URL-адресом.
     * <p>
     * Поддерживает протоколы http, https, ftp.
     * </p>
     *
     * @param url Строка ссылки.
     * @return true, если это URL.
     */
    public static boolean isValidUrl(String url) {
        if (url == null) return false;
        return url.matches("^(https?|ftp)://[^\\s/$.?#].[^\\s]*$");
    }
    
    /**
     * Проверяет, является ли строка корректным целым числом в заданном диапазоне.
     *
     * @param value Строковое значение.
     * @param min Минимальное значение.
     * @param max Максимальное значение.
     * @return true, если это Integer и он входит в диапазон.
     */
    public static boolean isValidInteger(String value, int min, int max) {
        try {
            int intValue = Integer.parseInt(value);
            return intValue >= min && intValue <= max;
        } catch (NumberFormatException e) {
            return false;
        }
    }
    
    /**
     * Проверяет, является ли строка корректным длинным целым числом (Long) в заданном диапазоне.
     *
     * @param value Строковое значение.
     * @param min Минимальное значение.
     * @param max Максимальное значение.
     * @return true, если это Long и он входит в диапазон.
     */
    public static boolean isValidLong(String value, long min, long max) {
        try {
            long longValue = Long.parseLong(value);
            return longValue >= min && longValue <= max;
        } catch (NumberFormatException e) {
            return false;
        }
    }
    
    /**
     * Проверяет, является ли строка корректным числом с плавающей точкой в заданном диапазоне.
     *
     * @param value Строковое значение.
     * @param min Минимальное значение.
     * @param max Максимальное значение.
     * @return true, если это Double и он входит в диапазон.
     */
    public static boolean isValidDouble(String value, double min, double max) {
        try {
            double doubleValue = Double.parseDouble(value.replace(",", "."));
            return doubleValue >= min && doubleValue <= max;
        } catch (NumberFormatException e) {
            return false;
        }
    }
}