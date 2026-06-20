package com.vttish.bookstore.auth.service;

import com.vttish.bookstore.auth.dto.*;
import com.vttish.bookstore.common.dto.ResponseDto;

public interface AuthService {
    void register(RegisterDto registerDto);
    TokensDto verify(VerifyRequestDto verifyRequestDto);
    void resendVerification(ResendVerificationRequestDto resendVerificationRequestDto);
    TokensDto login(LoginDto loginDto);
    TokensDto refresh(String token);
    void logout(String token);
    void forgotPassword(ForgotPasswordRequestDto forgotPasswordRequestDto);
    void resetPassword(ResetPasswordRequestDto resetPasswordRequestDto);
}
