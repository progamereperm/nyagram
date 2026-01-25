package com.kaleert.nyagram.feature.system;

import com.kaleert.nyagram.api.methods.send.SendChatAction;
import com.kaleert.nyagram.client.NyagramClient;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

import java.util.concurrent.CompletableFuture;
import java.util.concurrent.TimeUnit;
import java.util.function.Supplier;

/**
 * Сервис для управления статусами чата (Chat Action).
 * <p>
 * Позволяет отправлять статусы "печатает..." (typing), "загружает фото..." и т.д.
 * Также умеет поддерживать статус активным в течение длительного времени,
 * автоматически обновляя его каждые 4-5 секунд.
 * </p>
 *
 * @since 1.0.0
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class ChatActionService {

    private final NyagramClient client;
    
    /**
     * Отправляет однократный статус действия.
     * Статус виден пользователю около 5 секунд.
     *
     * @param chatId ID чата.
     * @param action Тип действия (например, "typing").
     */
    public void send(Long chatId, String action) {
        try {
            SendChatAction msg = SendChatAction.builder()
                    .chatId(chatId.toString())
                    .action(action)
                    .build();
            client.execute(msg);
        } catch (Exception e) {
            log.warn("Failed to send chat action '{}' to {}: {}", action, chatId, e.getMessage());
        }
    }
    
    /**
     * Отправляет статус "печатает..." (typing).
     * <p>
     * Сообщает пользователю, что бот готовит текстовый ответ.
     * Статус исчезает автоматически через 5 секунд или при отправке сообщения.
     * </p>
     *
     * @param chatId Уникальный идентификатор чата.
     */
    public void typing(Long chatId) {
        send(chatId, "typing");
    }
    
    /**
     * Отправляет статус "загружает фото..." (upload_photo).
     *
     * @param chatId ID чата.
     */
    public void uploadPhoto(Long chatId) {
        send(chatId, "upload_photo");
    }
    
    /**
     * Отправляет статус "записывает голосовое..." (record_voice).
     *
     * @param chatId ID чата.
     */
    public void recordVoice(Long chatId) {
        send(chatId, "record_voice");
    }
    
    /**
     * Выполняет задачу, поддерживая статус "печатает..." (typing) активным.
     * <p>
     * Автоматически обновляет статус каждые несколько секунд, пока {@code task} выполняется.
     * </p>
     *
     * @param chatId ID чата.
     * @param task Задача, которую нужно выполнить.
     * @param <T> Тип результата задачи.
     * @return Future с результатом задачи.
     */
    @Async
    public <T> CompletableFuture<T> executeWithTyping(Long chatId, Supplier<T> task) {
        return executeWithAction(chatId, "typing", task);
    }
    
    /**
     * Выполняет длительную задачу, поддерживая указанный статус действия активным.
     * <p>
     * Запускает фоновый поток, который периодически отправляет {@code sendChatAction},
     * пока задача {@code task} не завершится.
     * </p>
     *
     * @param chatId ID чата.
     * @param action Тип действия (например, "typing", "upload_photo", "record_voice").
     * @param task Задача (Supplier), которую нужно выполнить.
     * @param <T> Тип возвращаемого значения задачи.
     * @return Future с результатом выполнения задачи.
     */
    public <T> CompletableFuture<T> executeWithAction(Long chatId, String action, Supplier<T> task) {
        return CompletableFuture.supplyAsync(() -> {
            final boolean[] running = {true};
            
            Thread statusThread = new Thread(() -> {
                while (running[0]) {
                    send(chatId, action);
                    try {
                        TimeUnit.SECONDS.sleep(4);
                    } catch (InterruptedException e) {
                        Thread.currentThread().interrupt();
                        break;
                    }
                }
            });
            statusThread.setDaemon(true);
            statusThread.start();

            try {
                return task.get();
            } finally {
                running[0] = false;
                statusThread.interrupt();
            }
        });
    }
}