package com.vttish.bookstore.auth.service;

import com.vttish.bookstore.auth.entity.User;
import java.util.UUID;

public interface JwtService {
    String generateAccessToken(User user);
    String generateOpaqueToken();
    UUID extractUserId(String token);
    boolean isTokenValid(String token, User user);
}
