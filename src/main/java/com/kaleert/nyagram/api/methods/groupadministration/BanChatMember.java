package com.kaleert.nyagram.api.methods.groupadministration;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.kaleert.nyagram.api.exception.TelegramApiValidationException;
import com.kaleert.nyagram.api.meta.BotApiMethodBoolean;
import lombok.*;

import java.time.Duration;
import java.time.Instant;

/**
 * Используйте этот метод для бана пользователя в группе, супергруппе или канале.
 * <p>
 * В случае с супергруппами и каналами, пользователь не сможет вернуться в чат самостоятельно,
 * используя пригласительные ссылки, пока не будет разбанен.
 * Бот должен быть администратором с правом {@code can_restrict_members}.
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
public class BanChatMember extends BotApiMethodBoolean {
    
    /** Имя метода в Telegram Bot API. */
    public static final String PATH = "banChatMember";
    
    /**
     * Уникальный идентификатор чата или username канала (в формате @channelusername).
     */
    @JsonProperty("chat_id")
    private String chatId;
    
    /**
     * Уникальный идентификатор пользователя, которого нужно забанить.
     */
    @JsonProperty("user_id")
    private Long userId;
    
    /**
     * Дата (Unix timestamp), когда пользователь будет разбанен.
     * <p>
     * Если пользователь забанен более чем на 366 дней или менее чем на 30 секунд
     * от текущего времени, он считается забаненным навсегда.
     * </p>
     */
    @JsonProperty("until_date")
    private Integer untilDate;
    
    /**
     * Передайте true, чтобы удалить все сообщения от этого пользователя в чате.
     * (Только для супергрупп).
     */
    @JsonProperty("revoke_messages")
    private Boolean revokeMessages;

    @Override
    public String getMethod() {
        return PATH;
    }

    @Override
    public void validate() throws TelegramApiValidationException {
        if (chatId == null || chatId.isEmpty()) {
            throw new TelegramApiValidationException("ChatId cannot be empty", PATH, "chat_id");
        }
        if (userId == null || userId == 0) {
            throw new TelegramApiValidationException("UserId cannot be empty", PATH, "user_id");
        }
    }
    
    /**
     * Устанавливает ID чата из Long.
     *
     * @param chatId ID чата.
     * @return текущий билдер.
     */
    public BanChatMember setChatId(Long chatId) {
        this.chatId = chatId.toString();
        return this;
    }
    
    /**
     * Устанавливает срок бана относительно текущего времени.
     *
     * @param duration Длительность бана.
     * @return текущий билдер.
     */
    public BanChatMember forDuration(Duration duration) {
        this.untilDate = (int) (Instant.now().plus(duration).getEpochSecond());
        return this;
    }
    
    /**
     * Устанавливает вечный бан (until_date = 0).
     *
     * @return текущий билдер.
     */
    public BanChatMember forever() {
        this.untilDate = 0;
        return this;
    }
    
    /**
     * Устанавливает флаг удаления сообщений пользователя.
     * Работает только в супергруппах.
     *
     * @return текущий билдер.
     */    
    public BanChatMember revokeMessages() {
        this.revokeMessages = true;
        return this;
    }
    
    /**
     * Создает базовый запрос на бан пользователя.
     *
     * @param chatId ID чата.
     * @param userId ID пользователя.
     * @return готовый объект запроса.
     */    
    public static BanChatMember of(Long chatId, Long userId) {
        return BanChatMember.builder()
                .chatId(chatId.toString())
                .userId(userId)
                .build();
    }
}