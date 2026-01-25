package com.kaleert.nyagram.client;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import org.springframework.core.io.ByteArrayResource;
import org.springframework.core.io.FileSystemResource;
import org.springframework.stereotype.Component;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import com.kaleert.nyagram.api.meta.BotApiMethod;
import com.kaleert.nyagram.api.objects.InputFile;

import java.io.IOException;
import java.io.InputStream;
import java.lang.reflect.Field;
import java.lang.reflect.Modifier;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * Утилита для автоматического преобразования объектов API в Multipart-запросы.
 * <p>
 * Использует рефлексию для поиска полей типа {@link com.kaleert.nyagram.api.objects.InputFile}
 * и добавляет их в тело запроса как файлы, а остальные поля — как текстовые параметры или JSON.
 * </p>
 *
 * @since 1.0.0
 */
@Slf4j
@Component
@RequiredArgsConstructor
public class AutoMultipartBuilder {

    private final ObjectMapper objectMapper;
    private final Map<Class<?>, Field[]> fieldCache = new ConcurrentHashMap<>();
    
    /**
     * Конвертирует объект API метода в карту параметров для multipart-запроса.
     * <p>
     * Проходит по всем полям объекта через рефлексию. Если поле является {@link InputFile} и содержит
     * новый файл или поток, оно добавляется как ресурс. Остальные поля сериализуются в строки или JSON.
     * </p>
     *
     * @param method Объект метода API.
     * @return Карта параметров.
     */
    @SneakyThrows
    public MultiValueMap<String, Object> build(BotApiMethod<?> method) {
        MultiValueMap<String, Object> body = new LinkedMultiValueMap<>();
        Field[] fields = getFields(method.getClass());

        for (Field field : fields) {
            Object value = field.get(method);
            if (value == null) continue;

            String paramName = getParamName(field);

            if (value instanceof InputFile inputFile) {
                handleInputFile(body, paramName, inputFile);
            } else {
                handleRegularField(body, paramName, value);
            }
        }
        return body;
    }

    private void handleInputFile(MultiValueMap<String, Object> body, String paramName, InputFile inputFile) throws IOException {
        if (inputFile.getNewMediaFile() != null) {
            body.add(paramName, new FileSystemResource(inputFile.getNewMediaFile()));
        } else if (inputFile.getNewMediaStream() != null) {
            String filename = inputFile.getMediaName() != null ? inputFile.getMediaName() : "file";
            body.add(paramName, new NamedByteArrayResource(inputFile.getNewMediaStream().readAllBytes(), filename));
        } else if (inputFile.getAttachName() != null) {
            body.add(paramName, inputFile.getAttachName());
        } else {
            body.add(paramName, inputFile.getAttachName()); 
        }
    }

    private void handleRegularField(MultiValueMap<String, Object> body, String paramName, Object value) throws JsonProcessingException {
        if (isPrimitiveOrString(value)) {
            body.add(paramName, value.toString());
        } else {
            body.add(paramName, objectMapper.writeValueAsString(value));
        }
    }

    private String getParamName(Field field) {
        JsonProperty annotation = field.getAnnotation(JsonProperty.class);
        if (annotation != null && !annotation.value().isEmpty()) {
            return annotation.value();
        }
        return toSnakeCase(field.getName());
    }

    private boolean isPrimitiveOrString(Object value) {
        return value instanceof String || 
               value instanceof Number || 
               value instanceof Boolean || 
               value instanceof Enum;
    }

    private String toSnakeCase(String name) {
        return name.replaceAll("([a-z])([A-Z]+)", "$1_$2").toLowerCase();
    }

    private Field[] getFields(Class<?> clazz) {
        return fieldCache.computeIfAbsent(clazz, c -> {
            Field[] fields = c.getDeclaredFields();
            for (Field f : fields) {
                if (!Modifier.isStatic(f.getModifiers())) {
                    f.setAccessible(true);
                }
            }
            return fields;
        });
    }
   
   /**
     * Внутренний класс для передачи байтовых массивов в Spring RestTemplate/Client.
     * Требуется, так как стандартный ByteArrayResource не всегда возвращает имя файла,
     * что критично для multipart-запросов.
     */
    private static class NamedByteArrayResource extends ByteArrayResource {
        private final String filename;
        
        /**
         * Создает ресурс из массива байтов с указанным именем файла.
         *
         * @param content Содержимое файла.
         * @param filename Имя файла (важно для multipart-заголовков).
         */
        public NamedByteArrayResource(byte[] content, String filename) {
            super(content);
            this.filename = filename;
        }

        @Override
        public String getFilename() {
            return filename;
        }
    }
}