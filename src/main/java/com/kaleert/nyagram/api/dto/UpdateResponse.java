package com.kaleert.nyagram.api.dto;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.Data;
import com.kaleert.nyagram.api.objects.Update;
import java.util.List;

/**
 * DTO для десериализации ответа метода {@code getUpdates}.
 * <p>
 * Содержит список обновлений {@link Update}.
 * </p>
 *
 * @since 1.0.0
 */
@Data
@JsonIgnoreProperties(ignoreUnknown = true)
public class UpdateResponse {
    private Boolean ok;
    private List<Update> result;
    private String description;
    private Integer errorCode;
}