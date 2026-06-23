package com.vttish.bookstore.auth.scheduler;

import com.vttish.bookstore.auth.repository.RefreshTokenRepository;
import com.vttish.bookstore.auth.repository.ResetPasswordTokenRepository;
import com.vttish.bookstore.auth.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.time.Instant;
import java.time.temporal.ChronoUnit;

@Component
@RequiredArgsConstructor
public class AuthCleanupScheduler {
    private final RefreshTokenRepository refreshTokenRepository;
    private final ResetPasswordTokenRepository resetPasswordTokenRepository;
    private final UserRepository userRepository;

    @Value("${book-store.scheduler.cleanup.unverified-user-ttl-days}")
    private Long unverifiedUserTtlDays;

    private String ttl;

    @Scheduled(cron = "${book-store.scheduler.cleanup.cron}")
    @Transactional
    public int cleanupRefreshTokens() {
        return refreshTokenRepository.deleteAllExpired();
    }

    @Scheduled(cron = "${book-store.scheduler.cleanup.cron}")
    @Transactional
    public int cleanupUnverifiedUsers() {
        Instant cutoffTime = Instant.now().minus(unverifiedUserTtlDays, ChronoUnit.DAYS);
        return userRepository.deleteUnverifiedOlderThan(cutoffTime);
    }

    @Scheduled(cron = "${book-store.scheduler.cleanup.cron}")
    @Transactional
    public int cleanupResetPasswordTokens() {
        return resetPasswordTokenRepository.deleteAllExpired();
    }
}
