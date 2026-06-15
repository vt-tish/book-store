package com.vttish.bookstore.cart.repository;

import com.vttish.bookstore.cart.entity.Cart;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;
import java.util.UUID;

public interface CartRepository extends JpaRepository<Cart, UUID> {
    Optional<Cart> findByOwnerId(UUID ownerId);
}
