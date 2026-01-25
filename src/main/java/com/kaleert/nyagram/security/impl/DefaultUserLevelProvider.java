package com.kaleert.nyagram.security.impl;

import com.kaleert.nyagram.security.spi.UserLevelProvider;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.stereotype.Component;
import com.kaleert.nyagram.api.objects.User;

/**
 * Провайдер уровня доступа по умолчанию.
 * <p>
 * Используется, если разработчик не предоставил собственную реализацию {@link UserLevelProvider}.
 * Всегда возвращает уровень 0, что означает, что все пользователи имеют одинаковые базовые права.
 * </p>
 *
 * @since 1.0.0
 */
@Component
@ConditionalOnMissingBean(UserLevelProvider.class)
public class DefaultUserLevelProvider implements UserLevelProvider {
    @Override
    public Integer getUserLevel(User telegramUser) {
        return 0;
    }
}