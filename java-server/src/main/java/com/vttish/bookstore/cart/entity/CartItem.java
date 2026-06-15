package com.vttish.bookstore.cart.entity;

import jakarta.persistence.Embeddable;
import lombok.*;

import java.math.BigDecimal;
import java.util.UUID;

@Embeddable
@Getter
@AllArgsConstructor
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class CartItem {
    private UUID bookId;
    private BigDecimal pricePerUnit;

    @Setter
    private Integer quantity;

}
