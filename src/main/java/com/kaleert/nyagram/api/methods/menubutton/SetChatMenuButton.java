package com.kaleert.nyagram.api.methods.menubutton;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.kaleert.nyagram.api.meta.BotApiMethodBoolean;
import com.kaleert.nyagram.api.objects.menubutton.MenuButton;
import com.kaleert.nyagram.api.objects.menubutton.MenuButtonWebApp;
import com.kaleert.nyagram.api.objects.webapp.WebAppInfo;
import lombok.*;

/**
 * Используйте этот метод для изменения кнопки меню бота в приватном чате или удаления её.
 * <p>
 * Кнопка меню может открывать Web App или список команд.
 * </p>
 *
 * @since 1.0.0
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode(callSuper = false)
public class SetChatMenuButton extends BotApiMethodBoolean {
        
    /** Имя метода в Telegram Bot API. */
    public static final String PATH = "setChatMenuButton";

    /**
     * Уникальный идентификатор чата (опционально).
     * Если не указан, кнопка меняется для всех приватных чатов.
     */
    @JsonProperty("chat_id") 
    private String chatId;
    
    /**
     * Новая кнопка меню.
     */
    @JsonProperty("menu_button") 
    private MenuButton menuButton;

    @Override public String getMethod() { return PATH; }
    
    @Override public void validate() {}
    
    /**
     * Настраивает кнопку меню для открытия Web App.
     *
     * @param text Текст на кнопке.
     * @param url HTTPS ссылка на Web App.
     * @return готовый объект запроса.
     */
    public static SetChatMenuButton toWebApp(String text, String url) {
        return SetChatMenuButton.builder()
                .menuButton(new MenuButtonWebApp(text, new WebAppInfo(url)))
                .build();
    }
}