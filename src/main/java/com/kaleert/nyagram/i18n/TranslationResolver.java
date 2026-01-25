package com.kaleert.nyagram.i18n;

import java.util.HashMap;
import java.util.Map;

/**
 * Помощник для подстановки параметров в шаблоны переводов.
 * <p>
 * Реализует Fluent API для удобного построения строк.
 * Пример: {@code translate(locale, "welcome").p("name", "User").build()}.
 * </p>
 *
 * @since 1.0.0
 */
public class TranslationResolver {

    private final String translationString;
    private final Map<String, Object> params = new HashMap<>();

    /**
     * Создает новый резолвер.
     * @param translationString Шаблон строки с плейсхолдерами (например, "Hello, %name%!")
     */
    public TranslationResolver(String translationString) {
        this.translationString = translationString;
    }

    /**
     * Добавляет параметр для подстановки.
     * @param name Имя плейсхолдера (например, "name" для %name%)
     * @param value Значение, которое будет подставлено
     * @return Текущий экземпляр для продолжения цепочки
     */
    public TranslationResolver p(String name, Object value) {
        this.params.put(name, value != null ? value : "NULL");
        return this;
    }
    
    /**
     * Добавляет параметр для подстановки в шаблон.
     * <p>
     * Алиас для метода {@link #p(String, Object)}.
     * </p>
     *
     * @param name Имя плейсхолдера в строке (без процентов, например "user").
     * @param value Значение, которое заменит плейсхолдер.
     * @return Текущий экземпляр резолвера (fluent interface).
     */
    public TranslationResolver param(String name, Object value) {
        return p(name, value);
    }

    /**
     * Выполняет подстановку всех параметров и возвращает готовую строку.
     * @return Готовая локализованная строка.
     */
    public String build() {
        if (params.isEmpty()) {
            return translationString;
        }

        String result = translationString;
        for (Map.Entry<String, Object> entry : params.entrySet()) {
            String placeholder = "%" + entry.getKey() + "%";
            result = result.replace(placeholder, String.valueOf(entry.getValue()));
        }

        return result;
    }
    
    @Override
    public String toString() {
        return build();
    }
}