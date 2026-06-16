package com.vttish.bookstore.cart.service;

import com.vttish.bookstore.cart.dto.AddCartItemDto;
import com.vttish.bookstore.cart.dto.CartDto;
import com.vttish.bookstore.cart.dto.UpdateCartItemDto;

import java.util.UUID;

public interface CartService {
    CartDto get(UUID ownerId);
    CartDto addItem(UUID ownerId, AddCartItemDto addCartItemDto);
    CartDto updateItem(UUID ownerId, UUID bookId, UpdateCartItemDto updateCartItemDto);
    CartDto removeItem(UUID ownerId, UUID bookId);
    void clear(UUID ownerId);
}
