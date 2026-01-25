package com.kaleert.nyagram.core.registry;

import com.kaleert.nyagram.api.objects.Update;
import com.kaleert.nyagram.api.objects.User;
import com.kaleert.nyagram.api.objects.message.Message;
import com.kaleert.nyagram.client.NyagramClient;
import com.kaleert.nyagram.command.BotCommand;
import com.kaleert.nyagram.command.CommandArgument;
import com.kaleert.nyagram.command.CommandContext;
import com.kaleert.nyagram.command.CommandHandler;
import com.kaleert.nyagram.core.AsyncMode;
import com.kaleert.nyagram.meta.CommandMeta;
import com.kaleert.nyagram.security.AccessDeniedAction;
import com.kaleert.nyagram.security.LevelRequired;
import com.kaleert.nyagram.security.RateLimit;
import com.kaleert.nyagram.security.RequiresPermission;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.config.BeanPostProcessor;
import org.springframework.core.annotation.AnnotatedElementUtils;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;

import java.lang.invoke.MethodHandle;
import java.lang.invoke.MethodHandles;
import java.lang.reflect.Method;
import java.lang.reflect.Parameter;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.stream.Collectors;

/**
 * Реестр зарегистрированных команд бота.
 * <p>
 * Сканирует бины Spring на наличие аннотаций {@link BotCommand} и {@link CommandHandler}.
 * Сохраняет метаданные команд и предоставляет быстрый поиск нужного обработчика
 * по текстовому триггеру (например, "/start" или "Меню").
 * </p>
 * <p>
 * Поддерживает поиск по принципу "Longest Match First" (наиболее длинное совпадение)
 * для поддержки подкоманд (например, "/settings audio" приоритетнее "/settings").
 * </p>
 *
 * @since 1.0.0
 */
@Slf4j
@Component
@RequiredArgsConstructor
public class CommandRegistry implements BeanPostProcessor {

    @Getter
    private final Map<String, CommandMeta> commandMap = new ConcurrentHashMap<>();
    
    // Кэш отсортированных ключей для быстрого поиска по префиксу (Longest Match First)
    private final List<String> sortedCommandKeys = new ArrayList<>();

    @Override
    public Object postProcessAfterInitialization(Object bean, String beanName) {
        Class<?> beanClass = bean.getClass();
        
        // Снимаем CGLIB прокси-обертку, если она есть, чтобы добраться до аннотаций
        if (beanClass.getName().contains("$$")) {
            beanClass = beanClass.getSuperclass();
        }

        BotCommand classAnn = AnnotatedElementUtils.findMergedAnnotation(beanClass, BotCommand.class);
        if (classAnn == null) {
            return bean;
        }

        // Если value пустое -> режим "Контроллер" (много разных команд в одном классе)
        // Если value есть -> режим "Команда" (корневая команда + подкоманды)
        String rootValue = classAnn.value().trim();
        boolean isControllerMode = !StringUtils.hasText(rootValue);

        // Находим все методы, помеченные @CommandHandler
        List<Method> handlers = Arrays.stream(beanClass.getDeclaredMethods())
                .filter(m -> AnnotatedElementUtils.findMergedAnnotation(m, CommandHandler.class) != null)
                .collect(Collectors.toList());

        if (handlers.isEmpty()) {
            return bean;
        }

        // Валидация для режима "Команда": не должно быть конфликтующих дефолтных хендлеров
        if (!isControllerMode && handlers.size() > 1) {
            long defaultHandlersCount = handlers.stream()
                    .map(m -> AnnotatedElementUtils.findMergedAnnotation(m, CommandHandler.class))
                    .filter(h -> h.value().isEmpty()) // Пустой value = дефолтный обработчик корня
                    .count();
            
            if (defaultHandlersCount > 1) {
                log.warn("⚠️ Class '{}' defines root command '{}' but has multiple default handlers (empty value). Ambiguous mapping.", 
                        beanClass.getSimpleName(), rootValue);
            }
        }

        for (Method method : handlers) {
            CommandHandler methodAnn = AnnotatedElementUtils.findMergedAnnotation(method, CommandHandler.class);
            registerHandler(bean, method, rootValue, methodAnn, isControllerMode);
        }

        // Обновляем ключи поиска
        refreshSearchKeys();
        
        return bean;
    }

