/* nyagram/src/main/java/com/kaleert/nyagram/api/objects/InputFileSerializer.java */

package com.kaleert.nyagram.api.objects;

import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.databind.JsonSerializer;
import com.fasterxml.jackson.databind.SerializerProvider;

import java.io.IOException;

/**
 * Кастомный сериализатор Jackson для {@link InputFile}.
 * <p>
 * Обеспечивает правильную сериализацию объекта в JSON:
 * Если это {@code file_id} или URL, записывается строка {@code attachName}.
 * Если это файл для загрузки (multipart), сериализатор не используется (обрабатывается отдельно в клиенте).
 * </p>
 *
 * @since 1.0.0
 */
public class InputFileSerializer extends JsonSerializer<InputFile> {
    @Override
    public void serialize(InputFile value, JsonGenerator gen, SerializerProvider serializers) throws IOException {
        if (value.getAttachName() != null) {
            gen.writeString(value.getAttachName());
        } else {
            gen.writeNull();
        }
    }
}