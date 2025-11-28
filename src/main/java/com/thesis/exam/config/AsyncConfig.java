package com.thesis.exam.config;

import org.springframework.context.annotation.Configuration;
import org.springframework.scheduling.annotation.EnableAsync;

/**
 * Configuration class to enable asynchronous method execution
 * This allows email sending to run in background threads
 */
@Configuration
@EnableAsync
public class AsyncConfig {
    // Spring will automatically configure a task executor for @Async methods
}
