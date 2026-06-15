package com.vttish.bookstore.cart.controller;

import com.vttish.bookstore.cart.dto.CartDto;
import com.vttish.bookstore.cart.dto.UpdateCartDto;
import com.vttish.bookstore.cart.service.CartService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;

@RestController
@RequestMapping("/v1/cart")
@Validated
@RequiredArgsConstructor
public class CartController {
    private final CartService cartService;

    @GetMapping
    public CartDto get() {
        return cartService.get(UUID.randomUUID()); // TODO: change to actual ownerId
    }

    @PatchMapping
    public CartDto update(@Valid @RequestBody UpdateCartDto updateCartDto) {
        return cartService.update(UUID.randomUUID(), updateCartDto); // TODO: change to actual ownerId
    }
}
