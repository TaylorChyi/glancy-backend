package com.glancy.backend.service;

import org.springframework.boot.logging.LogLevel;
import org.springframework.boot.logging.LoggingSystem;
import org.springframework.stereotype.Service;

/**
 * Allows changing the application's log level at runtime.
 */
@Service
public class LoggingService {
    private final LoggingSystem loggingSystem;

    public LoggingService(LoggingSystem loggingSystem) {
        this.loggingSystem = loggingSystem;
    }

    /**
     * Update the log level for the given logger name.
     *
     * @param loggerName the logger to update (e.g. "com.glancy.backend")
     * @param level the desired log level (e.g. "DEBUG")
     */
    public void setLogLevel(String loggerName, String level) {
        LogLevel target = LogLevel.valueOf(level.toUpperCase());
        loggingSystem.setLogLevel(loggerName, target);
    }
}
