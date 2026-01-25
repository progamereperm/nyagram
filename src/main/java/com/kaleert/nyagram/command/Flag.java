package com.kaleert.nyagram.command;

import java.lang.annotation.*;

/**
 * Помечает параметр метода как флаг командной строки (булев переключатель).
 * <p>
 * Флаги не зависят от порядка аргументов. Если флаг присутствует в сообщении
 * (например, <code>-f</code> или <code>--force</code>), параметр будет равен {@code true}.
 * </p>
 *
 * @example
 * Сообщение: <code>/delete -f</code>
 * <pre>
 * public void delete(CommandContext ctx, {@code @Flag("f")} boolean force) {
 *     if (force) { ... }
 * }
 * </pre>
 *
 * @since 1.1.0
 */
@Target(ElementType.PARAMETER)
@Retention(RetentionPolicy.RUNTIME)
@Documented
public @interface Flag {
    
    /**
     * Значение флага без префикса.
     * <p>
     * Например, для флага "-s" или "--silent" значением будет "s" или "silent".
     * </p>
     *
     * @return имя флага.
     */
    String value();
}