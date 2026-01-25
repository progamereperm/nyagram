package com.kaleert.nyagram.command;

import java.lang.annotation.*;

/**
 * Помечает метод как исполняемый обработчик команды Telegram или текстового сообщения.
 * <p>
 * Методы, помеченные этой аннотацией, должны принимать {@link CommandContext} первым аргументом.
 * Дополнительные аргументы могут быть внедрены с помощью {@link CommandArgument}.
 * </p>
 * 
 * @example
 * <pre>
 * {@code @CommandHandler(value = "/ban", description = "Забанить пользователя")}
 * public void ban(CommandContext ctx, {@code @CommandArgument("username")} String user) {
 *     // логика бана
 * }
 * </pre>
 *
 * @see CommandArgument
 * @see CommandContext
 * @since 1.0.0
 */
@Target(ElementType.METHOD)
@Retention(RetentionPolicy.RUNTIME)
@Documented
public @interface CommandHandler {
    
    /**
     * Основной триггер для этого обработчика.
     * <ul>
     *     <li>Если класс - Контроллер: это полная команда (например, "/start", "Меню").</li>
     *     <li>Если класс - Команда: это подкоманда (например, "add" для "/list add").</li>
     * </ul>
     * Оставьте пустым, чтобы сделать этот метод обработчиком по умолчанию для класса.
     *
     * @return триггер команды.
     */
    String value() default "";
    
    /**
     * Альтернативные триггеры для этой команды.
     * Полезно для локализации (например, {"/help", "помощь"}).
     *
     * @return массив алиасов.
     */
    String[] aliases() default {};
    
    /**
     * Описание команды.
     * Отображается в автоматически генерируемом сообщении помощи.
     *
     * @return текст описания.
     */
    String description() default "";
    
    /**
     * Скрывать ли эту команду из автоматически генерируемого списка /help.
     * Полезно для секретных команд или инструментов только для администраторов.
     *
     * @return true, если команда скрыта.
     */
    boolean hidden() default false;
}