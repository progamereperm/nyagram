package com.kaleert.nyagram.context;

import com.kaleert.nyagram.command.CommandContext;
import lombok.experimental.UtilityClass;

/**
 * Хранилище контекста бота, привязанное к текущему потоку (ThreadLocal).
 * <p>
 * Позволяет получить доступ к {@link CommandContext} из любой точки кода
 * в рамках обработки одного запроса, не передавая объект контекста через параметры методов.
 * Аналогичен {@code SecurityContextHolder} в Spring Security.
 * </p>
 *
 * @since 1.0.0
 */
@UtilityClass
public class BotContextHolder {

    private static final ThreadLocal<CommandContext> holder = new ThreadLocal<>();
    
    /**
     * Устанавливает контекст для текущего потока.
     * @param context Текущий контекст команды.
     */
    public static void setContext(CommandContext context) {
        holder.set(context);
    }
    
    /**
     * Возвращает контекст, привязанный к текущему потоку.
     * @return Контекст или null, если он не был установлен.
     */
    public static CommandContext getContext() {
        return holder.get();
    }
    
    /**
     * Очищает контекст текущего потока.
     * Должен вызываться после завершения обработки запроса для предотвращения утечек памяти.
     */
    public static void clear() {
        holder.remove();
    }
    
    /**
     * Удобный метод для получения ID текущего пользователя.
     * @return ID пользователя или null, если контекста нет.
     */
    public static Long currentUserId() {
        CommandContext ctx = getContext();
        return ctx != null ? ctx.getUserId() : null;
    }
}