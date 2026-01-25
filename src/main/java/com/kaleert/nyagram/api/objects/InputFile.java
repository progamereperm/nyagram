package com.kaleert.nyagram.api.objects;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.kaleert.nyagram.api.exception.TelegramApiValidationException;
import com.kaleert.nyagram.api.meta.BotApiObject;
import com.kaleert.nyagram.api.meta.Validable;
import lombok.*;

import java.io.File;
import java.io.InputStream;

/**
 * Представляет файл, который будет отправлен ботом.
 * <p>
 * Этот класс абстрагирует три способа отправки файла:
 * <ol>
 *     <li>Через {@code file_id} (если файл уже есть на серверах Telegram).</li>
 *     <li>Через HTTP URL (Telegram скачает его сам).</li>
 *     <li>Через загрузку нового файла (Multipart Upload) из {@link java.io.File} или {@link java.io.InputStream}.</li>
 * </ol>
 * </p>
 *
 * @since 1.0.0
 */
@Getter
@Setter
@ToString
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode
@JsonSerialize(using = InputFileSerializer.class)
public class InputFile implements Validable, BotApiObject {
    
    /**
     * Строковое представление файла: либо file_id, либо HTTP URL, либо "attach://name".
     */
    private String attachName;

    /**
     * Имя файла (используется при multipart загрузке).
     */
    @JsonIgnore
    private String mediaName;

    /**
     * Физический файл для загрузки (если используется).
     */
    @JsonIgnore
    private File newMediaFile;

    /**
     * Поток данных для загрузки (если используется).
     */
    @JsonIgnore
    private InputStream newMediaStream;

    /**
     * Флаг, указывающий, является ли это новой загрузкой (true) или ссылкой (false).
     */
    @JsonIgnore
    private boolean isNew;

    /**
     * Создает InputFile из file_id или URL.
     * @param attachName ID файла или ссылка.
     */
    public InputFile(@NonNull String attachName) {
        this.attachName = attachName;
        this.isNew = false;
    }

    /**
     * Создает InputFile для загрузки локального файла.
     * @param mediaFile Файл на диске.
     */
    public InputFile(@NonNull File mediaFile) {
        this.newMediaFile = mediaFile;
        this.mediaName = mediaFile.getName();
        this.attachName = "attach://" + mediaFile.getName();
        this.isNew = true;
    }

    /**
     * Создает InputFile для загрузки локального файла с кастомным именем.
     * @param mediaFile Файл на диске.
     * @param fileName Имя, которое увидит получатель.
     */
    public InputFile(@NonNull File mediaFile, @NonNull String fileName) {
        this.newMediaFile = mediaFile;
        this.mediaName = fileName;
        this.attachName = "attach://" + fileName;
        this.isNew = true;
    }

    /**
     * Создает InputFile для загрузки из InputStream.
     * @param mediaStream Поток данных.
     * @param fileName Имя файла (обязательно для стрима).
     */
    public InputFile(@NonNull InputStream mediaStream, @NonNull String fileName) {
        this.newMediaStream = mediaStream;
        this.mediaName = fileName;
        this.attachName = "attach://" + fileName;
        this.isNew = true;
    }
    
    /**
     * Устанавливает медиа из строки (file_id или URL).
     * Сбрасывает любые ранее установленные файлы или потоки.
     *
     * @param media ID файла или HTTP URL.
     * @return текущий объект для цепочки вызовов.
     */
    public InputFile setMedia(@NonNull String media) {
        this.attachName = media;
        this.isNew = false;
        this.newMediaFile = null;
        this.newMediaStream = null;
        return this;
    }
    
    /**
     * Устанавливает медиа из локального файла.
     *
     * @param mediaFile Файл на диске.
     * @return текущий объект для цепочки вызовов.
     */
    public InputFile setMedia(@NonNull File mediaFile) {
        this.newMediaFile = mediaFile;
        this.mediaName = mediaFile.getName();
        this.attachName = "attach://" + mediaFile.getName();
        this.isNew = true;
        return this;
    }
    
    /**
     * Устанавливает медиа из потока данных.
     *
     * @param mediaStream Поток данных (InputStream).
     * @param fileName Имя файла (обязательно).
     * @return текущий объект для цепочки вызовов.
     */
    public InputFile setMedia(@NonNull InputStream mediaStream, @NonNull String fileName) {
        this.newMediaStream = mediaStream;
        this.mediaName = fileName;
        this.attachName = "attach://" + fileName;
        this.isNew = true;
        return this;
    }

    @Override
    public void validate() throws TelegramApiValidationException {
        if (isNew) {
            if (mediaName == null || mediaName.isEmpty()) {
                throw new TelegramApiValidationException("Media name can't be empty", "InputFile");
            }
            if (newMediaFile == null && newMediaStream == null) {
                throw new TelegramApiValidationException("Media (File or Stream) can't be empty", "InputFile");
            }
        } else {
            if (attachName == null || attachName.isEmpty()) {
                throw new TelegramApiValidationException("File_id or URL can't be empty", "InputFile");
            }
        }
    }
}