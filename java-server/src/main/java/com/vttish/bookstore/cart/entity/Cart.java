package com.vttish.bookstore.cart.entity;

import com.vttish.bookstore.common.entity.BaseEntity;
import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.HashSet;
import java.util.Set;
import java.util.UUID;

@Entity
@Table(name = "carts")
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class Cart extends BaseEntity {
    private UUID ownerId;

    @ElementCollection
    @CollectionTable(
            name = "cart_items",
            joinColumns = @JoinColumn(name = "cart_id")
    )
    private Set<CartItem> items = new HashSet<>();

    public Cart(UUID ownerId) {
        this.ownerId = ownerId;
    }

    public void addItem(CartItem item) {
        items.add(item);
    }
}
