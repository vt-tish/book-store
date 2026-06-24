package com.vttish.bookstore.cart.repository;

import com.vttish.bookstore.cart.entity.CartItem;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.UUID;

public interface CartItemRepository extends JpaRepository<CartItem, UUID> {
    boolean existsByBookId(UUID bookId);
}
