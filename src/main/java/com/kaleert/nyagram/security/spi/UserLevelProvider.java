package com.kaleert.nyagram.security.spi;

import com.kaleert.nyagram.api.objects.User;

/**
 * Интерфейс (SPI) для определения уровня доступа пользователя.
 * <p>
 * Реализуйте этот интерфейс, чтобы интегрировать бота с вашей системой ролей.
 * Например, можно возвращать 0 для обычных юзеров, 10 для модераторов и 100 для администраторов.
 * Используется аннотацией {@link com.kaleert.nyagram.security.LevelRequired}.
 * </p>
 *
 * @since 1.0.0
 */
public interface UserLevelProvider {

    /**
     * Возвращает текущий уровень доступа пользователя.
     *
     * @param telegramUser Объект пользователя Telegram.
     * @return Уровень доступа (Integer).
     */
    Integer getUserLevel(User telegramUser);
}
