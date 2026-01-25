package com.kaleert.nyagram.feature.files;

import com.kaleert.nyagram.client.NyagramClient;
import com.kaleert.nyagram.core.spi.NyagramBotConfig;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestClient;

import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardCopyOption;
import java.util.concurrent.CompletableFuture;

/**
 * Сервис для работы с файлами Telegram.
 * <p>
 * Упрощает процесс скачивания файлов:
 * 1. Получает объект {@link com.kaleert.nyagram.api.objects.File} по {@code file_id}.
 * 2. Формирует ссылку для скачивания.
 * 3. Загружает поток байтов и сохраняет его на диск.
 * </p>
 *
 * @since 1.0.0
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class FileService {

    private final NyagramClient client;
    private final NyagramBotConfig config;
    private final RestClient rawClient = RestClient.builder().build(); 
    
    /**
     * Асинхронно скачивает файл по его ID и сохраняет в указанное место.
     *
     * @param fileId Идентификатор файла в Telegram.
     * @param destination Путь на локальном диске, куда сохранить файл.
     * @return Future, который завершится путем к сохраненному файлу.
     */
    public CompletableFuture<Path> downloadFile(String fileId, Path destination) {
        return client.getFileAsync(fileId)
                .thenApply(file -> downloadContent(file.filePath(), destination));
    }
    
    /**
     * Скачивает содержимое файла по известному {@code file_path} (из объекта File).
     *
     * @param telegramFilePath Путь файла на сервере Telegram.
     * @param destination Локальный путь назначения.
     * @return Локальный путь к сохраненному файлу.
     * @throws RuntimeException если скачивание не удалось.
     */
    public Path downloadContent(String telegramFilePath, Path destination) {
        String url = String.format("https://api.telegram.org/file/bot%s/%s", 
                config.getBotToken(), telegramFilePath);
        
        try {
            InputStream inputStream = rawClient.get()
                    .uri(url)
                    .retrieve()
                    .body(InputStream.class);

            if (inputStream == null) {
                throw new RuntimeException("Response body is empty");
            }

            Files.createDirectories(destination.getParent());
            
            try (inputStream) {
                Files.copy(inputStream, destination, StandardCopyOption.REPLACE_EXISTING);
            }
            
            return destination;

        } catch (Exception e) {
            log.error("Failed to download file {}", telegramFilePath, e);
            throw new RuntimeException("Download failed", e);
        }
    }
}