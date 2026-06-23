package com.vttish.bookstore.cart.service;

import com.vttish.bookstore.cart.entity.Cart;

import java.util.UUID;

public interface CartQueryService {
    boolean containsBook(UUID bookId);
    Cart getCartForSubmission(UUID ownerId);
}
