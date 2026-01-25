package com.kaleert.nyagram.client;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.kaleert.nyagram.api.meta.BotApiMethod;
import com.kaleert.nyagram.api.methods.send.SendMediaGroup;
import com.kaleert.nyagram.api.methods.send.SendDocument;
import com.kaleert.nyagram.api.methods.send.SendVideo;
import com.kaleert.nyagram.api.methods.send.SendAudio;
import com.kaleert.nyagram.api.objects.InputFile;
import com.kaleert.nyagram.api.objects.media.InputMedia;
import com.kaleert.nyagram.api.objects.media.InputMediaVideo;
import com.kaleert.nyagram.api.objects.media.InputMediaDocument;
import com.kaleert.nyagram.api.objects.media.InputMediaAudio;
import lombok.RequiredArgsConstructor;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Primary;
import org.springframework.stereotype.Component;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;

import java.lang.reflect.Field;
import java.lang.reflect.Modifier;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;

/**
 * Специализированный компонент для построения Multipart-запросов.
 * <p>
 * Умеет обрабатывать сложные сценарии, такие как {@link SendMediaGroup},
 * где файлы должны быть прикреплены как части multipart-запроса, но ссылки на них
 * (attach://) должны быть внутри JSON-структуры.
 * </p>
 *
 * @since 1.0.0
 */
@Slf4j
@Component
@Primary
@RequiredArgsConstructor
public class AdvancedMultipartBuilder {

    private final ObjectMapper objectMapper;
    private final Map<Class<?>, Field[]> fieldCache = new ConcurrentHashMap<>();
    
    /**
     * Преобразует метод API в карту параметров для Multipart-запроса.
     * <p>
     * Особым образом обрабатывает {@link SendMediaGroup}, сериализуя список медиа в JSON,
     * но прикрепляя файлы как отдельные части multipart с уникальными именами (attach://).
     * Для остальных методов делегирует построение стандартной логике.
     * </p>
     *
     * @param method Метод API для отправки.
     * @return Карта параметров (String -> Object/Resource).
     */
    @SneakyThrows
    public MultiValueMap<String, Object> build(BotApiMethod<?> method) {
        if (method instanceof SendMediaGroup mediaGroup) {
            return buildMediaGroup(mediaGroup);
        }
        return buildStandard(method);
    }

    @SneakyThrows
    private MultiValueMap<String, Object> buildStandard(BotApiMethod<?> method) {
        MultiValueMap<String, Object> body = new LinkedMultiValueMap<>();
        Field[] fields = getFields(method.getClass());

        for (Field field : fields) {
            Object value = field.get(method);
            if (value == null) continue;

            String paramName = getParamName(field);

            if (value instanceof InputFile inputFile) {
                if (inputFile.isNew()) {
                    body.add(paramName, new StreamingMultipartFile(inputFile));
                } else {
                    body.add(paramName, inputFile.getAttachName());
                }
            } else {
                addRegularField(body, paramName, value);
            }
        }
        return body;
    }

    @SneakyThrows
    private MultiValueMap<String, Object> buildMediaGroup(SendMediaGroup method) {
        MultiValueMap<String, Object> body = new LinkedMultiValueMap<>();
        
        if (method.getChatId() != null) body.add("chat_id", method.getChatId());
        if (method.getMessageThreadId() != null) body.add("message_thread_id", method.getMessageThreadId());
        if (method.getDisableNotification() != null) body.add("disable_notification", method.getDisableNotification());
        if (method.getProtectContent() != null) body.add("protect_content", method.getProtectContent());
        if (method.getReplyToMessageId() != null) body.add("reply_to_message_id", method.getReplyToMessageId());

        List<InputMedia> originalList = method.getMedias();
        List<InputMedia> processedList = new ArrayList<>(originalList.size());

        for (InputMedia media : originalList) {
            InputMedia processedMedia = media;

            InputFile mainFile = media.getMediaFile();
            if (mainFile != null && mainFile.isNew()) {
                String attachName = "file_" + UUID.randomUUID();
                
                body.add(attachName, new StreamingMultipartFile(mainFile));
                
                processedMedia = processedMedia.withMedia("attach://" + attachName);
            }

            processedMedia = processThumbnail(processedMedia, body);

            processedList.add(processedMedia);
        }

        body.add("media", objectMapper.writeValueAsString(processedList));

        return body;
    }

    private InputMedia processThumbnail(InputMedia media, MultiValueMap<String, Object> body) {
        InputFile thumb = media.getThumbnail();
        
        if (thumb == null || !thumb.isNew()) {
            return media;
        }

        String thumbAttachName = "thumb_" + UUID.randomUUID();
        body.add(thumbAttachName, new StreamingMultipartFile(thumb));
        
        InputFile attachReference = new InputFile("attach://" + thumbAttachName);

        if (media instanceof InputMediaVideo vid) {
            return vid.withThumbnail(attachReference);
        } else if (media instanceof InputMediaDocument doc) {
            return doc; 
        } else if (media instanceof InputMediaAudio aud) {
            return aud;
        }

        return media;
    }

    private void addRegularField(MultiValueMap<String, Object> body, String paramName, Object value) throws JsonProcessingException {
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
        return field.getName().replaceAll("([a-z])([A-Z]+)", "$1_$2").toLowerCase();
    }

    private boolean isPrimitiveOrString(Object value) {
        return value instanceof String || 
               value instanceof Number || 
               value instanceof Boolean || 
               value instanceof Enum;
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
}