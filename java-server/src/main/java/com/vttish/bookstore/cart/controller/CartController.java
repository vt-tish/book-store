package com.vttish.bookstore.cart.controller;

import com.vttish.bookstore.cart.dto.AddCartItemRequestDto;
import com.vttish.bookstore.cart.dto.CartResponseDto;
import com.vttish.bookstore.cart.dto.UpdateCartItemRequestDto;
import com.vttish.bookstore.cart.service.CartService;
import com.vttish.bookstore.common.constant.ApiRoutingConstants;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;

@RestController
@RequestMapping(ApiRoutingConstants.API_V1 + "/cart")
@Validated
@RequiredArgsConstructor
@PreAuthorize("hasRole('CLIENT')")
public class CartController {
    private final CartService cartService;

    @GetMapping
    public CartResponseDto getCart(@AuthenticationPrincipal UUID ownerId) {
        return cartService.get(ownerId);
    }

    @PostMapping("/items")
    public CartResponseDto addItem(
            @Valid @RequestBody AddCartItemRequestDto addCartItemRequestDto,
            @AuthenticationPrincipal UUID ownerId
    ) {
        return cartService.addItem(ownerId, addCartItemRequestDto);
    }

    @PutMapping("/items/{bookId}")
    public CartResponseDto updateItemQuantity(
            @PathVariable UUID bookId,
            @Valid @RequestBody UpdateCartItemRequestDto updateCartItemRequestDto,
            @AuthenticationPrincipal UUID ownerId
    ) {
        return cartService.updateItem(ownerId, bookId, updateCartItemRequestDto);
    }

    @DeleteMapping("/items/{bookId}")
    public CartResponseDto removeItem(
            @PathVariable UUID bookId,
            @AuthenticationPrincipal UUID ownerId
    ) {
        return cartService.removeItem(ownerId, bookId);
    }

    @DeleteMapping
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void clearCart(@AuthenticationPrincipal UUID ownerId) {
        cartService.clear(ownerId);
    }
}
