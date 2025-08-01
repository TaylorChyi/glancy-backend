package com.glancy.backend.config;

import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.CorsRegistry;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;
import org.springframework.lang.NonNull;
import org.springframework.web.method.support.HandlerMethodArgumentResolver;
import java.util.List;

import com.glancy.backend.config.auth.AuthenticatedUserArgumentResolver;

/**
 * General web configuration including CORS and token authentication.
 */
@Configuration
public class WebConfig implements WebMvcConfigurer {

    private final TokenAuthenticationInterceptor tokenAuthenticationInterceptor;
    private final AuthenticatedUserArgumentResolver authenticatedUserArgumentResolver;

    public WebConfig(TokenAuthenticationInterceptor tokenAuthenticationInterceptor,
                     AuthenticatedUserArgumentResolver authenticatedUserArgumentResolver) {
        this.tokenAuthenticationInterceptor = tokenAuthenticationInterceptor;
        this.authenticatedUserArgumentResolver = authenticatedUserArgumentResolver;
    }

    @Override
    public void addCorsMappings(@NonNull CorsRegistry registry) {
        registry.addMapping("/api/**")
            .allowedOrigins(
                "http://120.26.118.206",
                "http://localhost:8080",
                "http://127.0.0.1:8080"
            )
            .allowedMethods("GET", "POST", "PUT", "DELETE", "OPTIONS")
            .allowedHeaders("*")
            .allowCredentials(true)
            .maxAge(3600);
    }

    @Override
    public void addInterceptors(@NonNull InterceptorRegistry registry) {
        registry.addInterceptor(tokenAuthenticationInterceptor)
            .addPathPatterns("/api/search-records/**", "/api/words");
    }

    @Override
    public void addArgumentResolvers(@NonNull List<HandlerMethodArgumentResolver> resolvers) {
        resolvers.add(authenticatedUserArgumentResolver);
    }
}
