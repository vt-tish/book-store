package com.vttish.bookstore.cart.entity;

import com.vttish.bookstore.books.entity.Book;
import com.vttish.bookstore.common.entity.BaseEntity;
import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "cart_items")
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class CartItem extends BaseEntity {

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "cart_id")
    @Setter
    private Cart cart;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "book_id")
    private Book book;

    @Setter
    private Integer quantity;

    public CartItem(Book book, Integer quantity) {
        this.book = book;
        this.quantity = quantity;
    }
}
