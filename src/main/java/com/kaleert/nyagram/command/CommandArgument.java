package com.kaleert.nyagram.command;

import java.lang.annotation.*;

/**
 * Указывает, что параметр метода должен быть извлечен из текста команды.
 * <p>
 * Nyagram автоматически парсит текст сообщения, разбивает его на токены (по пробелам,
 * учитывая кавычки) и маппит их на аннотированные параметры.
 * </p>
 * 
 * <h3>Поддерживаемые типы:</h3>
 * <ul>
 *     <li>String (одно слово или "фраза в кавычках")</li>
 *     <li>Integer, Long, Double, Float</li>
 *     <li>Boolean (true/false, yes/no, 1/0, +/-)</li>
 *     <li>Enum константы</li>
 * </ul>
 * 
 * @example
 * Сообщение: <code>/gift @user 100</code>
 * <pre>
 * public void gift(
 *     CommandContext ctx, 
 *     {@code @CommandArgument("target")} String target, // "@user"
 *     {@code @CommandArgument("amount")} int amount     // 100
 * )
 * </pre>
 * 
 * @since 1.0.0
 */
@Target(ElementType.PARAMETER)
@Retention(RetentionPolicy.RUNTIME)
@Documented
public @interface CommandArgument {
    
    /**
     * Имя аргумента.
     * <p>
     * Используется при генерации сообщений об ошибках и синтаксиса в помощи
     * (например, "/cmd &lt;имя_аргумента&gt;").
     * </p>
     *
     * @return имя аргумента.
     */
    String value();
    
    /**
     * Является ли этот аргумент обязательным.
     * <p>
     * Если true и пользователь не предоставил аргумент, выполнение команды прерывается,
     * и пользователю возвращается сообщение об ошибке.
     * </p>
     *
     * @return true, если аргумент обязателен (по умолчанию true).
     */
    boolean required() default true;
    
    /**
     * Имя кастомного бина-резолвера для обработки этого конкретного аргумента.
     * <p>
     * Используется для продвинутых сценариев, когда стандартных конвертеров недостаточно.
     * </p>
     *
     * @return имя бина резолвера или пустая строка.
     */
    String resolverName() default "";
}