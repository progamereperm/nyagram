package com.kaleert.nyagram.api.methods.groupadministration;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.kaleert.nyagram.api.exception.TelegramApiValidationException;
import com.kaleert.nyagram.api.meta.BotApiMethodBoolean;
import com.kaleert.nyagram.api.objects.ChatPermissions;
import lombok.*;

import java.time.Duration;
import java.time.Instant;

/**
 * Используйте этот метод для ограничения прав пользователя в супергруппе.
 * <p>
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
public class RestrictChatMember extends BotApiMethodBoolean {
    
    /** Имя метода в Telegram Bot API. */
    public static final String PATH = "restrictChatMember";
    
    /**
     * Уникальный идентификатор чата.
     */
    @JsonProperty("chat_id")
    private String chatId;
    
    /**
     * Уникальный идентификатор пользователя.
     */
    @JsonProperty("user_id")
    private Long userId;
    
    /**
     * Объект с новыми правами пользователя.
     */
    @JsonProperty("permissions")
    private ChatPermissions permissions;
    
    /**
     * Дата снятия ограничений (Unix timestamp).
     */
    @JsonProperty("until_date")
    private Integer untilDate;
    
    /**
     * Передайте True, если права должны применяться независимо от настроек чата по умолчанию.
     */
    @JsonProperty("use_independent_chat_permissions")
    private Boolean useIndependentChatPermissions;

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
        if (permissions == null) {
            throw new TelegramApiValidationException("Permissions cannot be null", PATH, "permissions");
        }
    }

    /**
     * Устанавливает ID чата из Long.
     *
     * @param chatId ID чата.
     * @return текущий билдер.
     */
    public RestrictChatMember setChatId(Long chatId) {
        this.chatId = chatId.toString();
        return this;
    }

    /**
     * Делает ограничения бессрочными (навсегда).
     *
     * @return текущий билдер.
     */
    public RestrictChatMember forever() {
        this.untilDate = 0;
        return this;
    }
    
    /**
     * Устанавливает длительность ограничений.
     *
     * @param duration Через какое время ограничения будут сняты.
     * @return текущий билдер.
     */
    public RestrictChatMember forDuration(Duration duration) {
        this.untilDate = (int) (Instant.now().plus(duration).getEpochSecond());
        return this;
    }

    /**
     * Устанавливает точную дату окончания ограничений.
     *
     * @param instant Момент времени, когда ограничения будут сняты.
     * @return текущий билдер.
     */
    public RestrictChatMember until(Instant instant) {
        this.untilDate = (int) instant.getEpochSecond();
        return this;
    }

    /**
     * Статический метод для быстрого создания вечного "мута" (Read-Only).
     *
     * @param chatId ID чата.
     * @param userId ID пользователя.
     * @return готовый объект запроса.
     */
    public static RestrictChatMember muteForever(Long chatId, Long userId) {
        return RestrictChatMember.builder()
                .chatId(chatId.toString())
                .userId(userId)
                .permissions(ChatPermissions.muteAll())
                .untilDate(0)
                .build();
    }

    /**
     * Статический метод для быстрого создания "мута" (запрет на отправку сообщений) на определенное время.
     *
     * @param chatId ID чата.
     * @param userId ID пользователя.
     * @param duration Длительность мута.
     * @return готовый объект запроса.
     */
    public static RestrictChatMember muteFor(Long chatId, Long userId, Duration duration) {
        return RestrictChatMember.builder()
                .chatId(chatId.toString())
                .userId(userId)
                .permissions(ChatPermissions.muteAll())
                .untilDate((int) (Instant.now().plus(duration).getEpochSecond()))
                .build();
    }
}