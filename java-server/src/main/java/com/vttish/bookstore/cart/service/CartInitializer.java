package com.vttish.bookstore.cart.service;

import com.vttish.bookstore.cart.entity.Cart;

import java.util.UUID;

public interface CartInitializer {
    Cart getOrCreate(UUID ownerId);
}
