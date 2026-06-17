package com.vttish.bookstore.orders.repository;

import com.vttish.bookstore.orders.entity.Order;
import com.vttish.bookstore.orders.entity.enums.OrderStatus;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.Optional;
import java.util.UUID;

public interface OrderRepository extends JpaRepository<Order, UUID> {
    Page<Order> findAllByClientId(UUID clientId, Pageable pageable);
    Optional<Order> findByIdAndClientId(UUID id, UUID clientId);

    @Query("SELECT o FROM Order o " +
            "WHERE (:employeeId IS NULL OR o.employeeId = :employeeId) AND " +
            "(:status IS NULL OR o.status = :status)")
    Page<Order> findByFilters(
            @Param("employeeId") UUID employeeId,
            @Param("status") OrderStatus status,
            Pageable pageable
    );

    @Query("SELECT COUNT(o) > 0 FROM Order o JOIN o.bookItems item WHERE item.bookId = :bookId")
    boolean hasBookBeenOrdered(@Param("bookId") UUID bookId);
}
