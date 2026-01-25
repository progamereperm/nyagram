package com.kaleert.nyagram.config;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.jsontype.BasicPolymorphicTypeValidator;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import com.kaleert.nyagram.fsm.UserSession;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.redis.connection.RedisConnectionFactory;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.serializer.GenericJackson2JsonRedisSerializer;
import org.springframework.data.redis.serializer.StringRedisSerializer;

/**
 * Конфигурация интеграции с Redis.
 * <p>
 * Активируется только если свойство {@code nyagram.state-repository.type} установлено в {@code redis}.
 * Настраивает сериализацию сессий пользователей (FSM) в JSON формат.
 * </p>
 *
 * @since 1.0.0
 */
@Configuration
@ConditionalOnProperty(name = "nyagram.state-repository.type", havingValue = "redis")
public class NyagramRedisConfig {
    
    /**
     * Создает и настраивает {@link RedisTemplate} для хранения пользовательских сессий.
     * <p>
     * Ключи сериализуются как строки, значения (UserSession) — как JSON с сохранением информации о типах,
     * что позволяет корректно восстанавливать состояние FSM.
     * </p>
     *
     * @param connectionFactory Фабрика соединений Redis (предоставляется Spring Data Redis).
     * @return Настроенный шаблон Redis.
     */
    @Bean
    public RedisTemplate<String, UserSession> userSessionRedisTemplate(RedisConnectionFactory connectionFactory) {
        RedisTemplate<String, UserSession> template = new UpdateAwareRedisTemplate<>();
        template.setConnectionFactory(connectionFactory);

        ObjectMapper mapper = new ObjectMapper();
        mapper.registerModule(new JavaTimeModule());
        mapper.activateDefaultTyping(
                BasicPolymorphicTypeValidator.builder()
                        .allowIfBaseType(Object.class)
                        .build(),
                ObjectMapper.DefaultTyping.NON_FINAL
        );

        GenericJackson2JsonRedisSerializer serializer = new GenericJackson2JsonRedisSerializer(mapper);

        template.setKeySerializer(new StringRedisSerializer());
        template.setHashKeySerializer(new StringRedisSerializer());
        template.setValueSerializer(serializer);
        template.afterPropertiesSet();

        return template;
    }
    
    /**
     * Специализированный шаблон Redis.
     * <p>
     * В данный момент функционально идентичен стандартному {@link RedisTemplate},
     * но оставлен для возможного расширения логики обработки обновлений в будущем.
     * </p>
     *
     * @param <K> Тип ключа.
     * @param <V> Тип значения.
     */
    private static class UpdateAwareRedisTemplate<K, V> extends RedisTemplate<K, V> {}
}