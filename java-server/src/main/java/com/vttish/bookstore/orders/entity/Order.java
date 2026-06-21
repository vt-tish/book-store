package com.vttish.bookstore.orders.entity;

import com.vttish.bookstore.common.entity.BaseEntity;
import com.vttish.bookstore.orders.entity.enums.OrderStatus;
import com.vttish.bookstore.orders.exception.InvalidOrderStateException;
import com.vttish.bookstore.orders.exception.OrderEmployeeMismatchException;
import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@Entity
@Table(name = "orders")
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class Order extends BaseEntity {
    private UUID clientId;
    private UUID employeeId;
    private BigDecimal totalPrice;

    @OneToMany(mappedBy = "order", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<BookItem> bookItems = new ArrayList<>();

    @Enumerated(value = EnumType.STRING)
    private OrderStatus status = OrderStatus.PENDING;

    private Instant closedAt;

    public Order(UUID clientId, BigDecimal totalPrice) {
        this.clientId = clientId;
        this.totalPrice = totalPrice;
    }

    public void addBookItem(BookItem bookItem) {
        bookItems.add(bookItem);
        bookItem.setOrder(this);
    }

    public void accept(UUID employeeId) {
        if (status != OrderStatus.PENDING) {
            throw new InvalidOrderStateException(status.name());
        }

        status = OrderStatus.ACCEPTED;
        this.employeeId = employeeId;
    }

    public void cancel(UUID employeeId) {
        if (status != OrderStatus.ACCEPTED) {
            throw new InvalidOrderStateException(status.name());
        }

        if (this.employeeId == null || !this.employeeId.equals(employeeId)) {
            throw new OrderEmployeeMismatchException();
        }

        status = OrderStatus.CANCELLED;
        closedAt = Instant.now();
    }

    public void complete(UUID employeeId) {
        if (status != OrderStatus.ACCEPTED) {
            throw new InvalidOrderStateException(status.name());
        }

        if (this.employeeId == null || !this.employeeId.equals(employeeId)) {
            throw new OrderEmployeeMismatchException();
        }

        status = OrderStatus.COMPLETED;
        closedAt = Instant.now();
    }
}
