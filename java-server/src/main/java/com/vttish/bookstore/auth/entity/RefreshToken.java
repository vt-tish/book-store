package com.vttish.bookstore.auth.entity;

import com.vttish.bookstore.auth.entity.enums.RefreshTokenStatus;
import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.Instant;
import java.util.UUID;

@Entity
@Table(name = "refresh_tokens")
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class RefreshToken extends BaseToken {
    private String childToken;
    private UUID familyId;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id")
    private User user;

    @Enumerated(value = EnumType.STRING)
    private RefreshTokenStatus status = RefreshTokenStatus.ACTIVE;

    private Instant consumedAt;

    public RefreshToken(UUID familyId, String token, User user, Instant expiresAt) {
        super(token, expiresAt);
        this.familyId = familyId;
        this.user = user;
    }

    public RefreshToken(String token, User user, Instant expiresAt) {
        this(UUID.randomUUID(), token, user, expiresAt);
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
            throw new IllegalStateException("Cannot consume a token with status " + status);
        }

        status = RefreshTokenStatus.CONSUMED;
        consumedAt = Instant.now();
        this.childToken = childToken;
    }

    public void revoke() {
        status = RefreshTokenStatus.REVOKED;
    }
}
