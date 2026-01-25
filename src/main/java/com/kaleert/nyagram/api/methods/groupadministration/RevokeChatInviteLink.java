package com.kaleert.nyagram.api.methods.groupadministration;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.kaleert.nyagram.api.exception.TelegramApiRequestException;
import com.kaleert.nyagram.api.exception.TelegramApiValidationException;
import com.kaleert.nyagram.api.meta.BotApiMethod;
import com.kaleert.nyagram.api.objects.ChatInviteLink;
import lombok.*;

/**
 * Используйте этот метод для отзыва пригласительной ссылки.
 * <p>
 * Если ссылка была создана другим администратором, бот должен быть администратором,
 * создавшим эту ссылку, или иметь право {@code can_edit_others_members_rights}.
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
public class RevokeChatInviteLink extends BotApiMethod<ChatInviteLink> {
    
    /** Имя метода в Telegram Bot API. */
    public static final String PATH = "revokeChatInviteLink";
    
    /**
     * Уникальный идентификатор чата.
     */
    @JsonProperty("chat_id")
    private String chatId;
    
    /**
     * Пригласительная ссылка для отзыва.
     */
    @JsonProperty("invite_link")
    private String inviteLink;

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
    }
    
    /**
     * Создает запрос на отзыв пригласительной ссылки.
     *
     * @param chatId ID чата (Long).
     * @param inviteLink Ссылка для отзыва.
     * @return готовый объект запроса.
     */
    public static RevokeChatInviteLink of(Long chatId, String inviteLink) {
        return RevokeChatInviteLink.builder()
                .chatId(chatId.toString())
                .inviteLink(inviteLink)
                .build();
    }
        
    /**
     * Создает запрос на отзыв пригласительной ссылки.
     *
     * @param chatId ID чата или username канала.
     * @param inviteLink Ссылка для отзыва.
     * @return готовый объект запроса.
     */
    public static RevokeChatInviteLink of(String chatId, String inviteLink) {
        return RevokeChatInviteLink.builder()
                .chatId(chatId)
                .inviteLink(inviteLink)
                .build();
    }
}