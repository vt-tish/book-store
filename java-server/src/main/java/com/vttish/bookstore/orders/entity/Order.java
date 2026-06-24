package com.vttish.bookstore.orders.entity;

import com.vttish.bookstore.auth.entity.User;
import com.vttish.bookstore.common.entity.BaseEntity;
import com.vttish.bookstore.orders.entity.enums.OrderStatus;
import com.vttish.bookstore.orders.exception.InvalidOrderStateException;
import com.vttish.bookstore.orders.exception.OrderEmployeeMismatchException;
import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

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

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "client_id")
    private User client;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "employee_id")
    private User employee;

    @Setter
    private BigDecimal totalPrice;

    @OneToMany(mappedBy = "order", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<OrderItem> items = new ArrayList<>();

    @Enumerated(value = EnumType.STRING)
    private OrderStatus status = OrderStatus.PENDING;

    private Instant closedAt;

    public Order(User client) {
        this.client = client;
    }

    public void addItem(OrderItem orderItem) {
        items.add(orderItem);
        orderItem.setOrder(this);
    }

    public void accept(User employee) {
        if (status != OrderStatus.PENDING) {
            throw new InvalidOrderStateException(status.name());
        }

        status = OrderStatus.ACCEPTED;
        this.employee = employee;
    }

    public void cancel(User employee) {
        if (status != OrderStatus.ACCEPTED) {
            throw new InvalidOrderStateException(status.name());
        }

        if (this.employee == null || !this.employee.getId().equals(employee.getId())) {
            throw new OrderEmployeeMismatchException();
        }

        status = OrderStatus.CANCELLED;
        closedAt = Instant.now();
    }

    public void complete(User employee) {
        if (status != OrderStatus.ACCEPTED) {
            throw new InvalidOrderStateException(status.name());
        }

        if (this.employee == null || !this.employee.getId().equals(employee.getId())) {
            throw new OrderEmployeeMismatchException();
        }

        status = OrderStatus.COMPLETED;
        closedAt = Instant.now();
    }
}
