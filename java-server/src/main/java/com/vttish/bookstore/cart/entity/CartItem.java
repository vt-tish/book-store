package com.vttish.bookstore.cart.entity;

import jakarta.persistence.Embeddable;
import lombok.*;

import java.math.BigDecimal;
import java.util.UUID;

@Embeddable
@Getter
@AllArgsConstructor
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@EqualsAndHashCode(of = "bookId")
public class CartItem {
    private UUID bookId;

    @Setter
    private String bookName;

    @Setter
    private Integer quantity;

}
