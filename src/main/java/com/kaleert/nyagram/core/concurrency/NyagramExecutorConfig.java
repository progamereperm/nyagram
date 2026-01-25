package com.kaleert.nyagram.core.concurrency;

import lombok.Builder;
import lombok.Data;

/**
 * Параметры конфигурации для {@link NyagramExecutor}.
 *
 * @since 1.0.0
 */
@Data
@Builder
public class NyagramExecutorConfig {
    
    /** Количество потоков/шардов. */
    @Builder.Default
    private int threadCount = Runtime.getRuntime().availableProcessors() * 2;
    
    /** Префикс имен потоков. */
    @Builder.Default
    private String threadPrefix = "nyagram-worker-";
    
    /** Время жизни простаивающего потока (в мс). */
    @Builder.Default
    private long keepAliveTimeMs = 60000L;
}