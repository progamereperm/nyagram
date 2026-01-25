package com.kaleert.nyagram.callback;

import lombok.Getter;
import org.springframework.stereotype.Component;

import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Парсер шаблонов callback-путей.
 * <p>
 * Преобразует строковые шаблоны с переменными (например, {@code store:buy:{itemId}})
 * в регулярные выражения и извлекает значения переменных при совпадении.
 * </p>
 *
 * @since 1.0.0
 */
@Component
public class CallbackPatternParser {

    private static final Pattern VAR_PATTERN = Pattern.compile("\\{([a-zA-Z0-9_]+)}");
    
    /**
     * Компилирует строковый шаблон в регулярное выражение.
     * <p>
     * Пример: {@code user:{id}:action} -> {@code ^\Quser:\E(.+?)\Q:action\E$}
     * </p>
     *
     * @param template Шаблон пути (например, "game:{gameId}:start").
     * @return Объект с готовым Pattern и списком имен переменных.
     */
    public CompiledPattern compile(String template) {
        Matcher matcher = VAR_PATTERN.matcher(template);
        List<String> varNames = new ArrayList<>();
        StringBuilder regexBuilder = new StringBuilder("^");
        
        int lastEnd = 0;
        while (matcher.find()) {
            regexBuilder.append(Pattern.quote(template.substring(lastEnd, matcher.start())));
            
            varNames.add(matcher.group(1));
            regexBuilder.append("(.+?)");
            
            lastEnd = matcher.end();
        }
        regexBuilder.append(Pattern.quote(template.substring(lastEnd)));
        regexBuilder.append("$");

        return new CompiledPattern(Pattern.compile(regexBuilder.toString()), varNames);
    }
    
    /**
     * Извлекает значения переменных из строки данных на основе скомпилированного шаблона.
     *
     * @param compiled Скомпилированный шаблон.
     * @param data Реальная строка callback_data.
     * @return Карта "Имя переменной" -> "Значение".
     */
    public Map<String, String> extractVariables(CompiledPattern compiled, String data) {
        Matcher matcher = compiled.pattern.matcher(data);
        if (!matcher.matches()) {
            return Collections.emptyMap();
        }

        Map<String, String> variables = new HashMap<>();
        for (int i = 0; i < compiled.varNames.size(); i++) {
            String value = matcher.group(i + 1);
            variables.put(compiled.varNames.get(i), value);
        }
        return variables;
    }
    
    /**
     * Внутренний рекорд для хранения скомпилированного паттерна и имен переменных.
     * @param pattern Регулярное выражение.
     * @param varNames Список имен переменных в порядке их появления в шаблоне.
     */
    public record CompiledPattern(Pattern pattern, List<String> varNames) {
        
        /**
         * Проверяет, соответствует ли строка данных этому шаблону.
         *
         * @param data Строка callback_data.
         * @return true, если соответствует.
         */
        public boolean matches(String data) {
            return pattern.matcher(data).matches();
        }
    }
}