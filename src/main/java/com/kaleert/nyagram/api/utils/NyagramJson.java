package com.kaleert.nyagram.api.utils;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;

/**
 * Утилитарный класс для работы с JSON.
 * <p>
 * Предоставляет настроенный экземпляр {@link ObjectMapper}, который используется
 * всей библиотекой для сериализации и десериализации объектов Telegram API.
 * Конфигурация включает поддержку Java 8 Time, игнорирование неизвестных полей и
 * пропуск null-значений.
 * </p>
 *
 * @since 1.0.0
 */
public class NyagramJson {
    
    private static final ObjectMapper MAPPER;

    static {
        MAPPER = new ObjectMapper();
        MAPPER.registerModule(new JavaTimeModule());
        MAPPER.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);
        MAPPER.setSerializationInclusion(JsonInclude.Include.NON_NULL);
        MAPPER.configure(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS, true);
    }
    
    /**
     * Возвращает глобальный экземпляр ObjectMapper.
     * @return ObjectMapper.
     */
    public static ObjectMapper getMapper() {
        return MAPPER;
    }
}