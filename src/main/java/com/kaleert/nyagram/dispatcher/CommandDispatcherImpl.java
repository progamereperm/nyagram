package com.kaleert.nyagram.dispatcher;

import com.kaleert.nyagram.client.NyagramClient;
import com.kaleert.nyagram.core.ArgumentResolver;
import com.kaleert.nyagram.command.CommandContext;
import com.kaleert.nyagram.core.CommandResult;
import com.kaleert.nyagram.core.concurrency.NyagramExecutor;
import com.kaleert.nyagram.core.registry.CommandRegistry;
import com.kaleert.nyagram.core.resolver.TypedArgumentResolver;
import com.kaleert.nyagram.core.AsyncMode;
import com.kaleert.nyagram.core.spi.MissingArgumentHandler;
import com.kaleert.nyagram.event.BotExecutionErrorEvent;
import com.kaleert.nyagram.exception.ArgumentParseException;
import com.kaleert.nyagram.exception.CommandExecutionException;
import com.kaleert.nyagram.meta.CommandMeta;
import com.kaleert.nyagram.middleware.MdcMiddleware;
import com.kaleert.nyagram.middleware.MiddlewareDispatcher;
import com.kaleert.nyagram.middleware.MiddlewareResult;
import com.kaleert.nyagram.pipeline.CommandPostProcessor;
import com.kaleert.nyagram.pipeline.CommandPreProcessor;
import com.kaleert.nyagram.util.CommandTokenizer;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.stereotype.Component;
import com.kaleert.nyagram.api.methods.send.SendMessage;
import com.kaleert.nyagram.api.objects.message.Message;
import com.kaleert.nyagram.api.objects.Update;
import org.slf4j.MDC;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.lang.invoke.MethodHandles;
import java.lang.reflect.Parameter;
import java.util.*;
import java.util.concurrent.CompletableFuture;
import jakarta.annotation.PostConstruct;

/**
 * Стандартная реализация диспетчера команд.
 * <p>
 * Основные функции:
 * <ul>
 *     <li>Поиск обработчика по тексту сообщения (через {@link CommandRegistry}).</li>
 *     <li>Запуск цепочки Middleware.</li>
 *     <li>Парсинг аргументов из строки и их инъекция в методы контроллеров.</li>
 *     <li>Вызов методов-обработчиков (через Reflection или MethodHandles).</li>
 *     <li>Обработка результатов и исключений.</li>
 * </ul>
 * </p>
 *
 * @since 1.0.0
 */
@Slf4j
@Component
@RequiredArgsConstructor
public class CommandDispatcherImpl implements CommandDispatcher {

    private final CommandRegistry commandRegistry;
    private final Map<Class<?>, ArgumentResolver<?>> argumentResolversMap = new HashMap<>();
    private ArgumentResolver<?> enumResolver;
    private final List<ArgumentResolver<?>> rawResolvers;
    private final List<CommandPreProcessor> preProcessors;
    private final List<CommandPostProcessor> postProcessors;
    private final MiddlewareDispatcher middlewareDispatcher;
    private final NyagramExecutor taskExecutor;
    private final NyagramClient nyagramClient;
    private final ApplicationEventPublisher eventPublisher;
    private CommandMeta fallbackMeta;
    private final MissingArgumentHandler missingArgumentHandler;
    
    /**
     * Инициализирует диспетчер, регистрируя базовые резолверы аргументов.
     */
    @PostConstruct
    public void init() {
        for (ArgumentResolver<?> resolver : rawResolvers) {
            registerResolver(resolver);
        }
        initFallbackMeta();
        log.info("Nyagram Dispatcher initialized with {} type mappings.", argumentResolversMap.size());
    }
    
    private void initFallbackMeta() {
        try {
            Method method = this.getClass().getDeclaredMethod("fallbackHandler");
            fallbackMeta = CommandMeta.builder()
                    .fullCommandPath("fallback")
                    .handlerInstance(this)
                    .method(method)
                    .methodHandle(MethodHandles.lookup().unreflect(method).bindTo(this))
                    .methodParameters(Collections.emptyList())
                    .asyncMode(AsyncMode.Mode.SEQUENTIAL)
                    .requiredPermissions(Collections.emptySet())
                    .build();
        } catch (Exception e) {
            throw new RuntimeException("Failed to init fallback handler", e);
        }
    }

    private CommandResult fallbackHandler() {
        return CommandResult.noResponse();
    }
    
    private void registerResolver(ArgumentResolver<?> resolver) {
        if (resolver instanceof TypedArgumentResolver<?> typedResolver) {
            Set<Class<?>> types = typedResolver.getSupportedTypes();
            
            for (Class<?> type : types) {
                if (type.equals(Enum.class)) {
                    this.enumResolver = resolver;
                } else {
                    argumentResolversMap.put(type, resolver);
                }
            }
            return;
        }

        log.warn("ArgumentResolver {} does not implement TypedArgumentResolver. " +
                 "It will be ignored in fast-path lookup.", resolver.getClass().getSimpleName());
    }
    
