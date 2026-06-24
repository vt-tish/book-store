package com.vttish.bookstore.cart.scheduler;

import lombok.RequiredArgsConstructor;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

@Component
@RequiredArgsConstructor
public class CartCleanupScheduler {
    private final CartCleanupTasks cartCleanupTasks;

    @Scheduled(cron = "${book-store.scheduler.cleanup.cron}")
    @Transactional
    public void cleanupCarts() {
        cartCleanupTasks.cleanupCarts();
    }
}
