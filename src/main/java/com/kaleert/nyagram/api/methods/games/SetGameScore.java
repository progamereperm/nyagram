package com.kaleert.nyagram.api.methods.games;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.kaleert.nyagram.api.exception.TelegramApiRequestException;
import com.kaleert.nyagram.api.exception.TelegramApiValidationException;
import com.kaleert.nyagram.api.meta.BotApiMethod;
import com.kaleert.nyagram.api.objects.message.Message;
import lombok.*;
import java.io.Serializable;

/**
 * Используйте этот метод для установки очков пользователя в игре.
 * <p>
 * При успехе, если сообщение не было отредактировано (очки не изменились), возвращает {@code True}.
 * В противном случае возвращает отредактированное {@link Message}.
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
public class SetGameScore extends BotApiMethod<Serializable> {

    /** Имя метода в Telegram Bot API. */
    public static final String PATH = "setGameScore";
    
    /**
     * Уникальный идентификатор пользователя.
     */
    @JsonProperty("user_id")
    private Long userId;
    
    /**
     * Новое количество очков (должно быть неотрицательным).
     */
    @JsonProperty("score")
    private Integer score;
    
    /**
     * Передайте True, если новый счет может быть меньше текущего.
     * Полезно для исправления ошибок или наказания читеров.
     */
    @JsonProperty("force")
    private Boolean force;
    
    /**
     * Передайте True, если сообщение с игрой не должно обновляться автоматически
     * (не будет надписи "Новый рекорд!").
     */
    @JsonProperty("disable_edit_message")
    private Boolean disableEditMessage;
   
   /**
     * Обязательно, если inline_message_id не указан. Идентификатор чата.
     */
    @JsonProperty("chat_id")
    private String chatId;
    
    /**
     * Обязательно, если inline_message_id не указан. Идентификатор сообщения.
     */
    @JsonProperty("message_id")
    private Integer messageId;
    
    /**
     * Обязательно, если chat_id и message_id не указаны. Идентификатор inline-сообщения.
     */
    @JsonProperty("inline_message_id")
    private String inlineMessageId;

    @Override
    public String getMethod() {
        return PATH;
    }

    @Override
    public Serializable deserializeResponse(String answer) throws TelegramApiRequestException {
         try {
            return deserializeResponse(answer, Message.class);
        } catch (TelegramApiRequestException e) {
            return deserializeResponse(answer, Boolean.class);
        }
    }

    @Override
    public void validate() throws TelegramApiValidationException {
        if (userId == null) throw new TelegramApiValidationException("UserId required", PATH, "user_id");
        if (score == null) throw new TelegramApiValidationException("Score required", PATH, "score");
    }
    
   /**
     * Устанавливает флаг force.
     * Если true, новый счет может быть меньше предыдущего.
     *
     * @return текущий билдер.
     */
    public SetGameScore force() {
        this.force = true;
        return this;
    }
    
    /**
     * Отключает редактирование сообщения с игрой при изменении счета.
     *
     * @return текущий билдер.
     */
    public SetGameScore noEdit() {
        this.disableEditMessage = true;
        return this;
    }
}