package com.glancy.backend.controller;

import jakarta.servlet.http.HttpServletRequest;
import java.util.Locale;
import java.util.Map;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.glancy.backend.dto.LocaleResponse;

/**
 * Provides the user's likely locale based on request headers.
 */
@RestController
@RequestMapping("/api")
public class LocaleController {

    private static final Map<String, String> COUNTRY_TO_LANG = Map.of(
            "CN", "zh",
            "US", "en",
            "GB", "en",
            "DE", "de",
            "FR", "fr",
            "RU", "ru",
            "JP", "ja",
            "ES", "es");

    /**
     * Determine language and country from Accept-Language header.
     */
    @GetMapping("/locale")
    public ResponseEntity<LocaleResponse> getLocale(HttpServletRequest request) {
        String header = request.getHeader("Accept-Language");
        String country = "US";
        String lang = "en";
        if (header != null && !header.isBlank()) {
            String[] parts = header.split(",")[0].split("-");
            lang = parts[0].toLowerCase(Locale.ROOT);
            if (parts.length > 1) {
                country = parts[1].toUpperCase(Locale.ROOT);
            }
        } else {
            Locale loc = request.getLocale();
            if (!loc.getCountry().isEmpty()) {
                country = loc.getCountry();
            }
        }
        lang = COUNTRY_TO_LANG.getOrDefault(country, lang);
        return ResponseEntity.ok(new LocaleResponse(country, lang));
    }
}
