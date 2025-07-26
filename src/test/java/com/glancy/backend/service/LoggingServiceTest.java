package com.glancy.backend.service;

import io.github.cdimascio.dotenv.Dotenv;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.boot.logging.LogLevel;
import org.springframework.boot.logging.LoggingSystem;

import static org.mockito.Mockito.*;

@SpringBootTest
class LoggingServiceTest {

    @Autowired
    private LoggingService loggingService;

    @MockitoBean
    private LoggingSystem loggingSystem;

    @BeforeAll
    static void loadEnv() {
        Dotenv dotenv = Dotenv.configure().ignoreIfMissing().load();
        String dbPassword = dotenv.get("DB_PASSWORD");
        if (dbPassword != null) {
            System.setProperty("DB_PASSWORD", dbPassword);
        }
    }

    /**
     * 测试 setLogLevelCallsLoggingSystem 接口
     */
    @Test
    void setLogLevelCallsLoggingSystem() {
        loggingService.setLogLevel("com.test", "debug");
        verify(loggingSystem, times(1)).setLogLevel("com.test", LogLevel.DEBUG);
    }
}
