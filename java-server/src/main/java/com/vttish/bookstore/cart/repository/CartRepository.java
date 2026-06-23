package com.vttish.bookstore.cart.repository;

import com.vttish.bookstore.cart.entity.Cart;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.time.Instant;
import java.util.Optional;
import java.util.UUID;

public interface CartRepository extends JpaRepository<Cart, UUID> {
    Optional<Cart> findByOwnerId(UUID ownerId);

    @EntityGraph(attributePaths = { "items", "items.book", "items.book.translations" })
    Optional<Cart> findFullByOwnerId(UUID ownerId);

    @Query("SELECT COUNT(c) > 0 FROM Cart c JOIN c.items it WHERE it.book.id = :bookId")
    boolean existsByBookId(@Param("bookId") UUID bookId);

    @Modifying
    @Query("DELETE FROM Cart c WHERE c.updatedAt < :cutoffTime")
    int deleteOlderThan(@Param("cutoffTime") Instant cutoffTime);
}
