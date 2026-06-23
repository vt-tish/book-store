package com.vttish.bookstore.orders.repository;

import com.vttish.bookstore.orders.dto.AdminOrderCardResponseDto;
import com.vttish.bookstore.orders.dto.OrderCardResponseDto;
import com.vttish.bookstore.orders.entity.Order;
import com.vttish.bookstore.orders.entity.enums.OrderStatus;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.Optional;
import java.util.UUID;

public interface OrderRepository extends JpaRepository<Order, UUID> {

    @EntityGraph(attributePaths = {
            "items",
            "items.book",
            "items.book.translations"
    })
    @Query("SELECT o FROM Order o WHERE o.id = :id AND o.client.id = :clientId")
    Optional<Order> findFullByIdAndClientId(@Param("id") UUID id, @Param("clientId") UUID clientId);

    @EntityGraph(attributePaths = {
            "client",
            "employee",
            "items",
            "items.book",
            "items.book.translations"
    })
    Optional<Order> findFullById(UUID id);

    @Query("""
        SELECT new com.vttish.bookstore.orders.dto.OrderCardResponseDto(
            o.id,
            o.totalPrice,
            SIZE(o.items),
            o.status,
            o.createdAt,
            o.closedAt
        )
        FROM Order o
        WHERE o.client.id = :clientId
    """)
    Page<OrderCardResponseDto> findAllCardsByClientId(
            @Param("clientId") UUID clientId,
            Pageable pageable
    );

    @Query("""
        SELECT new com.vttish.bookstore.orders.dto.AdminOrderCardResponseDto(
            o.id,
            c.id,
            c.email,
            e.id,
            e.email,
            o.totalPrice,
            SIZE(o.items),
            o.status,
            o.createdAt,
            o.closedAt
        )
        FROM Order o
        JOIN o.client c
        LEFT JOIN o.employee e
        WHERE (:employeeId IS NULL OR e.id = :employeeId)
        AND (:status IS NULL OR o.status = :status)
    """)
    Page<AdminOrderCardResponseDto> findAllAdminCardsByFilter(
            @Param("employeeId") UUID employeeId,
            @Param("status") OrderStatus status,
            Pageable pageable
    );

    @Query("SELECT COUNT(o) > 0 FROM Order o JOIN o.items it WHERE it.book.id = :bookId")
    boolean existsByBookId(@Param("bookId") UUID bookId);
}
