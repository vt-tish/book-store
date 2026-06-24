package com.vttish.bookstore.auth.scheduler;

import lombok.RequiredArgsConstructor;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class AuthCleanupScheduler {
    private final AuthCleanupTasks authCleanupTasks;

    @Scheduled(cron = "${book-store.scheduler.cleanup.cron}")
    public void cleanupRefreshTokens() {
        authCleanupTasks.cleanupRefreshTokens();
    }

    @Scheduled(cron = "${book-store.scheduler.cleanup.cron}")
    public void cleanupUnverifiedUsers() {
        authCleanupTasks.cleanupUnverifiedUsers();
    }

    @Scheduled(cron = "${book-store.scheduler.cleanup.cron}")
    public void cleanupResetPasswordTokens() {
        authCleanupTasks.cleanupResetPasswordTokens();
    }
}
