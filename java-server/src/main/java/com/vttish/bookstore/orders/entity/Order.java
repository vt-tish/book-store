package com.vttish.bookstore.orders.entity;

import com.vttish.bookstore.common.entity.BaseEntity;
import com.vttish.bookstore.orders.entity.enums.OrderStatus;
import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

// @Entity
@Table(name = "orders")
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class Order extends BaseEntity {
    private UUID clientId;
    private UUID employeeId;
    private BigDecimal totalPrice;

    @OneToMany(mappedBy = "order", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<BookItem> bookItems;

    @Enumerated(value = EnumType.STRING)
    private OrderStatus status;

    private LocalDateTime completedAt;
}
