package com.vttish.bookstore.cart.service;

import com.vttish.bookstore.cart.dto.CartDto;
import com.vttish.bookstore.cart.dto.UpdateCartDto;

import java.util.UUID;

public interface CartService {
    CartDto get(UUID ownerId);
    CartDto update(UUID ownerId, UpdateCartDto updateCartDto);
}
