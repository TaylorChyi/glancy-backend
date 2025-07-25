package com.glancy.backend;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.context.properties.ConfigurationPropertiesScan;
import org.springframework.context.annotation.Bean;
import org.springframework.web.client.RestTemplate;
import lombok.extern.slf4j.Slf4j;

/**
 * Application entry point for the Glancy dictionary backend.
 */
@SpringBootApplication
@ConfigurationPropertiesScan
@Slf4j
public class GlancyBackendApplication {
    /**
     * Bootstraps the Spring application while loading DB credentials
     * from a .env file for convenience during development.
     */
    public static void main(String[] args) {
        io.github.cdimascio.dotenv.Dotenv dotenv =
                io.github.cdimascio.dotenv.Dotenv.configure().ignoreIfMissing().load();
        String dbPassword = dotenv.get("DB_PASSWORD");
        if (dbPassword != null) {
            System.setProperty("DB_PASSWORD", dbPassword);
        }
        String deepseekKey = dotenv.get("thirdparty.deepseek.api-key");
        if (deepseekKey != null) {
            System.setProperty("thirdparty.deepseek.api-key", deepseekKey);
        }
        String ossKey = dotenv.get("OSS_ACCESS_KEY_ID");
        if (ossKey != null) {
            System.setProperty("OSS_ACCESS_KEY_ID", ossKey);
        }
        String ossSecret = dotenv.get("OSS_ACCESS_KEY_SECRET");
        if (ossSecret != null) {
            System.setProperty("OSS_ACCESS_KEY_SECRET", ossSecret);
        }
        SpringApplication.run(GlancyBackendApplication.class, args);
    }

    /**
     * Shared RestTemplate bean for downstream API calls.
     */
    @Bean
    public RestTemplate restTemplate() {
        return new RestTemplate();
    }
}
