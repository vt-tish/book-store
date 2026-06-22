package com.vttish.bookstore.cart.controller;

import com.vttish.bookstore.cart.dto.AddCartItemRequestDto;
import com.vttish.bookstore.cart.dto.CartResponseDto;
import com.vttish.bookstore.cart.dto.UpdateCartItemRequestDto;
import com.vttish.bookstore.cart.service.CartManagementService;
import com.vttish.bookstore.common.constant.ApiRoutingConstants;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.Locale;
import java.util.UUID;

@RestController
@RequestMapping(ApiRoutingConstants.API_V1 + "/cart")
@Validated
@RequiredArgsConstructor
@PreAuthorize("hasRole('CLIENT')")
public class CartController {
    private final CartManagementService cartManagementService;

    @GetMapping
    public CartResponseDto getCart(@AuthenticationPrincipal UUID ownerId, Locale locale) {
        return cartManagementService.get(ownerId, locale.getLanguage());
    }

    @PostMapping("/items")
    public CartResponseDto addItem(
            @AuthenticationPrincipal UUID ownerId,
            @Valid @RequestBody AddCartItemRequestDto addCartItemRequestDto,
            Locale locale
    ) {
        return cartManagementService.addItem(ownerId, locale.getLanguage(), addCartItemRequestDto);
    }

    @PutMapping("/items/{id}")
    public CartResponseDto updateItemQuantity(
            @AuthenticationPrincipal UUID ownerId,
            @PathVariable UUID id,
            @Valid @RequestBody UpdateCartItemRequestDto updateCartItemRequestDto,
            Locale locale
    ) {
        return cartManagementService.updateItem(ownerId, id, locale.getLanguage(), updateCartItemRequestDto);
    }

    @DeleteMapping("/items/{id}")
    public CartResponseDto removeItem(
            @AuthenticationPrincipal UUID ownerId,
            @PathVariable UUID id,
            Locale locale
    ) {
        return cartManagementService.removeItem(ownerId, id, locale.getLanguage());
    }

    @DeleteMapping
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void clearCart(@AuthenticationPrincipal UUID ownerId) {
        cartManagementService.clear(ownerId);
    }
}
