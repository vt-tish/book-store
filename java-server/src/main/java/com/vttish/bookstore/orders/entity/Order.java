package com.vttish.bookstore.orders.entity;

import com.vttish.bookstore.common.entity.BaseEntity;
import com.vttish.bookstore.common.exception.IllegalEntityAccessException;
import com.vttish.bookstore.common.exception.IllegalEntityStateException;
import com.vttish.bookstore.orders.entity.enums.OrderStatus;
import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.math.BigDecimal;
import java.time.LocalDateTime;
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

    private LocalDateTime closedAt;

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
            throw new IllegalEntityStateException("Cannot accept order with status " + status);
        }

        status = OrderStatus.ACCEPTED;
        this.employeeId = employeeId;
    }

    public void cancel(UUID employeeId) {
        if (status != OrderStatus.ACCEPTED) {
            throw new IllegalEntityStateException("Cannot cancel order with status " + status);
        }

        if (!this.employeeId.equals(employeeId)) {
            throw new IllegalEntityAccessException("Order only can be canceled by employee who accepted it");
        }

        status = OrderStatus.CANCELLED;
        closedAt = LocalDateTime.now();
    }

    public void complete(UUID employeeId) {
        if (status != OrderStatus.ACCEPTED) {
            throw new IllegalEntityStateException("Cannot complete order with status " + status);
        }

        if (!this.employeeId.equals(employeeId)) {
            throw new IllegalEntityAccessException("Order can only be completed by the employee who accepted it");
        }

        status = OrderStatus.COMPLETED;
        closedAt = LocalDateTime.now();
    }
}
