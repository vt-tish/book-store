package com.vttish.bookstore.auth.entity;

import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.Instant;

@Entity
@Table(name = "reset_password_tokens")
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class ResetPasswordToken extends BaseToken {

    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id")
    private User user;

    public ResetPasswordToken(String token, User user, Instant expiresAt) {
        super(token, expiresAt);
        this.user = user;
    }
}
