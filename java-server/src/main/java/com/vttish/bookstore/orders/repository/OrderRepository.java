package com.vttish.bookstore.orders.repository;

import com.vttish.bookstore.orders.dto.AdminOrderCardResponseDto;
import com.vttish.bookstore.orders.dto.OrderCardResponseDto;
import com.vttish.bookstore.orders.entity.Order;
import com.vttish.bookstore.orders.entity.enums.OrderStatus;
import org.jspecify.annotations.NonNull;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.Optional;
import java.util.UUID;

public interface OrderRepository extends JpaRepository<Order, UUID>, JpaSpecificationExecutor<Order> {

    @EntityGraph(attributePaths = { "client", "employee" })
    @NonNull Page<Order> findAll(Specification<Order> spec, @NonNull Pageable pageable);

    @EntityGraph(attributePaths = { "items", "items.book" })
    @Query("SELECT o FROM Order o WHERE o.id = :id AND o.client.id = :clientId")
    Optional<Order> findWithBooksByIdAndClientId(@Param("id") UUID id, @Param("clientId") UUID clientId);

    @EntityGraph(attributePaths = {
            "client",
            "employee",
            "items",
            "items.book"
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
}
