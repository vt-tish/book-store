package com.vttish.bookstore.cart.controller;

import com.vttish.bookstore.cart.dto.AddCartItemDto;
import com.vttish.bookstore.cart.dto.CartDto;
import com.vttish.bookstore.cart.dto.UpdateCartItemDto;
import com.vttish.bookstore.cart.service.CartService;
import com.vttish.bookstore.common.constant.ApiRoutingConstants;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;

@RestController
@RequestMapping(ApiRoutingConstants.API_V1 + "/cart")
@Validated
@RequiredArgsConstructor
public class CartController {
    private final CartService cartService;

    @GetMapping
    public CartDto getCart(UUID ownerId) { // TODO: Add ownerId retrieving
        return cartService.get(ownerId);
    }

    @PostMapping("/items")
    public CartDto addItem(@Valid @RequestBody AddCartItemDto addCartItemDto, UUID ownerId) { // TODO: Add ownerId retrieving
        return cartService.addItem(ownerId, addCartItemDto);
    }

    @PutMapping("/items/{bookId}")
    public CartDto updateItemQuantity(
            @PathVariable UUID bookId,
            @Valid @RequestBody UpdateCartItemDto updateCartItemDto,
            UUID ownerId
    ) { // TODO: Add ownerId retrieving
        return cartService.updateItem(ownerId, bookId, updateCartItemDto);
    }

    @DeleteMapping("/items/{bookId}")
    public CartDto removeItem(
            @PathVariable UUID bookId,
            UUID ownerId
    ) { // TODO: Add ownerId retrieving
        return cartService.removeItem(ownerId, bookId);
    }

    @DeleteMapping
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void clearCart(UUID ownerId) { // TODO: Add ownerId retrieving
        cartService.clear(ownerId);
    }
}
