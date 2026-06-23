package com.vttish.bookstore.cart.scheduler;

import com.vttish.bookstore.cart.repository.CartRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.time.Instant;
import java.time.temporal.ChronoUnit;

@Component
@RequiredArgsConstructor
public class CartCleanupScheduler {
    private final CartRepository cartRepository;

    @Value("${book-store.scheduler.cleanup.cart-ttl-days}")
    private Long cartTtlDays;

    @Scheduled(cron = "${book-store.scheduler.cleanup.cron}")
    @Transactional
    public Integer cleanupCarts() {
        Instant cutoffTime = Instant.now().minus(cartTtlDays, ChronoUnit.DAYS);
        return cartRepository.deleteOlderThan(cutoffTime);
    }
}
