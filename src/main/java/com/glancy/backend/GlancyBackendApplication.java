package com.glancy.backend;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;
import org.springframework.web.client.RestTemplate;

/**
 * Application entry point for the Glancy dictionary backend.
 */
@SpringBootApplication
public class GlancyBackendApplication {
    /**
     * Bootstraps the Spring application while loading DB credentials
     * from a .env file for convenience during development.
     */
    public static void main(String[] args) {
        io.github.cdimascio.dotenv.Dotenv dotenv = io.github.cdimascio.dotenv.Dotenv.configure().load();
        System.setProperty("DB_PASSWORD", dotenv.get("DB_PASSWORD"));
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
