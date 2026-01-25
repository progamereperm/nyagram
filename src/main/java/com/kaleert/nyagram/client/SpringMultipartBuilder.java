package com.kaleert.nyagram.client;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.kaleert.nyagram.api.meta.BotApiMethod;
import com.kaleert.nyagram.api.objects.InputFile;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeanWrapper;
import org.springframework.beans.BeanWrapperImpl;
import org.springframework.context.annotation.Primary;
import org.springframework.stereotype.Component;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;

import java.beans.PropertyDescriptor;
import java.lang.reflect.Field;

/**
 * Простой строитель Multipart-запросов, использующий стандартные механизмы Spring.
 * <p>
 * Использует {@link org.springframework.beans.BeanWrapper} для обхода полей объекта
 * и формирования тела запроса. Подходит для простых методов, не требующих сложной
 * вложенности JSON (как в SendMediaGroup).
 * </p>
 *
 * @since 1.0.0
 */
@Slf4j
@Component
@Primary
@RequiredArgsConstructor
public class SpringMultipartBuilder {

    private final ObjectMapper objectMapper;
    
    /**
     * Строит тело Multipart-запроса на основе объекта метода API.
     * <p>
     * Преобразует поля объекта в параметры формы. Файлы ({@link com.kaleert.nyagram.api.objects.InputFile})
     * добавляются как ресурсы, остальные поля — как строки или JSON.
     * </p>
     *
     * @param method Метод API, который нужно отправить.
     * @return Карта параметров для {@code RestClient}.
     */
    public MultiValueMap<String, Object> build(BotApiMethod<?> method) {
        MultiValueMap<String, Object> body = new LinkedMultiValueMap<>();
        BeanWrapper wrapper = new BeanWrapperImpl(method);

        for (PropertyDescriptor pd : wrapper.getPropertyDescriptors()) {
            String propName = pd.getName();
            
            if ("class".equals(propName) || "method".equals(propName) || "deserializeResponseClass".equals(propName)) {
                continue;
            }

            Object value = wrapper.getPropertyValue(propName);
            if (value == null) continue;

            String jsonName = resolveJsonName(method.getClass(), propName);

            if (value instanceof InputFile inputFile) {
                if (inputFile.isNew()) {
                    body.add(jsonName, new StreamingMultipartFile(inputFile));
                } else {
                    body.add(jsonName, inputFile.getAttachName());
                }
            } else {
                addRegularField(body, jsonName, value);
            }
        }
        return body;
    }

    private String resolveJsonName(Class<?> clazz, String propertyName) {
        try {
            Field field = findField(clazz, propertyName);
            if (field != null && field.isAnnotationPresent(JsonProperty.class)) {
                String val = field.getAnnotation(JsonProperty.class).value();
                if (!val.isEmpty()) return val;
            }
        } catch (Exception ignored) {}
        
        return propertyName; 
    }

    private Field findField(Class<?> clazz, String name) {
        Class<?> searchType = clazz;
        while (searchType != null) {
            try {
                return searchType.getDeclaredField(name);
            } catch (NoSuchFieldException e) {
                searchType = searchType.getSuperclass();
            }
        }
        return null;
    }

    private void addRegularField(MultiValueMap<String, Object> body, String key, Object value) {
        if (value instanceof String || value instanceof Number || value instanceof Boolean || value instanceof Enum) {
            body.add(key, value.toString());
        } else {
            try {
                body.add(key, objectMapper.writeValueAsString(value));
            } catch (JsonProcessingException e) {
                log.error("Failed to serialize field {}", key, e);
            }
        }
    }
}