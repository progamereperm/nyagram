package com.kaleert.nyagram.util;

import lombok.experimental.UtilityClass;

import java.util.ArrayList;
import java.util.List;

/**
 * Утилита для парсинга строки аргументов команды.
 * <p>
 * Разбивает строку на токены, учитывая:
 * <ul>
 *     <li>Пробелы как разделители.</li>
 *     <li>Двойные кавычки (") для объединения слов в один токен.</li>
 *     <li>Одинарные кавычки (') для объединения слов.</li>
 *     <li>Экранирование символов через обратный слэш (\).</li>
 * </ul>
 * </p>
 *
 * @since 1.0.0
 */
@UtilityClass
public class CommandTokenizer {
    
    /**
     * Разбивает строку команды на аргументы (токены).
     * <p>
     * Учитывает:
     * <ul>
     *     <li>Пробелы как разделители.</li>
     *     <li>Двойные кавычки ({@code "..."}) для объединения слов.</li>
     *     <li>Одинарные кавычки ({@code '...'}) для объединения слов.</li>
     *     <li>Экранирование символов ({@code \}).</li>
     * </ul>
     * Пример: {@code arg1 "arg 2" arg3} -> {@code ["arg1", "arg 2", "arg3"]}
     * </p>
     *
     * @param text Исходная строка аргументов.
     * @return Список токенов.
     */
    public static List<String> tokenize(String text) {
        List<String> tokens = new ArrayList<>();
        if (text == null || text.isBlank()) {
            return tokens;
        }

        StringBuilder currentToken = new StringBuilder();
        boolean inDoubleQuotes = false;
        boolean inSingleQuotes = false;
        boolean escaped = false;

        for (char c : text.toCharArray()) {
            if (escaped) {
                currentToken.append(c);
                escaped = false;
                continue;
            }

            if (c == '\\') {
                escaped = true;
                continue;
            }

            if (c == '"' && !inSingleQuotes) {
                inDoubleQuotes = !inDoubleQuotes;
                continue;
            }

            if (c == '\'' && !inDoubleQuotes) {
                inSingleQuotes = !inSingleQuotes;
                continue;
            }

            if (Character.isWhitespace(c) && !inDoubleQuotes && !inSingleQuotes) {
                if (currentToken.length() > 0) {
                    tokens.add(currentToken.toString());
                    currentToken.setLength(0);
                }
            } else {
                currentToken.append(c);
            }
        }

        if (currentToken.length() > 0) {
            tokens.add(currentToken.toString());
        }

        return tokens;
    }
}