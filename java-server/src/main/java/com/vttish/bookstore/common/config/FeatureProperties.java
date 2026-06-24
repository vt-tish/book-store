package com.vttish.bookstore.common.config;

import org.springframework.boot.context.properties.ConfigurationProperties;

@ConfigurationProperties
public record FeatureProperties(boolean enableDevTools) {}
