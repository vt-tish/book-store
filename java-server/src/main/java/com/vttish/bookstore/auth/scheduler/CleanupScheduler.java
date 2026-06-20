package com.vttish.bookstore.auth.scheduler;

import com.vttish.bookstore.auth.repository.RefreshTokenRepository;
import com.vttish.bookstore.auth.repository.ResetPasswordTokenRepository;
import com.vttish.bookstore.auth.repository.VerifyTokenRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

@Component
@RequiredArgsConstructor
public class CleanupScheduler {
    private final RefreshTokenRepository refreshTokenRepository;
    private final VerifyTokenRepository verifyTokenRepository;
    private final ResetPasswordTokenRepository resetPasswordTokenRepository;

    @Scheduled(cron = "${book-store.scheduler.cleanup-cron}")
    @Transactional
    public int cleanUpRefreshTokens() {
        return refreshTokenRepository.deleteAllExpired();
    }

    @Scheduled(cron = "${book-store.scheduler.cleanup-cron}")
    @Transactional
    public int cleanUpVerifyTokens() {
        return verifyTokenRepository.deleteAllExpired();
    }

    @Scheduled(cron = "${book-store.scheduler.cleanup-cron}")
    @Transactional
    public int cleanUpResetPasswordTokens() {
        return resetPasswordTokenRepository.deleteAllExpired();
    }
}
