package com.kaleert.nyagram.api.objects.forum;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

/**
 * Перечисление стандартных цветов для иконок топиков форума.
 * <p>
 * Используется при создании или редактировании топика, если не используется кастомный эмодзи.
 * </p>
 *
 * @since 1.0.0
 */
@Getter
@RequiredArgsConstructor
public enum ForumIconColor {
    BLUE(0x6FB9F0),
    YELLOW(0xFFD67E),
    VIOLET(0xCB86DB),
    GREEN(0x8EEE98),
    ROSE(0xFF93B2),
    RED(0xFB6F5F);

    /**
     * RGB значение цвета в формате integer.
     */
    private final int value;
}