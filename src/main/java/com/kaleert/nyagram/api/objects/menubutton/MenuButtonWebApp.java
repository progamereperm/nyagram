package com.kaleert.nyagram.api.objects.menubutton;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.kaleert.nyagram.api.objects.webapp.WebAppInfo;

/**
 * Кнопка меню, открывающая Web App.
 *
 * @param text Текст на кнопке.
 * @param webApp Информация о Web App.
 *
 * @since 1.0.0
 */
@JsonInclude(JsonInclude.Include.NON_NULL)
public record MenuButtonWebApp(
    @JsonProperty("text") String text,
    @JsonProperty("web_app") WebAppInfo webApp
) implements MenuButton {}