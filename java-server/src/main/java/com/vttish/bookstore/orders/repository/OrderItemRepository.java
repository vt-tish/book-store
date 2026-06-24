package com.vttish.bookstore.orders.repository;

import com.vttish.bookstore.orders.entity.OrderItem;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.UUID;

public interface OrderItemRepository extends JpaRepository<OrderItem, UUID> {
    boolean existsByBookId(UUID bookId);
}