    /**
     * Основной метод диспетчеризации.
     * <p>
     * Проверяет, является ли update сообщением, определяет `userId` для шардирования
     * и передает задачу в {@link NyagramExecutor}.
     * </p>
     *
     * @param update Входящее обновление.
     * @return Future с результатом обработки.
     */
    @Override
    public CompletableFuture<CommandResult> dispatch(Update update) {
        if (!isValidCommand(update)) {
            return CompletableFuture.completedFuture(CommandResult.noResponse());
        }

        Long userId = update.getMessage().getFrom().getId();
        CompletableFuture<CommandResult> future = new CompletableFuture<>();

        taskExecutor.execute(userId, () -> {
            try {
                processCommandAsync(update, future);
            } catch (Exception e) {
                log.error("Critical error in command dispatch for user {}", userId, e);
                future.completeExceptionally(e);
            }
        });
        
        return future;
    }

    private boolean isValidCommand(Update update) {
        return update.hasMessage();
    }

    private CommandResult processCommandSync(Update update) {
        Message message = update.getMessage();
        String text = message.hasText() ? message.getText() : null;
        long startTime = System.currentTimeMillis();

        CommandMeta meta = (text != null) ? commandRegistry.findMatch(text) : null;
        if (meta == null) {
            meta = fallbackMeta;
        }
        
        final CommandMeta finalMeta = meta; 

        CommandContext context = new CommandContext(update, nyagramClient);

        if (meta.getAsyncMode() == com.kaleert.nyagram.core.AsyncMode.Mode.CONCURRENT) {
            CompletableFuture.runAsync(() -> executeCommandLogic(context, finalMeta, startTime));
            return CommandResult.noResponse();
        }

        return executeCommandLogic(context, finalMeta, startTime);
    }
    
    private void processCommandAsync(Update update, CompletableFuture<CommandResult> future) {
        Message message = update.getMessage();
        String text = message.hasText() ? message.getText() : null;
        long startTime = System.currentTimeMillis();

        CommandMeta meta = (text != null) ? commandRegistry.findMatch(text) : null;
        
        if (meta == null) {
            meta = fallbackMeta;
        }

        CommandContext context = new CommandContext(update, nyagramClient);
        
        final CommandMeta finalMeta = meta; 

        middlewareDispatcher.dispatch(context, finalMeta)
                .thenCompose(middlewareResult -> {
                    if (!middlewareResult.shouldContinue()) {
                        String msg = middlewareResult.getMessage();
                        return CompletableFuture.completedFuture(
                            msg != null ? CommandResult.success(msg) : CommandResult.noResponse()
                        );
                    }
                    
                    if (middlewareResult.getType() == MiddlewareResult.Type.ERROR) {
                        return CompletableFuture.completedFuture(CommandResult.error(middlewareResult.getMessage()));
                    }

                    if (finalMeta == fallbackMeta) {
                        return CompletableFuture.completedFuture(CommandResult.noResponse());
                    }

                    if (finalMeta.getAsyncMode() == AsyncMode.Mode.CONCURRENT) {
                        return CompletableFuture.supplyAsync(() -> executeCommandLogic(context, finalMeta, startTime));
                    } else {
                        return CompletableFuture.completedFuture(executeCommandLogic(context, finalMeta, startTime));
                    }
                })
                .whenComplete((result, ex) -> {
                    if (ex != null) {
                        handleException(context, finalMeta, ex);
                        future.complete(CommandResult.error(ex.getMessage()));
                    } else {
                        future.complete(result);
                    }
                });
    }

    private CommandResult executeCommandLogic(CommandContext context, CommandMeta meta, long startTime) {
        CommandResult result = null;
        try {
            for (CommandPreProcessor preProcessor : preProcessors) {
                Optional<CommandResult> preResult = preProcessor.process(context, meta);
                if (preResult.isPresent()) {
                    result = preResult.get();
                    sendResult(context, result);
                    return result;
                }
            }

            Object[] args = resolveArguments(meta, context.getText(), context);
            
            Object invocationResult;
            if (meta.getMethodHandle() != null) {
                invocationResult = meta.getMethodHandle().invokeWithArguments(args);
            } else {
                invocationResult = meta.getMethod().invoke(meta.getHandlerInstance(), args);
            }

            result = processInvocationResult(invocationResult);
            sendResult(context, result);

        } catch (InvocationTargetException e) {
            result = handleException(context, meta, e.getTargetException());
        } catch (Throwable e) {
            result = handleException(context, meta, e);
        } finally {
            long duration = System.currentTimeMillis() - startTime;
            final CommandResult finalResult = (result != null) ? result : CommandResult.error("Unknown state");
            
            for (CommandPostProcessor postProcessor : postProcessors) {
                try {
                    postProcessor.process(context, finalResult, duration);
                } catch (Exception e) {
                    log.error("Error in PostProcessor {}", postProcessor.getClass().getSimpleName(), e);
                }
            }
        }
        return result;
    }

