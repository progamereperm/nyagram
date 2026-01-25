package com.kaleert.nyagram.api.methods;

/**
 * Константы для режимов парсинга текста в сообщениях.
 * <p>
 * Используется в методах отправки сообщений (SendMessage, SendPhoto и т.д.)
 * для указания формата разметки.
 * </p>
 *
 * @since 1.0.0
 */
public class ParseMode {
    
    /**
     * Устаревший режим Markdown. Поддерживает меньше возможностей и имеет особенности экранирования.
     */
    public static final String MARKDOWN = "Markdown";
    
    /**
     * Режим Markdown V2. Рекомендуемый стандарт для Markdown-разметки.
     * Поддерживает спойлеры, зачеркивание, подчеркивание и вложенные сущности.
     */
    public static final String MARKDOWNV2 = "MarkdownV2";
    
    /**
     * Режим HTML. Позволяет использовать теги {@code <b>}, {@code <i>}, {@code <a>} и т.д.
     */
    public static final String HTML = "HTML";
}