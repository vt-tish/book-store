package com.vttish.bookstore.auth.entity;

import com.vttish.bookstore.common.entity.BaseEntity;
import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.Instant;

@Entity
@Table(name = "refresh_tokens")
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class RefreshToken extends BaseEntity {
    private String token;

    @ManyToOne
    @JoinColumn(name = "user_id")
    User user;

    private Instant expiresAt;
    private boolean isRevoked = false;

    public RefreshToken(String token, User user, Instant expiresAt) {
        this.token = token;
        this.user = user;
        this.expiresAt = expiresAt;
    }

    public void revoke() {
        isRevoked = true;
    }
}
