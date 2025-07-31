package com.glancy.backend.util;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.stream.Stream;

/**
 * Utility to load key-value pairs from a .env file without failing when keys
 * appear multiple times. Later entries override previous ones.
 */
public final class EnvLoader {
    private EnvLoader() {}

    /**
     * Loads environment variables from the given path if it exists.
     * Duplicate keys are resolved by keeping the last value.
     *
     * @param file path to the .env file
     */
    public static void load(Path file) {
        if (!Files.exists(file)) {
            return;
        }
        try (Stream<String> lines = Files.lines(file)) {
            lines.map(String::trim)
                 .filter(line -> !line.isEmpty() && !line.startsWith("#"))
                 .forEach(line -> {
                     int idx = line.indexOf('=');
                     if (idx > 0) {
                         String key = line.substring(0, idx).trim();
                         String value = line.substring(idx + 1).trim();
                         System.setProperty(key, value);
                     }
                 });
        } catch (IOException ignored) {
            // ignore malformed lines and loading errors for developer convenience
        }
    }
}
