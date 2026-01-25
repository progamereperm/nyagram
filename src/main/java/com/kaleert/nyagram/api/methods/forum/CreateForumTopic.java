package com.kaleert.nyagram.api.methods.forum;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.kaleert.nyagram.api.exception.TelegramApiRequestException;
import com.kaleert.nyagram.api.exception.TelegramApiValidationException;
import com.kaleert.nyagram.api.meta.BotApiMethod;
import com.kaleert.nyagram.api.objects.forum.ForumIconColor;
import com.kaleert.nyagram.api.objects.forum.ForumTopic;
import lombok.*;

/**
 * Используйте этот метод для создания нового топика в супергруппе-форуме.
 * <p>
 * Возвращает информацию о созданном топике в виде {@link ForumTopic}.
 * Бот должен иметь право {@code can_manage_topics}.
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
public class CreateForumTopic extends BotApiMethod<ForumTopic> {

    /** Имя метода в Telegram Bot API. */
    public static final String PATH = "createForumTopic";

    /**
     * Уникальный идентификатор чата.
     */
    @JsonProperty("chat_id")
    private String chatId;

    /**
     * Название топика (1-128 символов).
     */
    @JsonProperty("name")
    private String name;

    /**
     * Цвет иконки топика (RGB integer).
     * Используйте {@link ForumIconColor} для стандартных цветов.
     */
    @JsonProperty("icon_color")
    private Integer iconColor;

    /**
     * Уникальный идентификатор кастомного эмодзи, который будет иконкой топика.
     */
    @JsonProperty("icon_custom_emoji_id")
    private String iconCustomEmojiId;

    @Override
    public String getMethod() {
        return PATH;
    }

    @Override
    public ForumTopic deserializeResponse(String answer) throws TelegramApiRequestException {
        return deserializeResponse(answer, ForumTopic.class);
    }

    @Override
    public void validate() throws TelegramApiValidationException {
        if (chatId == null || chatId.isEmpty()) {
            throw new TelegramApiValidationException("ChatId cannot be empty", PATH, "chat_id");
        }
        if (name == null || name.isEmpty()) {
            throw new TelegramApiValidationException("Name cannot be empty", PATH, "name");
        }
        if (name.length() > 128) {
             throw new TelegramApiValidationException("Name length must be <= 128 chars", PATH, "name");
        }
    }
    
    /**
     * Устанавливает цвет иконки топика.
     *
     * @param color Цвет из перечисления {@link ForumIconColor}.
     * @return текущий билдер.
     */
    public CreateForumTopic iconColor(ForumIconColor color) {
        this.iconColor = color.getValue();
        return this;
    }
    
    /**
     * Создает запрос на создание нового топика.
     *
     * @param chatId ID чата.
     * @param name Название топика.
     * @return готовый объект запроса.
     */
    public static CreateForumTopic of(Long chatId, String name) {
        return CreateForumTopic.builder()
                .chatId(chatId.toString())
                .name(name)
                .build();
    }
}