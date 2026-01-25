package com.kaleert.nyagram.api.objects.inlinequery.inputmessagecontent;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.kaleert.nyagram.api.objects.LinkPreviewOptions;
import com.kaleert.nyagram.api.objects.message.MessageEntity;
import lombok.Builder;

import java.util.List;

/**
 * Представляет содержимое текстового сообщения, которое будет отправлено
 * в результате выбора inline-результата.
 *
 * @param messageText Текст сообщения (1-4096 символов).
 * @param parseMode Режим парсинга (HTML, MarkdownV2).
 * @param entities Список специальных сущностей.
 * @param linkPreviewOptions Настройки превью ссылок.
 *
 * @since 1.0.0
 */
@Builder
@JsonInclude(JsonInclude.Include.NON_NULL)
@JsonIgnoreProperties(ignoreUnknown = true)
public record InputTextMessageContent(
    @JsonProperty("message_text") String messageText,
    @JsonProperty("parse_mode") String parseMode,
    @JsonProperty("entities") List<MessageEntity> entities,
    @JsonProperty("link_preview_options") LinkPreviewOptions linkPreviewOptions
) implements InputMessageContent {
    
    /**
     * Создает текстовое содержимое без разметки.
     * @param text Текст сообщения.
     * @return объект InputTextMessageContent.
     */
    public static InputTextMessageContent of(String text) {
        return InputTextMessageContent.builder().messageText(text).build();
    }
    
    /**
     * Создает текстовое содержимое с HTML разметкой.
     * @param text Текст сообщения.
     * @return объект InputTextMessageContent.
     */
    public static InputTextMessageContent html(String text) {
        return InputTextMessageContent.builder().messageText(text).parseMode("HTML").build();
    }
    
    @Override
    public void validate() {
        if (messageText == null || messageText.isEmpty()) {
            throw new IllegalArgumentException("Message text required");
        }
    }
}