package com.vttish.bookstore.auth.service;

public interface EmailService {
    void sendVerificationEmail(String email, String token);
    void sendEmployeeVerificationEmail(String email, String token);
    void sendResetPasswordEmail(String email, String token);
    void sendSecurityAlert(String email);
}
