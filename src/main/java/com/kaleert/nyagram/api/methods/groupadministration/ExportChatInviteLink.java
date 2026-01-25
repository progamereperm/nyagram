package com.kaleert.nyagram.api.methods.groupadministration;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.kaleert.nyagram.api.exception.TelegramApiRequestException;
import com.kaleert.nyagram.api.exception.TelegramApiValidationException;
import com.kaleert.nyagram.api.meta.BotApiMethod;
import lombok.*;

/**
 * Используйте этот метод для генерации новой основной пригласительной ссылки чата.
 * <p>
 * Старая основная ссылка будет отозвана. Возвращает новую ссылку в виде строки.
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
public class ExportChatInviteLink extends BotApiMethod<String> {
    
    /** Имя метода в Telegram Bot API. */
    public static final String PATH = "exportChatInviteLink";
    
    /**
     * Уникальный идентификатор чата.
     */
    @JsonProperty("chat_id")
    private String chatId;

    @Override
    public String getMethod() {
        return PATH;
    }

    @Override
    public String deserializeResponse(String answer) throws TelegramApiRequestException {
        return deserializeResponse(answer, String.class);
    }

    @Override
    public void validate() throws TelegramApiValidationException {
        if (chatId == null || chatId.isEmpty()) {
            throw new TelegramApiValidationException("ChatId cannot be empty", PATH, "chat_id");
        }
    }
    
    /**
     * Создает запрос на генерацию новой основной ссылки чата.
     *
     * @param chatId ID чата (Long).
     * @return готовый объект запроса.
     */
    public static ExportChatInviteLink of(Long chatId) {
        return ExportChatInviteLink.builder().chatId(chatId.toString()).build();
    }
    
    /**
     * Создает запрос на генерацию новой основной ссылки чата.
     *
     * @param chatId ID чата или username канала.
     * @return готовый объект запроса.
     */
    public static ExportChatInviteLink of(String chatId) {
        return ExportChatInviteLink.builder().chatId(chatId).build();
    }
}