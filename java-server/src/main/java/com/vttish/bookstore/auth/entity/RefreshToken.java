package com.vttish.bookstore.auth.entity;

import com.vttish.bookstore.auth.entity.enums.RefreshTokenStatus;
import com.vttish.bookstore.common.entity.BaseEntity;
import com.vttish.bookstore.common.exception.IllegalEntityStateException;
import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.Instant;
import java.util.UUID;

@Entity
@Table(name = "refresh_tokens")
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class RefreshToken extends BaseEntity {
    private String token;
    private String childToken;
    private UUID familyId;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id")
    private User user;

    @Enumerated(value = EnumType.STRING)
    private RefreshTokenStatus status = RefreshTokenStatus.ACTIVE;

    private Instant expiresAt;
    private Instant consumedAt;

    public RefreshToken(UUID familyId, String token, User user, Instant expiresAt) {
        this.familyId = familyId;
        this.token = token;
        this.user = user;
        this.expiresAt = expiresAt;
    }

    public RefreshToken(String token, User user, Instant expiresAt) {
        this(UUID.randomUUID(), token, user, expiresAt);
    }

    public boolean isExpired() {
        return expiresAt.isBefore(Instant.now());
    }

    public boolean isActive() {
        return status == RefreshTokenStatus.ACTIVE;
    }

    public boolean isConsumed() {
        return status == RefreshTokenStatus.CONSUMED;
    }

    public boolean isRevoked() {
        return status == RefreshTokenStatus.REVOKED;
    }

    public void consume(String childToken) {
        if (status != RefreshTokenStatus.ACTIVE) {
            throw new IllegalEntityStateException("Cannot consume a token with status " + status);
        }

        status = RefreshTokenStatus.CONSUMED;
        consumedAt = Instant.now();
        this.childToken = childToken;
    }

    public void revoke() {
        status = RefreshTokenStatus.REVOKED;
    }
}