    private Object[] resolveArguments(CommandMeta meta, String fullText, CommandContext context) {
        String commandPath = meta.getFullCommandPath();
        String argsString = fullText.length() >= commandPath.length()
                ? fullText.substring(commandPath.length()).trim()
                : "";

        Queue<String> tokens = new ArrayDeque<>(CommandTokenizer.tokenize(argsString));

        List<Parameter> parameters = meta.getMethodParameters();
        Object[] invokeArgs = new Object[parameters.size()];

        for (int i = 0; i < parameters.size(); i++) {
            Parameter param = parameters.get(i);
            
            ArgumentResolver<?> resolver = findResolver(param.getType());

            if (param.isVarArgs()) {
                invokeArgs[i] = resolveVarArgs(param, tokens, context);
                break;
            }

            if (!resolver.isTokenRequired()) {
                invokeArgs[i] = resolver.resolve(context, param, null);
                continue;
            }

            if (param.getType().equals(String.class) && isLastTokenConsumingParam(parameters, i)) {
                if (tokens.isEmpty() && isParameterOptional(param)) {
                    invokeArgs[i] = null;
                } else if (tokens.isEmpty()) {
                    throw new ArgumentParseException("Missing required text argument: " + getParameterName(param));
                } else {
                    invokeArgs[i] = String.join(" ", tokens);
                    tokens.clear();
                }
                continue;
            }

            String token = tokens.poll();
            
            if (token == null) {
                if (isParameterOptional(param)) {
                    invokeArgs[i] = null;
                    continue;
                }
                throw new ArgumentParseException("Missing required argument: " + getParameterName(param));
            }

            invokeArgs[i] = resolver.resolve(context, param, token);
        }
        
        if (!tokens.isEmpty()) {
             throw new ArgumentParseException(
                 String.format("Too many arguments provided. Usage: %s", meta.getUsageSyntax())
             );
        }

        return invokeArgs;
    }
    
    private boolean isLastTokenConsumingParam(List<Parameter> parameters, int currentIndex) {
        for (int i = currentIndex + 1; i < parameters.size(); i++) {
            Class<?> type = parameters.get(i).getType();
            ArgumentResolver<?> resolver = findResolver(type);
            if (resolver.isTokenRequired() && !parameters.get(i).isVarArgs()) {
                return false;
            }
        }
        return true;
    }
    
    private Object resolveVarArgs(Parameter param, Queue<String> tokens, CommandContext context) {
        Class<?> componentType = param.getType().getComponentType();
        ArgumentResolver<?> componentResolver = findResolver(componentType);

        List<Object> varArgsValues = new ArrayList<>();

        while (!tokens.isEmpty()) {
            String token = tokens.poll();
            try {
                varArgsValues.add(componentResolver.resolve(context, param, token));
            } catch (Exception e) {
                throw new ArgumentParseException(
                    String.format("Error parsing varargs element '%s': %s", token, e.getMessage())
                );
            }
        }

        Object array = java.lang.reflect.Array.newInstance(componentType, varArgsValues.size());
        for (int i = 0; i < varArgsValues.size(); i++) {
            java.lang.reflect.Array.set(array, i, varArgsValues.get(i));
        }

        return array;
    }
    
    private Object convertToken(String token, Parameter param, CommandContext ctx) {
        Class<?> type = param.getType();

        ArgumentResolver<?> resolver = argumentResolversMap.get(type);

        if (resolver == null && type.isEnum()) {
            resolver = enumResolver;
        }

        if (resolver == null) {
            log.error("No resolver registered for type: {}", type.getName());
            throw new CommandExecutionException(
                "System Error: Unsupported argument type " + type.getSimpleName(), null
            );
        }

        try {
            return resolver.resolve(ctx, param, token);
        } catch (ArgumentParseException e) {
            throw e;
        } catch (Exception e) {
            throw new ArgumentParseException("Invalid format for " + type.getSimpleName());
        }
    }
    
    private boolean isTypeSupported(ArgumentResolver<?> resolver, Class<?> type) {
        String resolverName = resolver.getClass().getSimpleName();
        if (resolverName.startsWith("Integer") && (type == Integer.class || type == int.class)) return true;
        if (resolverName.startsWith("Long") && (type == Long.class || type == long.class)) return true;
        if (resolverName.startsWith("Double") && (type == Double.class || type == double.class)) return true;
        if (resolverName.startsWith("String") && type == String.class) return true;
        if (resolverName.startsWith("Boolean") && (type == Boolean.class || type == boolean.class)) return true;
        
        return false;
    }
    
