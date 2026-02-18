package com.interview.platform.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.scheduling.annotation.EnableAsync;
import org.springframework.scheduling.annotation.EnableScheduling;

import java.util.concurrent.Executor;
import java.util.concurrent.Executors;

/**
 * Async configuration using Java 21 Virtual Threads.
 *
 * <p>Previous implementation used a {@code ThreadPoolTaskExecutor} with
 * core=2, max=5, queue=25. This was a severe bottleneck: each avatar
 * video generation blocks for up to 180 seconds (60 polls × 3s sleep),
 * so a single interview (10 questions) would saturate the pool and
 * force all other interviews to queue or get rejected.</p>
 *
 * <p>With virtual threads, every {@code Thread.sleep()} in polling loops
 * (D-ID, AssemblyAI) yields the underlying carrier thread back to the
 * pool. Thousands of concurrent polling operations can run without
 * consuming platform thread resources.</p>
 *
 * <p><strong>Important:</strong> Virtual threads work correctly with
 * {@code spring.threads.virtual.enabled=true} in application.properties,
 * which also configures Tomcat to use virtual threads for HTTP request
 * handling. The executors defined here are for {@code @Async} methods
 * specifically.</p>
 *
 * <p><strong>HikariCP Warning:</strong> Because virtual threads can spawn
 * many concurrent DB operations, the connection pool must be sized
 * appropriately. See {@code spring.datasource.hikari.maximum-pool-size}
 * in application.properties.</p>
 */
@Configuration
@EnableAsync
@EnableScheduling
public class AsyncConfig {

    /**
     * Executor for avatar video generation, transcription, and feedback tasks.
     *
     * <p>Each task gets its own virtual thread. No pool sizing, no queue
     * capacity limits, no thread starvation. The JVM manages carrier
     * threads automatically (typically pinned to available CPU cores).</p>
     */
    @Bean(name = "avatarTaskExecutor")
    public Executor avatarTaskExecutor() {
        return Executors.newVirtualThreadPerTaskExecutor();
    }

    /**
     * General-purpose executor for lightweight async operations
     * (e.g., SSE event dispatch, cache warming, scheduled recovery).
     */
    @Bean(name = "generalTaskExecutor")
    public Executor generalTaskExecutor() {
        return Executors.newVirtualThreadPerTaskExecutor();
    }
}
