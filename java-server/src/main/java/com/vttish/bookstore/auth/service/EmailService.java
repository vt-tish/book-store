package com.vttish.bookstore.auth.service;

public interface EmailService {
    void sendVerificationEmail(String email, String token);
    void sendPasswordResetEmail(String email, String token);
    void sendSecurityAlert(String email);
}
