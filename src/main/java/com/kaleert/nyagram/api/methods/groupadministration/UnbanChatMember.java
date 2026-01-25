package com.kaleert.nyagram.api.methods.groupadministration;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.kaleert.nyagram.api.exception.TelegramApiValidationException;
import com.kaleert.nyagram.api.meta.BotApiMethodBoolean;
import lombok.*;

/**
 * Используйте этот метод для разбана ранее забаненного пользователя в супергруппе или канале.
 * <p>
 * Пользователь не вернется в чат автоматически, но сможет вступить через ссылку.
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
public class UnbanChatMember extends BotApiMethodBoolean {
    
    /** Имя метода в Telegram Bot API. */
    public static final String PATH = "unbanChatMember";

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
     * Не делать ничего, если пользователь не забанен.
     */
    @JsonProperty("only_if_banned")
    private Boolean onlyIfBanned;

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
     * Устанавливает флаг {@code only_if_banned}.
     * Если true, метод вернет false, если пользователь не был забанен (вместо ошибки).
     *
     * @return текущий билдер.
     */
    public UnbanChatMember onlyIfBanned() {
        this.onlyIfBanned = true;
        return this;
    }
    
    /**
     * Создает запрос на разбан пользователя.
     *
     * @param chatId ID чата.
     * @param userId ID пользователя.
     * @return готовый объект запроса.
     */
    public static UnbanChatMember of(Long chatId, Long userId) {
        return UnbanChatMember.builder()
                .chatId(chatId.toString())
                .userId(userId)
                .build();
    }
}