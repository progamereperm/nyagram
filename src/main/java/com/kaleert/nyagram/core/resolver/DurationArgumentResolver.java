package com.kaleert.nyagram.core.resolver;

import com.kaleert.nyagram.core.ArgumentResolver;
import com.kaleert.nyagram.command.CommandContext;
import com.kaleert.nyagram.exception.ArgumentParseException;
import org.springframework.stereotype.Component;

import java.lang.reflect.Parameter;
import java.time.Duration;
import java.util.Set;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Резолвер для преобразования строкового представления времени в {@link java.time.Duration}.
 * <p>
 * Поддерживает форматы вида:
 * <ul>
 *     <li>{@code 10m} - 10 минут</li>
 *     <li>{@code 2h 30m} - 2 часа 30 минут</li>
 *     <li>{@code 1d} - 1 день</li>
 *     <li>{@code 30s} - 30 секунд</li>
 * </ul>
 * Полезен для команд временного бана или таймеров (например, {@code /mute @user 1h}).
 * </p>
 *
 * @since 1.0.0
 */
@Component
public class DurationArgumentResolver implements TypedArgumentResolver<Duration> {

    private static final Pattern DURATION_PATTERN = Pattern.compile("(\\d+)([yMwdhms])");
    
    private static final Set<String> INFINITY_KEYWORDS = Set.of(
            "inf", "infinity", "permanent", "perm", "forever", "бессрочно", "навсегда"
    );

    @Override
    public Duration resolve(CommandContext context, Parameter parameter, String rawValue) throws ArgumentParseException {
        if (rawValue == null || rawValue.isBlank()) {
            return null;
        }

        if (INFINITY_KEYWORDS.contains(rawValue.toLowerCase())) {
            return null;
        }

        Matcher matcher = DURATION_PATTERN.matcher(rawValue);
        Duration totalDuration = Duration.ZERO;
        boolean found = false;

        while (matcher.find()) {
            found = true;
            try {
                int amount = Integer.parseInt(matcher.group(1));
                String unit = matcher.group(2);

                switch (unit) {
                    case "y" -> totalDuration = totalDuration.plus(Duration.ofDays(amount * 365L));
                    case "M" -> totalDuration = totalDuration.plus(Duration.ofDays(amount * 30L));
                    case "w" -> totalDuration = totalDuration.plus(Duration.ofDays(amount * 7L));
                    case "d" -> totalDuration = totalDuration.plus(Duration.ofDays(amount));
                    case "h" -> totalDuration = totalDuration.plus(Duration.ofHours(amount));
                    case "m" -> totalDuration = totalDuration.plus(Duration.ofMinutes(amount));
                    case "s" -> totalDuration = totalDuration.plus(Duration.ofSeconds(amount));
                    default -> throw new ArgumentParseException("Неизвестная единица времени: " + unit);
                }
            } catch (NumberFormatException e) {
                throw new ArgumentParseException("Ошибка в числе: " + matcher.group(1));
            } catch (ArithmeticException e) {
                throw new ArgumentParseException("Слишком большое значение времени");
            }
        }

        if (!found) {
            throw new ArgumentParseException(
                "Неверный формат времени. Пример: '1d 2h' (1 день 2 часа) или '30m' (30 минут)."
            );
        }

        if (totalDuration.isZero()) {
            throw new ArgumentParseException("Длительность не может быть нулевой.");
        }

        return totalDuration;
    }
    
    /**
     * Возвращает набор типов, поддерживаемых этим резолвером.
     *
     * @return Set, содержащий {@link java.time.Duration}.
     */
    public Set<Class<?>> getSupportedTypes() {
            return Set.of(Duration.class);
        }
}