package com.vttish.bookstore.cart.service;

import com.vttish.bookstore.cart.dto.AddCartItemRequestDto;
import com.vttish.bookstore.cart.dto.CartResponseDto;
import com.vttish.bookstore.cart.dto.UpdateCartItemRequestDto;

import java.util.UUID;

public interface CartService {
    CartResponseDto get(UUID ownerId);
    CartResponseDto addItem(UUID ownerId, AddCartItemRequestDto addCartItemRequestDto);
    CartResponseDto updateItem(UUID ownerId, UUID bookId, UpdateCartItemRequestDto updateCartItemRequestDto);
    CartResponseDto removeItem(UUID ownerId, UUID bookId);
    void clear(UUID ownerId);
}
