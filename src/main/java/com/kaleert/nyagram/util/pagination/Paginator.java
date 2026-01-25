package com.kaleert.nyagram.util.pagination;

import lombok.Builder;
import com.kaleert.nyagram.api.objects.replykeyboard.InlineKeyboardMarkup;
import com.kaleert.nyagram.api.objects.replykeyboard.buttons.InlineKeyboardButton;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Function;

/**
 * Утилита для создания пагинации в сообщениях.
 * <p>
 * Разбивает список элементов на страницы и генерирует текст сообщения с клавиатурой
 * для навигации (кнопки "Вперед"/"Назад").
 * </p>
 *
 * @param <T> Тип элементов в списке.
 * @since 1.0.0
 */
@Builder
public class Paginator<T> {

    private final List<T> items;
    @Builder.Default private final int pageSize = 10;
    @Builder.Default private final String callbackPrefix = "page";
    
    private final Function<T, InlineKeyboardButton> itemMapper; 
    private final Function<T, String> textMapper; 
    
    @Builder.Default private final String header = "";
    @Builder.Default private final String footer = "";
    
    /**
     * Строит результат для конкретной страницы.
     *
     * @param pageNumber Номер страницы (начиная с 1).
     * @return Объект, содержащий текст сообщения и клавиатуру.
     */
    public PageResult build(int pageNumber) {
        if (items.isEmpty()) {
            return new PageResult("Список пуст", InlineKeyboardMarkup.builder().inlineKeyboard(new ArrayList<>()).build());
        }

        int totalPages = (int) Math.ceil((double) items.size() / pageSize);
        int current = Math.max(1, Math.min(pageNumber, totalPages));
        
        int start = (current - 1) * pageSize;
        int end = Math.min(start + pageSize, items.size());
        List<T> pageItems = items.subList(start, end);

        StringBuilder textBuilder = new StringBuilder(header);
        
        List<List<InlineKeyboardButton>> rows = new ArrayList<>();

        if (itemMapper != null) {
            for (T item : pageItems) {
                List<InlineKeyboardButton> row = new ArrayList<>();
                row.add(itemMapper.apply(item));
                rows.add(row);
            }
        } else if (textMapper != null) {
            for (int i = 0; i < pageItems.size(); i++) {
                textBuilder.append(start + i + 1).append(". ")
                           .append(textMapper.apply(pageItems.get(i))).append("\n");
            }
        }
        
        textBuilder.append(footer);
        
        if (totalPages > 1) {
            List<InlineKeyboardButton> navRow = new ArrayList<>();
            if (current > 1) {
                String prevData = String.format("%s:%d", callbackPrefix, current - 1);
                navRow.add(createBtn("⬅️", prevData));
            }
            
            if (current < totalPages) {
                String nextData = String.format("%s:%d", callbackPrefix, current + 1);
                navRow.add(createBtn("➡️", nextData));
            }
            
            if (!navRow.isEmpty()) {
                rows.add(navRow);
            }
        }

        return new PageResult(
            textBuilder.toString(), 
            InlineKeyboardMarkup.builder().inlineKeyboard(rows).build()
        );
    }

    private InlineKeyboardButton createBtn(String text, String data) {
        return InlineKeyboardButton.builder().text(text).callbackData(data).build();
    }
    
    /**
     * Внутренний класс результата пагинации.
     * @param text Текст сообщения для этой страницы.
     * @param keyboard Клавиатура с навигацией.
     */
    public record PageResult(String text, InlineKeyboardMarkup keyboard) {}
}