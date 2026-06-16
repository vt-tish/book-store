package com.vttish.bookstore.orders.entity;

import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.util.UUID;

@Entity
@Table(name = "book_items")
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class BookItem {

    @ManyToOne(fetch = FetchType.LAZY)
    private Order order;

    UUID bookId;
    Integer quantity;
    BigDecimal pricePerUnit;
}
