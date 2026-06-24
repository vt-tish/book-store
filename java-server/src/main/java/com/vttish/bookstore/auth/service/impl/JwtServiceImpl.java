package com.vttish.bookstore.auth.service.impl;

import com.vttish.bookstore.auth.config.AuthProperties;
import com.vttish.bookstore.auth.entity.User;
import com.vttish.bookstore.auth.service.JwtService;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import io.jsonwebtoken.io.Decoders;
import io.jsonwebtoken.security.Keys;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.security.Key;
import java.util.Date;
import java.util.List;
import java.util.UUID;
import java.util.function.Function;

@Service
@RequiredArgsConstructor
public class JwtServiceImpl implements JwtService {
    private final AuthProperties authProps;

    @Override
    public String generateAccessToken(User user) {
        return Jwts.builder()
                .setSubject(user.getId().toString())
                .claim("roles", List.of(user.getRole()))
                .setIssuedAt(new Date())
                .setExpiration(new Date(System.currentTimeMillis() + authProps.jwt().accessToken().expirationMs()))
                .signWith(getSignKey(), SignatureAlgorithm.HS256)
                .compact();
    }

    @Override
    public String generateOpaqueToken() {
        return UUID.randomUUID().toString();
    }

    @Override
    public UUID extractUserId(String token) {
        return UUID.fromString(extractClaim(token, Claims::getSubject));
    }

    @Override
    public boolean isTokenValid(String token, User user) {
        return extractUserId(token).equals(user.getId()) && !isTokenExpired(token);
    }

    private boolean isTokenExpired(String token) {
        return extractClaim(token, Claims::getExpiration).before(new Date());
    }

    private <T> T extractClaim(String token, Function<Claims, T> claimsResolver) {
        Claims claims = Jwts.parserBuilder()
                .setSigningKey(getSignKey())
                .build()
                .parseClaimsJws(token)
                .getBody();

        return claimsResolver.apply(claims);
    }

    private Key getSignKey() {
        return Keys.hmacShaKeyFor(Decoders.BASE64.decode(authProps.jwt().accessToken().secretKey()));
    }
}
