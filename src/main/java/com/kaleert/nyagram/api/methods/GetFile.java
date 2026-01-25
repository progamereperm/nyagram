package com.kaleert.nyagram.api.methods;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.kaleert.nyagram.api.exception.TelegramApiRequestException;
import com.kaleert.nyagram.api.exception.TelegramApiValidationException;
import com.kaleert.nyagram.api.meta.BotApiMethod;
import com.kaleert.nyagram.api.objects.File;
import lombok.*;

/**
 * Используйте этот метод для получения основной информации о файле и подготовки его к скачиванию.
 * <p>
 * Возвращает объект {@link File}. Обратите внимание: этот метод не скачивает сам файл,
 * он возвращает путь {@code file_path}, который нужно использовать для скачивания.
 * </p>
 * <p>
 * Боты могут скачивать файлы размером до 20 МБ.
 * </p>
 *
 * @since 1.0.0
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode(callSuper = false)
@JsonIgnoreProperties(ignoreUnknown = true)
@JsonInclude(JsonInclude.Include.NON_NULL)
public class GetFile extends BotApiMethod<File> {

    /** Имя метода в Telegram Bot API. */
    public static final String PATH = "getFile";
    
    /**
     * Идентификатор файла для получения информации.
     */
    @JsonProperty("file_id")
    private String fileId;

    @Override
    public String getMethod() {
        return PATH;
    }

    @Override
    public File deserializeResponse(String answer) throws TelegramApiRequestException {
        return deserializeResponse(answer, File.class);
    }

    @Override
    public void validate() throws TelegramApiValidationException {
        if (fileId == null || fileId.isEmpty()) {
            throw new TelegramApiValidationException("FileId cannot be empty", PATH, "file_id");
        }
    }
    
    /**
     * Создает запрос на получение файла.
     * @param fileId ID файла.
     * @return объект запроса.
     */
    public static GetFile of(String fileId) {
        return new GetFile(fileId);
    }
}