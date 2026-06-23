package com.vttish.bookstore.orders.entity;

import com.vttish.bookstore.books.entity.Book;
import com.vttish.bookstore.common.entity.BaseEntity;
import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.math.BigDecimal;

@Entity
@Table(name = "order_items")
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class OrderItem extends BaseEntity {

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "order_id")
    @Setter
    private Order order;

    @ManyToOne(fetch = FetchType.LAZY)
    private Book book;

    private BigDecimal pricePerUnit;

    private Integer quantity;

    public OrderItem(Book book, BigDecimal pricePerUnit, Integer quantity) {
        this.book = book;
        this.pricePerUnit = pricePerUnit;
        this.quantity = quantity;
    }
}
