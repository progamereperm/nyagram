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
 * Используйте этот метод для создания дополнительной пригласительной ссылки для чата.
 * <p>
 * Ссылку можно сделать ограниченной по времени или количеству использований.
 * Возвращает объект {@link ChatInviteLink}.
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
public class CreateChatInviteLink extends BotApiMethod<ChatInviteLink> {
    
    /** Имя метода в Telegram Bot API. */
    public static final String PATH = "createChatInviteLink";

    /**
     * Уникальный идентификатор чата.
     */
    @JsonProperty("chat_id")
    private String chatId;

    /**
     * Название ссылки (для удобства администраторов).
     */
    @JsonProperty("name")
    private String name;

    /**
     * Дата истечения срока действия (Unix timestamp).
     */
    @JsonProperty("expire_date")
    private Integer expireDate;

    /**
     * Максимальное количество пользователей, которые могут вступить по ссылке (1-99999).
     */
    @JsonProperty("member_limit")
    private Integer memberLimit;

    /**
     * Если true, пользователи должны быть одобрены администратором.
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
        if (name != null && name.length() > 32) {
            throw new TelegramApiValidationException("Name cannot exceed 32 characters", PATH, "name");
        }
        if (Boolean.TRUE.equals(createsJoinRequest) && memberLimit != null) {
            throw new TelegramApiValidationException("Cannot use both member_limit and creates_join_request", PATH);
        }
        if (memberLimit != null && (memberLimit < 1 || memberLimit > 99999)) {
            throw new TelegramApiValidationException("Member limit must be between 1 and 99999", PATH, "member_limit");
        }
    }
    
    /**
     * Устанавливает ID чата из Long.
     *
     * @param chatId ID чата.
     * @return текущий билдер.
     */
    public CreateChatInviteLink setChatId(Long chatId) {
        this.chatId = chatId.toString();
        return this;
    }
    
    /**
     * Устанавливает время истечения ссылки относительно текущего момента.
     *
     * @param duration Через какое время ссылка истечет.
     * @return текущий билдер.
     */
    public CreateChatInviteLink expireIn(Duration duration) {
        this.expireDate = (int) (Instant.now().plus(duration).getEpochSecond());
        return this;
    }
    
    /**
     * Делает ссылку одноразовой (максимум 1 вступление).
     *
     * @return текущий билдер.
     */
    public CreateChatInviteLink oneTime() {
        this.memberLimit = 1;
        return this;
    }

    /**
     * Включает режим одобрения заявок (Join Request).
     * <p>
     * Пользователи не вступят в чат автоматически, администратор должен будет их одобрить.
     * При использовании этого флага {@code member_limit} сбрасывается.
     * </p>
     *
     * @return текущий билдер.
     */
    public CreateChatInviteLink withApproval() {
        this.createsJoinRequest = true;
        this.memberLimit = null;
        return this;
    }
}