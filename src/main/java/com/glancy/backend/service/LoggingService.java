package com.glancy.backend.service;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.logging.LogLevel;
import org.springframework.boot.logging.LoggingSystem;
import org.springframework.stereotype.Service;

/**
 * Allows changing the application's log level at runtime.
 */
@Service
public class LoggingService {
    private final LoggingSystem loggingSystem;
    private final String adminToken;

    public LoggingService(LoggingSystem loggingSystem,
                          @Value("${portal.admin-token:}") String adminToken) {
        this.loggingSystem = loggingSystem;
        this.adminToken = adminToken;
    }

    public boolean isTokenValid(String token) {
        return adminToken != null && !adminToken.isBlank()
                && adminToken.equals(token);
    }

    /**
     * Update the log level for the given logger name.
     *
     * @param loggerName the logger to update (e.g. "com.glancy.backend")
     * @param level the desired log level (e.g. "DEBUG")
     */
    public void setLogLevel(String loggerName, String level) {
        LogLevel target;
        try {
            target = LogLevel.valueOf(level.toUpperCase());
        } catch (IllegalArgumentException ex) {
            throw new IllegalArgumentException("Invalid log level: " + level);
        }
        loggingSystem.setLogLevel(loggerName, target);
    }
}
