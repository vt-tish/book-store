package com.vttish.bookstore.auth.scheduler;

import com.vttish.bookstore.auth.repository.RefreshTokenRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

@Component
@RequiredArgsConstructor
public class RefreshTokenCleanupScheduler {
    private final RefreshTokenRepository refreshTokenRepository;

    @Scheduled(cron = "${book-store.security.jwt.refresh-token.cleanup-cron}")
    @Transactional
    public int cleanUpOldTokens() {
        return refreshTokenRepository.deleteAllExpired();
    }
}
