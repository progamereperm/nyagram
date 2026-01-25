package com.kaleert.nyagram.command;

import java.lang.annotation.*;
import org.springframework.stereotype.Component;

/**
 * Помечает класс как обработчик команд Telegram (Command Handler).
 * <p>
 * Эта аннотация регистрирует класс как Spring Bean и указывает Nyagram
 * сканировать его на наличие методов, помеченных {@link CommandHandler}.
 * </p>
 *
 * <h2>Режимы использования:</h2>
 * <ul>
 *     <li>
 *         <b>Режим контроллера (пустое значение):</b> Класс действует как контейнер для множества разных команд.
 *         Каждый метод внутри определяет свой собственный путь команды.
 *         <pre>
 *         {@code @BotCommand}
 *         public class MyController {
 *             {@code @CommandHandler("/start")}
 *             public void start(...) {}
 *         }
 *         </pre>
 *     </li>
 *     <li>
 *         <b>Режим одной команды (значение задано):</b> Класс представляет одну корневую команду.
 *         Методы могут определять подкоманды или быть обработчиком по умолчанию.
 *         <pre>
 *         {@code @BotCommand("/settings")}
 *         public class SettingsCommand {
 *             {@code @CommandHandler} // Обрабатывает /settings
 *             public void root(...) {}
 *
 *             {@code @CommandHandler("audio")} // Обрабатывает /settings audio
 *             public void audio(...) {}
 *         }
 *         </pre>
 *     </li>
 * </ul>
 *
 * @see CommandHandler
 * @since 1.0.0
 */
@Component
@Target(ElementType.TYPE)
@Retention(RetentionPolicy.RUNTIME)
@Documented
public @interface BotCommand {
    
    /**
     * Корневой путь команды (например, "/start" или "/settings").
     * <p>
     * Если оставить пустым, класс работает как <b>Контроллер</b>, и пути команд определяются
     * непосредственно над методами через {@link CommandHandler}.
     * </p>
     *
     * @return путь команды или пустая строка.
     */
    String value() default "";
    
    /**
     * Краткое описание того, что делает эта группа команд.
     * Используется в автоматически генерируемом сообщении /help.
     *
     * @return текст описания.
     */
    String description() default "";
    
    /**
     * Категория для группировки команд в меню помощи (например, "Администрирование", "Пользователь").
     *
     * @return название категории.
     */
    String category() default "General";
}
