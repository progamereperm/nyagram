package com.kaleert.nyagram.fsm;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;
import java.time.LocalDateTime;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * Представляет текущую сессию пользователя в машине состояний (FSM).
 * <p>
 * Хранит текущий этап диалога (state) и произвольные данные, накопленные в процессе общения.
 * Сессии могут сохраняться в памяти, Redis или базе данных в зависимости от конфигурации.
 * </p>
 *
 * @since 1.0.0
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class UserSession implements Serializable {
    
        /** ID пользователя Telegram. */
    private Long userId;
    
    /** ID чата, в котором началась сессия. */
    private Long chatId;
    
    /** Текущее состояние (строковый идентификатор). */
    private String state; 
    
    /** Хранилище данных сессии (Key-Value). */
    private Map<String, Object> data = new ConcurrentHashMap<>();
    
    /** Время создания сессии. */
    private LocalDateTime createdAt;
    
    /** Время последнего обновления (используется для TTL очистки). */
    private LocalDateTime lastUpdatedAt;

    /**
     * Создает новую сессию.
     * 
     * @param userId ID пользователя.
     * @param chatId ID чата.
     */
    public UserSession(Long userId, Long chatId) {
        this.userId = userId;
        this.chatId = chatId;
        this.createdAt = LocalDateTime.now();
        this.lastUpdatedAt = LocalDateTime.now();
        this.data = new ConcurrentHashMap<>();
    }
    
    /**
     * Получает типизированные данные из сессии.
     *
     * @param key Ключ данных.
     * @param type Класс ожидаемого типа данных.
     * @param <T> Тип возвращаемого значения.
     * @return Значение или null, если ключ отсутствует или тип не совпадает.
     */
    public <T> T getData(String key, Class<T> type) {
        Object val = data.get(key);
        if (val == null) return null;
        try {
            if (type == Long.class && val instanceof Integer) {
                return type.cast(((Integer) val).longValue());
            }
            return type.cast(val);
        } catch (ClassCastException e) {
            return null;
        }
    }
    
    /**
     * Сохраняет данные в сессию.
     * Также обновляет время {@code lastUpdatedAt}.
     *
     * @param key Ключ.
     * @param value Значение (должно быть сериализуемым для Redis).
     */
    public void putData(String key, Object value) {
        if (value == null) data.remove(key);
        else data.put(key, value);
        this.lastUpdatedAt = LocalDateTime.now();
    }
}