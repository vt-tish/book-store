package com.vttish.bookstore.auth.service;

import com.vttish.bookstore.auth.dto.AuthResponseDto;
import com.vttish.bookstore.auth.dto.LoginDto;
import com.vttish.bookstore.auth.dto.RegisterDto;
import com.vttish.bookstore.auth.dto.TokensDto;

public interface AuthService {
    TokensDto register(RegisterDto registerDto);
    TokensDto login(LoginDto loginDto);
    AuthResponseDto refresh();
    void logout(String token);
}
