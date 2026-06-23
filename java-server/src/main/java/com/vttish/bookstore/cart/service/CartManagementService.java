package com.vttish.bookstore.cart.service;

import com.vttish.bookstore.cart.dto.AddCartItemRequestDto;
import com.vttish.bookstore.cart.dto.CartResponseDto;
import com.vttish.bookstore.cart.dto.UpdateCartItemRequestDto;
import com.vttish.bookstore.cart.entity.Cart;

import java.util.UUID;

public interface CartManagementService {
    CartResponseDto get(UUID ownerId, String lang);
    CartResponseDto addItem(UUID ownerId, String lang, AddCartItemRequestDto addCartItemRequestDto);
    CartResponseDto updateItem(UUID ownerId, UUID id, String lang, UpdateCartItemRequestDto updateCartItemRequestDto);
    CartResponseDto removeItem(UUID ownerId, UUID id, String lang);
    void clear(UUID ownerId);
    void clear(Cart cart);
}
