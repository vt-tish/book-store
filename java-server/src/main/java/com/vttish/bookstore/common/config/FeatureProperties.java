package com.vttish.bookstore.common.config;

import org.springframework.boot.context.properties.ConfigurationProperties;

@ConfigurationProperties(prefix = "book-store.features")
public record FeatureProperties(boolean enableDevTools) {}
