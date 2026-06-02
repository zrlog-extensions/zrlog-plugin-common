package com.zrlog.plugin.common;

import java.time.Duration;

public final class PluginExecutionTimeouts {

    public static final int DEFAULT_EXECUTION_TIMEOUT_SECONDS = 600;
    public static final Duration DEFAULT_EXECUTION_TIMEOUT =
            Duration.ofSeconds(DEFAULT_EXECUTION_TIMEOUT_SECONDS);

    private PluginExecutionTimeouts() {
    }

    public static Duration executionTimeout(Integer timeoutSeconds) {
        return executionTimeout(timeoutSeconds, DEFAULT_EXECUTION_TIMEOUT);
    }

    public static Duration executionTimeout(Integer timeoutSeconds, Duration fallback) {
        if (timeoutSeconds == null || timeoutSeconds <= 0) {
            return fallback == null ? DEFAULT_EXECUTION_TIMEOUT : fallback;
        }
        return Duration.ofSeconds(timeoutSeconds);
    }
}
