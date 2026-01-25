package com.kaleert.nyagram.util;

import lombok.experimental.UtilityClass;

/**
 * Утилиты для форматирования текста сообщений.
 * <p>
 * Предоставляет методы для экранирования специальных символов HTML и MarkdownV2,
 * а также помощники для создания ссылок и кода.
 * </p>
 *
 * @since 1.0.0
 */
@UtilityClass
public class TextUtil {
    
    /**
     * Экранирует специальные символы HTML.
     * <p>
     * Заменяет {@code <}, {@code >}, {@code &}, {@code "} на соответствующие сущности.
     * </p>
     *
     * @param text Исходный текст.
     * @return Безопасный HTML-текст.
     */
    public static String escapeHtml(String text) {
        if (text == null) return "";
        StringBuilder sb = new StringBuilder(text.length());
        for (int i = 0; i < text.length(); i++) {
            char c = text.charAt(i);
            switch (c) {
                case '<': sb.append("&lt;"); break;
                case '>': sb.append("&gt;"); break;
                case '&': sb.append("&amp;"); break;
                case '"': sb.append("&quot;"); break;
                default: sb.append(c);
            }
        }
        return sb.toString();
    }
    
    /**
     * Экранирует специальные символы для MarkdownV2.
     * <p>
     * Экранирует символы: {@code _ * [ ] ( ) ~ ` > # + - = | { } . !}
     * </p>
     *
     * @param text Исходный текст.
     * @return Безопасный MarkdownV2 текст.
     */
    public static String escapeMarkdown(String text) {
        if (text == null) return "";
        return text.replaceAll("([_\\*\\[\\]\\(\\)~`>#\\+\\-=|{}\\.!])", "\\\\$1");
    }
    
    /**
     * Создает HTML-ссылку.
     *
     * @param text Текст ссылки.
     * @param url Целевой URL.
     * @return Строка {@code <a href="...">text</a>}.
     */
    public static String link(String text, String url) {
        return "<a href=\"" + escapeHtml(url) + "\">" + escapeHtml(text) + "</a>";
    }
    
    /**
     * Создает упоминание пользователя (текстовую ссылку на профиль).
     *
     * @param name Отображаемое имя.
     * @param userId ID пользователя.
     * @return Строка {@code <a href="tg://user?id=...">name</a>}.
     */
    public static String mention(String name, Long userId) {
        return "<a href=\"tg://user?id=" + userId + "\">" + escapeHtml(name) + "</a>";
    }
    
    /**
     * Оборачивает текст в тег кода (моноширинный шрифт).
     * Автоматически экранирует содержимое.
     *
     * @param text Исходный текст.
     * @return Строка {@code <code>text</code>}.
     */
    public static String code(String text) {
        return "<code>" + escapeHtml(text) + "</code>";
    }
}