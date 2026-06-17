package com.vttish.bookstore.orders.entity;

import com.vttish.bookstore.common.entity.BaseEntity;
import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.math.BigDecimal;
import java.util.UUID;

@Entity
@Table(name = "book_items")
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class BookItem extends BaseEntity {

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "order_id")
    @Setter(value = AccessLevel.PACKAGE)
    private Order order;

    private UUID bookId;
    private String bookName;
    private String bookAuthor;
    private BigDecimal pricePerUnit;

    private Integer quantity;

    public BookItem(UUID bookId, String bookName, String bookAuthor, BigDecimal pricePerUnit, Integer quantity) {
        this.bookId = bookId;
        this.bookName = bookName;
        this.bookAuthor = bookAuthor;
        this.pricePerUnit = pricePerUnit;
        this.quantity = quantity;
    }
}