    private void registerHandler(Object bean, Method method, String root, CommandHandler handler, boolean isController) {
        String methodValue = handler.value().trim();
        Set<String> triggers = new HashSet<>();

        // 1. Формирование основного пути
        if (isController) {
            // В режиме контроллера метод сам определяет полный путь (например, "/start")
            if (StringUtils.hasText(methodValue)) {
                triggers.add(normalize(methodValue));
            }
        } else {
            // В режиме команды путь строится от корня (например, "/settings" + "group" -> "/settings group")
            triggers.add(buildPath(root, methodValue));
        }

        // 2. Обработка алиасов
        for (String alias : handler.aliases()) {
            if (!StringUtils.hasText(alias)) continue;
            
            if (isController) {
                // В контроллере алиас самостоятелен ("help", "помощь")
                triggers.add(normalize(alias));
            } else {
                // В режиме команды:
                // Если это дефолтный метод (methodValue пуст), то алиас относится к корню ("/rasp", "рп")
                // Если это подкоманда, то алиас относится к ней ("/test broadcast", "/test bc")
                if (methodValue.isEmpty()) {
                    triggers.add(normalize(alias)); 
                } else {
                    triggers.add(buildPath(root, alias));
                }
            }
        }

        // 3. Регистрация всех триггеров
        for (String path : triggers) {
            try {
                // Пытаемся открыть доступ к приватному методу
                if (!method.canAccess(bean)) {
                    method.setAccessible(true);
                }
                
                CommandMeta meta = buildCommandMeta(bean, method, path, handler);
                commandMap.put(path, meta);
                
                log.info("Registered Command: [{}] -> {}#{}", path, bean.getClass().getSimpleName(), method.getName());
            } catch (Exception e) {
                log.error("Failed to register command path: {}", path, e);
            }
        }
    }

    private CommandMeta buildCommandMeta(Object bean, Method method, String fullPath, CommandHandler handler) throws IllegalAccessException {
        MethodHandle methodHandle = MethodHandles.lookup().unreflect(method).bindTo(bean);
        
        // --- 1. Описание ---
        String description = handler.description();
        if (!StringUtils.hasText(description)) {
            // Если у метода нет описания, берем из класса (только если это дефолтный метод корневой команды)
            BotCommand classAnn = bean.getClass().getAnnotation(BotCommand.class);
            if (classAnn != null && StringUtils.hasText(classAnn.description())) {
                description = classAnn.description();
            }
        }

        // --- 2. Права (Permissions) ---
        Set<String> permissions = new HashSet<>();
        RequiresPermission permAnn = AnnotatedElementUtils.findMergedAnnotation(method, RequiresPermission.class);
        if (permAnn != null) {
            permissions.add(permAnn.value());
        }

        // --- 3. Уровень доступа (Level) ---
        CommandMeta.LevelRequirement levelReq;
        LevelRequired levelAnn = AnnotatedElementUtils.findMergedAnnotation(method, LevelRequired.class);
        if (levelAnn != null) {
            levelReq = new CommandMeta.LevelRequirement(levelAnn.min(), levelAnn.max(), levelAnn.deniedAction());
        } else {
            levelReq = new CommandMeta.LevelRequirement(0, Integer.MAX_VALUE, AccessDeniedAction.NOTIFY);
        }

        // --- 4. Rate Limiting ---
        CommandMeta.RateLimitMeta rateLimit = null;
        RateLimit rateLimitAnn = AnnotatedElementUtils.findMergedAnnotation(method, RateLimit.class);
        if (rateLimitAnn != null) {
            rateLimit = new CommandMeta.RateLimitMeta(
                    rateLimitAnn.calls(),
                    rateLimitAnn.timeWindowSec(),
                    rateLimitAnn.type()
            );
        }

        // --- 5. Асинхронность ---
        AsyncMode asyncAnn = AnnotatedElementUtils.findMergedAnnotation(method, AsyncMode.class);
        AsyncMode.Mode asyncMode = (asyncAnn != null) ? asyncAnn.value() : AsyncMode.Mode.SEQUENTIAL;

        // --- 6. Анализ аргументов (Usage Syntax) ---
        Parameter[] parameters = method.getParameters();
        int minArgs = 0;
        int maxArgs = 0;
        StringBuilder usageBuilder = new StringBuilder(fullPath);

        for (int i = 0; i < parameters.length; i++) {
            Parameter param = parameters[i];

            // Пропускаем служебные аргументы
            if (isContextParameter(param.getType())) {
                continue;
            }

            // Обработка VarArgs (String...)
            if (param.isVarArgs()) {
                CommandArgument argAnn = param.getAnnotation(CommandArgument.class);
                String name = (argAnn != null && StringUtils.hasText(argAnn.value())) ? argAnn.value() : "args";
                usageBuilder.append(" [").append(name).append("...]");
                maxArgs = Integer.MAX_VALUE; // Бесконечное кол-во
                break;
            }

            CommandArgument argAnn = param.getAnnotation(CommandArgument.class);
            boolean required = (argAnn == null) || argAnn.required();
            String name = (argAnn != null && StringUtils.hasText(argAnn.value())) ? argAnn.value() : param.getName();

            if (required) {
                minArgs++;
                maxArgs++;
                usageBuilder.append(" <").append(name).append(">");
            } else {
                maxArgs++;
                usageBuilder.append(" [").append(name).append("]");
            }
        }

        return CommandMeta.builder()
                .fullCommandPath(fullPath)
                .handlerInstance(bean)
                .method(method)
                .methodHandle(methodHandle)
                .methodParameters(Arrays.asList(parameters))
                .description(description)
                .requiredPermissions(permissions)
                .levelRequirement(levelReq)
                .rateLimitMeta(rateLimit)
                .minArgs(minArgs)
                .maxArgs(maxArgs)
                .usageSyntax(usageBuilder.toString())
                .asyncMode(asyncMode)
                .build();
    }

