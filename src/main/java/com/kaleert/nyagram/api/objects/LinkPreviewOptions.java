package com.kaleert.nyagram.api.objects;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.kaleert.nyagram.api.meta.BotApiObject;
import com.kaleert.nyagram.api.meta.Validable;
import com.kaleert.nyagram.api.exception.TelegramApiValidationException;
import lombok.*;

/**
 * Описывает настройки генерации превью ссылок для сообщений.
 *
 * @since 1.0.0
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class LinkPreviewOptions implements BotApiObject, Validable {

    /**
     * True, если превью ссылок должно быть отключено.
     */
    @JsonProperty("is_disabled")
    private Boolean isDisabled;

    /**
     * URL, для которого нужно сгенерировать превью.
     * Если не указан, используется первая найденная ссылка в тексте.
     */
    @JsonProperty("url")
    private String url;

    /**
     * True, если медиа в превью должно быть маленьким (сбоку от текста).
     * Игнорируется, если {@code prefer_large_media} равно True.
     */
    @JsonProperty("prefer_small_media")
    private Boolean preferSmallMedia;

    /**
     * True, если медиа в превью должно быть большим (над текстом).
     */
    @JsonProperty("prefer_large_media")
    private Boolean preferLargeMedia;

    /**
     * True, если превью должно быть показано над текстом сообщения.
     * Иначе оно будет показано под текстом.
     */
    @JsonProperty("show_above_text")
    private Boolean showAboveText;

    @Override
    public void validate() throws TelegramApiValidationException {
    }
}