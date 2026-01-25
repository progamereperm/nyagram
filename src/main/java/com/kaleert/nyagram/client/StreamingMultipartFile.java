package com.kaleert.nyagram.client;

import com.kaleert.nyagram.api.objects.InputFile;
import org.springframework.core.io.InputStreamResource;

import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.UUID;

/**
 * Адаптер для потоковой передачи файлов через Spring {@code RestClient}.
 * <p>
 * Позволяет отправлять файлы (как с диска, так и из InputStream) без предварительной
 * загрузки всего содержимого в оперативную память. Это предотвращает {@code OutOfMemoryError}
 * при работе с большими файлами (видео, аудио).
 * </p>
 *
 * @since 1.0.0
 */
public class StreamingMultipartFile extends InputStreamResource {

    private final String filename;
    private final long contentLength;
    
    /**
     * Создает ресурс для потоковой передачи из объекта {@link InputFile}.
     *
     * @param inputFile Файл-обертка, содержащий либо {@link java.io.File}, либо {@link InputStream}.
     */
    public StreamingMultipartFile(InputFile inputFile) {
        super(resolveStream(inputFile));
        this.filename = resolveName(inputFile);
        this.contentLength = resolveLength(inputFile);
    }

    private static InputStream resolveStream(InputFile file) {
        try {
            if (file.getNewMediaFile() != null) {
                return new FileInputStream(file.getNewMediaFile());
            }
            if (file.getNewMediaStream() != null) {
                return file.getNewMediaStream();
            }
            throw new IllegalArgumentException("InputFile has no content");
        } catch (IOException e) {
            throw new RuntimeException("Failed to open stream", e);
        }
    }

    private static String resolveName(InputFile file) {
        if (file.getMediaName() != null) return file.getMediaName();
        if (file.getNewMediaFile() != null) return file.getNewMediaFile().getName();
        return "file_" + UUID.randomUUID();
    }

    private static long resolveLength(InputFile file) {
        if (file.getNewMediaFile() != null) {
            return file.getNewMediaFile().length();
        }
        return -1; 
    }

    @Override
    public String getFilename() {
        return filename;
    }

    @Override
    public long contentLength() throws IOException {
        return contentLength;
    }
}