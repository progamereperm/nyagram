package com.kaleert.nyagram.callback.annotation;

import java.lang.annotation.*;

/**
 * Помечает метод как обработчик нажатия на Inline-кнопку (Callback Query).
 * <p>
 * Поддерживает шаблоны путей с переменными (path variables), обернутыми в фигурные скобки.
 * </p>
 *
 * @example
 * Кнопка с данными: <code>"store:buy:item_52"</code>
 * <pre>
 * {@code @Callback("store:buy:{itemId}")}
 * public void onBuy(CommandContext ctx, {@code @CallbackVar("itemId")} String id) {
 *     // id будет равен "item_52"
 * }
 * </pre>
 *
 * @see CallbackVar
 * @since 1.0.0
 */
@Target(ElementType.METHOD)
@Retention(RetentionPolicy.RUNTIME)
@Documented
public @interface Callback {
    
    /**
     * Шаблон данных callback.
     * Переменные обозначаются как <code>{varName}</code>.
     *
     * @return строка шаблона.
     */
    String value();
}