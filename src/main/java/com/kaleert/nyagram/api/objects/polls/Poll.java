package com.kaleert.nyagram.api.objects.polls;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.kaleert.nyagram.api.meta.BotApiObject;
import com.kaleert.nyagram.api.objects.message.MessageEntity;

import java.util.List;
import java.util.Optional;

/**
 * Представляет опрос (Poll).
 *
 * @param id Уникальный идентификатор опроса.
 * @param question Вопрос (1-300 символов).
 * @param options Список вариантов ответа.
 * @param totalVoterCount Общее количество проголосовавших.
 * @param isClosed True, если опрос закрыт.
 * @param isAnonymous True, если опрос анонимный.
 * @param type Тип опроса: "regular" или "quiz".
 * @param allowsMultipleAnswers True, если можно выбрать несколько вариантов.
 * @param correctOptionId Идентификатор (индекс 0-based) правильного ответа (для викторин).
 * @param explanation Объяснение, которое показывается при выборе неправильного ответа.
 * @param explanationEntities Сущности в объяснении.
 * @param openPeriod Время жизни опроса в секундах.
 * @param closeDate Дата автоматического закрытия опроса.
 *
 * @since 1.0.0
 */
@JsonIgnoreProperties(ignoreUnknown = true)
public record Poll(
    @JsonProperty("id") String id,
    @JsonProperty("question") String question,
    @JsonProperty("options") List<PollOption> options,
    @JsonProperty("total_voter_count") Integer totalVoterCount,
    @JsonProperty("is_closed") Boolean isClosed,
    @JsonProperty("is_anonymous") Boolean isAnonymous,
    @JsonProperty("type") String type,
    @JsonProperty("allows_multiple_answers") Boolean allowsMultipleAnswers,
    @JsonProperty("correct_option_id") Integer correctOptionId,
    @JsonProperty("explanation") String explanation,
    @JsonProperty("explanation_entities") List<MessageEntity> explanationEntities,
    @JsonProperty("open_period") Integer openPeriod,
    @JsonProperty("close_date") Integer closeDate
) implements BotApiObject {
    
    /**
     * Проверяет, является ли опрос викториной (Quiz).
     * В викторинах есть правильный ответ.
     *
     * @return true, если type="quiz".
     */
    @JsonIgnore
    public boolean isQuiz() {
        return "quiz".equals(type);
    }

    /**
     * Проверяет, является ли опрос обычным (Regular).
     *
     * @return true, если type="regular".
     */
    @JsonIgnore
    public boolean isRegular() {
        return "regular".equals(type);
    }

    /**
     * Находит вариант ответа с наибольшим количеством голосов.
     * @return опциональный вариант ответа.
     */
    @JsonIgnore
    public Optional<PollOption> getWinningOption() {
        if (options == null || options.isEmpty()) return Optional.empty();
        
        return options.stream()
                .max((o1, o2) -> Integer.compare(o1.voterCount(), o2.voterCount()));
    }

    /**
     * Возвращает правильный вариант ответа (только для викторин).
     * @return опциональный вариант ответа.
     */
    @JsonIgnore
    public Optional<PollOption> getCorrectOption() {
        if (!isQuiz() || correctOptionId == null || options == null) return Optional.empty();
        if (correctOptionId >= 0 && correctOptionId < options.size()) {
            return Optional.of(options.get(correctOptionId));
        }
        return Optional.empty();
    }
}