package com.vttish.bookstore.auth.service;

import com.vttish.bookstore.auth.dto.*;
import com.vttish.bookstore.auth.entity.User;

public interface AuthService {
    void register(RegisterRequestDto registerRequestDto);
    TokensDto verify(VerifyRequestDto verifyRequestDto);
    void resendVerification(ResendVerificationRequestDto resendVerificationRequestDto);
    TokensDto login(LoginRequestDto loginRequestDto);
    TokensDto refresh(String token);
    void logout(String token);
    void forgotPassword(ForgotPasswordRequestDto forgotPasswordRequestDto);
    void resetPassword(ResetPasswordRequestDto resetPasswordRequestDto);

    User registerEmployee(String email, String password);
    void verifyEmployee(String token);
}
