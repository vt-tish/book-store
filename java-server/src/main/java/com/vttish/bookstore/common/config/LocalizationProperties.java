package com.vttish.bookstore.common.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

import java.util.Set;

@ConfigurationProperties(prefix = "book-store.localization")
public record LocalizationProperties(
        String defaultLanguage,
        Set<String> supportedLanguages
) {
    public String resolveLanguage(String lang) {
        if (lang == null || lang.trim().isEmpty()) {
            return defaultLanguage;
        }

        String normalized = lang.toLowerCase().trim();
        if (supportedLanguages.contains(normalized)) {
            return normalized;
        }

        return defaultLanguage;
    }
}
