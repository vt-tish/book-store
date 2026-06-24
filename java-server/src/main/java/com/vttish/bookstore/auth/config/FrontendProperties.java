package com.vttish.bookstore.auth.config;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.web.util.UriComponentsBuilder;

@ConfigurationProperties(prefix = "book-store.frontend")
public record FrontendProperties(
        String baseUrl,
        String verifyUri,
        String employeeVerifyUri,
        String resetPasswordUri
) {
    public String getVerifyUrl() {
        return buildUrl(verifyUri);
    }

    public String getEmployeeVerifyUrl() {
        return buildUrl(employeeVerifyUri);
    }

    public String getResetPasswordUrl() {
        return buildUrl(resetPasswordUri);
    }

    private String buildUrl(String path) {
        return UriComponentsBuilder.fromUriString(baseUrl).path(path).toUriString();
    }
}
