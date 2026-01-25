package com.kaleert.nyagram.api.objects.replykeyboard;

import com.kaleert.nyagram.api.meta.BotApiObject;
import com.kaleert.nyagram.api.meta.Validable;

/**
 * Маркерный интерфейс для всех типов клавиатур.
 * <p>
 * Реализуется классами:
 * <ul>
 *     <li>{@link InlineKeyboardMarkup} - клавиатура под сообщением.</li>
 *     <li>{@link ReplyKeyboardMarkup} - кастомная клавиатура вместо системной.</li>
 *     <li>{@link ReplyKeyboardRemove} - удаление клавиатуры.</li>
 *     <li>{@link ForceReply} - принудительный ответ.</li>
 * </ul>
 * </p>
 *
 * @since 1.0.0
 */
public interface ReplyKeyboard extends BotApiObject, Validable {
}