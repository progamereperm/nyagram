package com.kaleert.nyagram.api.methods.groupadministration;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.kaleert.nyagram.api.exception.TelegramApiRequestException;
import com.kaleert.nyagram.api.exception.TelegramApiValidationException;
import com.kaleert.nyagram.api.meta.BotApiMethod;
import com.kaleert.nyagram.api.objects.ChatInviteLink;
import lombok.*;

import java.time.Duration;
import java.time.Instant;

/**
 * Используйте этот метод для редактирования дополнительной пригласительной ссылки, созданной ботом.
 * <p>
 * Возвращает отредактированный объект {@link ChatInviteLink}.
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
public class EditChatInviteLink extends BotApiMethod<ChatInviteLink> {
    
    /** Имя метода в Telegram Bot API. */
    public static final String PATH = "editChatInviteLink";
    
    /**
     * Уникальный идентификатор чата.
     */
    @JsonProperty("chat_id")
    private String chatId;
    
    /**
     * Ссылка, которую нужно отредактировать.
     */
    @JsonProperty("invite_link")
    private String inviteLink;
    
    /**
     * Название ссылки (0-32 символа).
     */
    @JsonProperty("name")
    private String name;
    
    /**
     * Дата истечения срока действия (Unix timestamp).
     */
    @JsonProperty("expire_date")
    private Integer expireDate;
    
    /**
     * Максимальное количество пользователей, которые могут присоединиться.
     * (1-99999).
     */
    @JsonProperty("member_limit")
    private Integer memberLimit;
    
    /**
     * Требуется ли одобрение администратора для вступления.
     */
    @JsonProperty("creates_join_request")
    private Boolean createsJoinRequest;

    @Override
    public String getMethod() {
        return PATH;
    }

    @Override
    public ChatInviteLink deserializeResponse(String answer) throws TelegramApiRequestException {
        return deserializeResponse(answer, ChatInviteLink.class);
    }

    @Override
    public void validate() throws TelegramApiValidationException {
        if (chatId == null || chatId.isEmpty()) {
            throw new TelegramApiValidationException("ChatId cannot be empty", PATH, "chat_id");
        }
        if (inviteLink == null || inviteLink.isEmpty()) {
            throw new TelegramApiValidationException("InviteLink cannot be empty", PATH, "invite_link");
        }
        if (name != null && name.length() > 32) {
             throw new TelegramApiValidationException("Name cannot exceed 32 characters", PATH, "name");
        }
        if (Boolean.TRUE.equals(createsJoinRequest) && memberLimit != null) {
             throw new TelegramApiValidationException("Cannot use both member_limit and creates_join_request", PATH);
        }
    }
    
    /**
     * Устанавливает новое время истечения ссылки относительно текущего момента.
     *
     * @param duration Через какое время ссылка истечет.
     * @return текущий билдер.
     */
    public EditChatInviteLink expireIn(Duration duration) {
        this.expireDate = (int) (Instant.now().plus(duration).getEpochSecond());
        return this;
    }

    /**
     * Создает базовый запрос на редактирование ссылки.
     *
     * @param chatId ID чата.
     * @param inviteLink Ссылка, которую нужно отредактировать.
     * @return готовый объект запроса.
     */
    public static EditChatInviteLink of(Long chatId, String inviteLink) {
        return EditChatInviteLink.builder()
                .chatId(chatId.toString())
                .inviteLink(inviteLink)
                .build();
    }
}