    /**
     * Поиск команды по введенному тексту.
     * Использует принцип Longest Match First (наиболее длинное совпадение).
     * @param text Текст сообщения (например, "/test parser url")
     * @return Метаданные команды или null
     */
    public CommandMeta findMatch(String text) {
        if (!StringUtils.hasText(text)) {
            return null;
        }

        String searchKey = text.trim().toLowerCase();

        // 1. Оптимизация: Прямое совпадение
        CommandMeta directMatch = commandMap.get(searchKey);
        if (directMatch != null) {
            return directMatch;
        }

        // 2. Поиск по префиксу с учетом пробела
        // sortedCommandKeys отсортирован от длинных к коротким.
        // Это гарантирует, что "/test parser" найдется раньше, чем "/test".
        for (String key : sortedCommandKeys) {
            // Проверяем startsWith(key + " "), чтобы "/testing" не сработало на "/test"
            if (searchKey.startsWith(key + " ")) {
                return commandMap.get(key);
            }
        }

        return null;
    }

    private void refreshSearchKeys() {
        synchronized (sortedCommandKeys) {
            sortedCommandKeys.clear();
            sortedCommandKeys.addAll(commandMap.keySet());
            // Сортировка: сначала длинные, потом короткие
            // Если длина равна, сортируем по алфавиту
            sortedCommandKeys.sort((k1, k2) -> {
                int lenComp = Integer.compare(k2.length(), k1.length());
                if (lenComp != 0) return lenComp;
                return k1.compareTo(k2);
            });
        }
    }

    private String normalize(String cmd) {
        return cmd.trim().toLowerCase();
    }

    private String buildPath(String root, String sub) {
        if (!StringUtils.hasText(sub)) {
            return normalize(root);
        }
        // Убираем слэши у подкоманд для чистоты (например, root="/test", sub="/run" -> "/test run")
        String cleanSub = sub.trim().replaceAll("^/+", "");
        return (normalize(root) + " " + cleanSub.toLowerCase()).trim();
    }

    private boolean isContextParameter(Class<?> type) {
        return type.isAssignableFrom(CommandContext.class) ||
               type.isAssignableFrom(Update.class) ||
               type.isAssignableFrom(Message.class) ||
               type.isAssignableFrom(User.class) ||
               type.isAssignableFrom(NyagramClient.class);
    }
    
    /**
     * Возвращает список метаданных всех зарегистрированных команд.
     * <p>
     * Используется для генерации меню помощи (/help), логирования или отладки.
     * </p>
     *
     * @return список объектов {@link CommandMeta}.
     */
    public List<CommandMeta> getAllCommands() {
        return new ArrayList<>(commandMap.values());
    }
    
    /**
     * Очищает внутренний кэш ключей поиска команд.
     * <p>
     * Вызывает принудительное обновление списка ключей и их сортировку
     * (по принципу Longest Match First) при следующем запросе команды.
     * Полезно при динамическом добавлении команд в рантайме.
     * </p>
     */
    public void clearCache() {
        refreshSearchKeys();
    }
}