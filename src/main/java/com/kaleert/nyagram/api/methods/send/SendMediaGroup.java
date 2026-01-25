package com.kaleert.nyagram.api.methods.send;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.kaleert.nyagram.api.exception.TelegramApiRequestException;
import com.kaleert.nyagram.api.exception.TelegramApiValidationException;
import com.kaleert.nyagram.api.meta.BotApiMethod;
import com.kaleert.nyagram.api.meta.MultipartRequest;
import com.kaleert.nyagram.api.objects.InputFile;
import com.kaleert.nyagram.api.objects.media.InputMedia;
import com.kaleert.nyagram.api.objects.media.InputMediaPhoto;
import com.kaleert.nyagram.api.objects.media.InputMediaVideo;
import com.kaleert.nyagram.api.objects.message.Message;
import lombok.*;

import java.io.Serializable;
import java.util.*;

/**
 * Используйте этот метод для отправки группы фотографий, видео или документов как альбома.
 * <p>
 * Документы и аудиофайлы можно группировать только с файлами того же типа.
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
public class SendMediaGroup extends BotApiMethod<List<Message>> implements MultipartRequest {
    
    /** Имя метода в Telegram Bot API. */
    public static final String PATH = "sendMediaGroup";

    /**
     * Уникальный идентификатор чата.
     */
    @JsonProperty("chat_id")
    private String chatId;

    /**
     * Идентификатор топика.
     */
    @JsonProperty("message_thread_id")
    private Integer messageThreadId;

    /**
     * Список медиа-объектов (InputMediaAudio, InputMediaDocument, InputMediaPhoto, InputMediaVideo).
     * От 2 до 10 элементов.
     */
    @JsonProperty("media")
    @Singular("media")
    private List<InputMedia> medias;

    /**
     * Отключить уведомление.
     */
    @JsonProperty("disable_notification")
    private Boolean disableNotification;

    /**
     * Защитить контент.
     */
    @JsonProperty("protect_content")
    private Boolean protectContent;

    /**
     * ID сообщения для ответа.
     */
    @JsonProperty("reply_to_message_id")
    private Integer replyToMessageId;
    
    /**
     * Разрешить отправку без ответа.
     */
    @JsonProperty("allow_sending_without_reply")
    private Boolean allowSendingWithoutReply;

    @Override
    public String getMethod() {
        return PATH;
    }

    @Override
    public List<Message> deserializeResponse(String answer) throws TelegramApiRequestException {
        return deserializeResponseArray(answer, Message.class);
    }

    @Override
    public void validate() throws TelegramApiValidationException {
        if (chatId == null || chatId.isEmpty()) {
            throw new TelegramApiValidationException("ChatId обязателен", PATH, "chat_id");
        }
        if (medias == null || medias.isEmpty()) {
            throw new TelegramApiValidationException("Список медиа не может быть пустым", PATH, "media");
        }
        if (medias.size() < 2 || medias.size() > 10) {
            throw new TelegramApiValidationException("Список медиа должен содержать от 2 до 10 элементов", PATH, "media");
        }
        
        for (InputMedia media : medias) {
            media.validate();
        }
    }
    
    /**
     * Устанавливает уникальный идентификатор чата.
     *
     * @param chatId ID чата (Long).
     * @return текущий билдер.
     */
    public SendMediaGroup setChatId(Long chatId) {
        this.chatId = chatId.toString();
        return this;
    }
    
    /**
     * Добавляет фотографию в альбом.
     *
     * @param file Файл фотографии.
     * @param caption Подпись к фотографии.
     * @return текущий билдер.
     */
    public SendMediaGroup addPhoto(InputFile file, String caption) {
        if (this.medias == null) this.medias = new ArrayList<>();
        this.medias.add(InputMediaPhoto.builder()
                .mediaFile(file)
                .caption(caption)
                .build());
        return this;
    }
    
    /**
     * Добавляет видео в альбом.
     *
     * @param file Файл видео.
     * @param caption Подпись к видео.
     * @return текущий билдер.
     */
    public SendMediaGroup addVideo(InputFile file, String caption) {
        if (this.medias == null) this.medias = new ArrayList<>();
        this.medias.add(InputMediaVideo.builder()
                .mediaFile(file)
                .caption(caption)
                .build());
        return this;
    }
    
    @Override
    public Map<String, InputFile> getFiles() {
        Map<String, InputFile> files = new HashMap<>();
        List<InputMedia> processedList = new ArrayList<>(medias.size());

        for (InputMedia media : medias) {
            InputMedia processed = media;

            InputFile mainFile = media.getMediaFile();
            if (mainFile != null && mainFile.isNew()) {
                String attachName = "file_" + UUID.randomUUID();
                files.put(attachName, mainFile);
                processed = processed.withMedia("attach://" + attachName);
            }

            InputFile thumbFile = media.getThumbnail();
            if (thumbFile != null && thumbFile.isNew()) {
                String thumbName = "thumb_" + UUID.randomUUID();
                files.put(thumbName, thumbFile);
                
                if (processed instanceof InputMediaVideo video) {
                    InputFile ref = new InputFile("attach://" + thumbName);
                    processed = video.withThumbnail(ref);
                }
            }

            processedList.add(processed);
        }

        this.medias = processedList;
        return files;
    }
}