    private ArgumentResolver<?> findResolver(Class<?> type) {
        ArgumentResolver<?> resolver = argumentResolversMap.get(type);
        if (resolver == null && type.isEnum()) {
            return enumResolver;
        }
        if (resolver == null) {
            throw new CommandExecutionException("No resolver found for type: " + type.getSimpleName(), null);
        }
        return resolver;
    }
    
    private boolean isParameterOptional(Parameter param) {
        com.kaleert.nyagram.command.CommandArgument argAnn = 
            param.getAnnotation(com.kaleert.nyagram.command.CommandArgument.class);
        return argAnn != null && !argAnn.required();
    }
    
    private String getParameterName(Parameter param) {
        com.kaleert.nyagram.command.CommandArgument argAnn = 
            param.getAnnotation(com.kaleert.nyagram.command.CommandArgument.class);
        return (argAnn != null && !argAnn.value().isEmpty()) ? argAnn.value() : param.getName();
    }

    private String reconstructTail(List<String> tokens, int startIndex) {
        StringBuilder tail = new StringBuilder();
        for (int j = startIndex; j < tokens.size(); j++) {
            if (tail.length() > 0) tail.append(" ");
            tail.append(tokens.get(j));
        }
        return tail.toString();
    }

    private CommandResult processInvocationResult(Object result) {
        if (result instanceof CommandResult cr) {
            return cr;
        } else if (result instanceof String text) {
            return CommandResult.success(text);
        } else if (result == null) {
            return CommandResult.noResponse();
        } else {
            log.warn("Command handler returned unexpected type: {}. Returning as string.",
                    result.getClass().getName());
            return CommandResult.success(result.toString());
        }
    }

    private void sendResult(CommandContext context, CommandResult result) {
        if (result != null && 
            result.getMessage() != null && 
            !result.getMessage().isEmpty() &&
            result.isSuccess()) {
            
            context.reply(result.getMessage());
        }
    }

    private CommandResult handleException(CommandContext context, CommandMeta meta, Throwable e) {
        String traceId = MDC.get(MdcMiddleware.TRACE_ID_KEY);
        if (traceId == null) traceId = "unknown";
    
        if (e instanceof ArgumentParseException) {
            missingArgumentHandler.handle(context, meta, (ArgumentParseException) e);
            return CommandResult.error(e.getMessage());
        }
    
        boolean isBusinessException = e instanceof ArgumentParseException 
                                   || e instanceof com.kaleert.nyagram.exception.NoPermissionException
                                   || e instanceof IllegalArgumentException;
    
        String userMessage;
        
        if (isBusinessException) {
            log.warn("[{}] User input error in {}: {}", traceId, meta.getFullCommandPath(), e.getMessage());
            userMessage = "⚠️ <b>Ошибка:</b> " + e.getMessage();
        } else {
            log.error("[{}] System failure in command {}: {}", traceId, meta.getFullCommandPath(), e.getMessage(), e);
            
            eventPublisher.publishEvent(new BotExecutionErrorEvent(context, e, meta.getFullCommandPath(), traceId));
            
            userMessage = "❌ <b>Внутренняя ошибка.</b>\nID запроса: <code>" + traceId + "</code>";
        }
    
        try {
            SendMessage msg = SendMessage.builder()
                .chatId(context.getChatId().toString())
                .text(userMessage)
                .parseMode("HTML")
                .build();
            nyagramClient.execute(msg);
        } catch (Exception ex) {
            log.warn("[{}] Failed to send error response to user: {}", traceId, ex.getMessage());
        }
        
        return CommandResult.error(e.getMessage());
    }
    
    /**
     * Генерирует текстовую сводку о всех зарегистрированных командах.
     * <p>
     * Формирует отформатированный список путей команд и их описаний.
     * Удобно использовать при старте приложения для вывода в лог.
     * </p>
     *
     * @return Строка с информацией о командах.
     */
    public String getRegisteredCommandsInfo() {
        StringBuilder sb = new StringBuilder();
        sb.append("📋 <b>Registered Commands:</b>\n\n");
        
        List<CommandMeta> allCommands = commandRegistry.getAllCommands();
        allCommands.sort((c1, c2) -> c1.getFullCommandPath().compareTo(c2.getFullCommandPath()));
        
        for (CommandMeta meta : allCommands) {
            sb.append("• <code>").append(meta.getFullCommandPath()).append("</code>\n");
            if (!meta.getDescription().isEmpty()) {
                sb.append("  └ ").append(meta.getDescription()).append("\n");
            }
            sb.append("\n");
        }
        
        sb.append("\nTotal: ").append(allCommands.size()).append(" command(s)");
        
        return sb.toString();
    }
}