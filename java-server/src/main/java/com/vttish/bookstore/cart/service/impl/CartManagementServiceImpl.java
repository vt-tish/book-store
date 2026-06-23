package com.vttish.bookstore.cart.service.impl;

import com.vttish.bookstore.books.entity.Book;
import com.vttish.bookstore.books.service.BookQueryService;
import com.vttish.bookstore.cart.dto.AddCartItemRequestDto;
import com.vttish.bookstore.cart.dto.CartItemDto;
import com.vttish.bookstore.cart.dto.CartResponseDto;
import com.vttish.bookstore.cart.dto.UpdateCartItemRequestDto;
import com.vttish.bookstore.cart.entity.Cart;
import com.vttish.bookstore.cart.entity.CartItem;
import com.vttish.bookstore.cart.exception.CartItemNotFoundException;
import com.vttish.bookstore.cart.repository.CartItemRepository;
import com.vttish.bookstore.cart.repository.CartRepository;
import com.vttish.bookstore.cart.service.CartInitializer;
import com.vttish.bookstore.cart.service.CartManagementService;
import com.vttish.bookstore.common.config.LocalizationProperties;
import lombok.RequiredArgsConstructor;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.*;

@Service
@Transactional(readOnly = true)
@RequiredArgsConstructor
public class CartManagementServiceImpl implements CartManagementService {
    private final CartRepository cartRepository;
    private final CartItemRepository cartItemRepository;
    private final BookQueryService bookQueryService;
    private final CartInitializer cartInitializer;
    private final LocalizationProperties localizationProps;

    @Override
    public CartResponseDto get(UUID ownerId, String lang) {
        Optional<Cart> existingCart = cartRepository.findByOwnerId(ownerId);

        return existingCart.map(cart -> toCartResponseDto(cart, lang))
                .orElseGet(CartResponseDto::empty);
    }

    @Override
    @Transactional
    public CartResponseDto addItem(UUID ownerId, String lang, AddCartItemRequestDto addCartItemRequestDto) {
        Cart cart;

        try {
            cart = cartInitializer.getOrCreate(ownerId);
        } catch (DataIntegrityViolationException ex) {
            cart = cartRepository.findByOwnerId(ownerId).orElseThrow(
                    CartItemNotFoundException::new
            );
        }

        Optional<CartItem> existingItem = cart.getItems().stream()
                .filter(item -> item.getBook().getId().equals(addCartItemRequestDto.bookId()))
                .findFirst();

        if (existingItem.isPresent()) {
            CartItem item = existingItem.get();
            item.setQuantity(item.getQuantity() + addCartItemRequestDto.quantity());
        } else {
            Book book = bookQueryService.getByIdAvailable(addCartItemRequestDto.bookId());
            cart.addItem(new CartItem(book, addCartItemRequestDto.quantity()));
        }

        return toCartResponseDto(cartRepository.saveAndFlush(cart), lang);
    }

    @Override
    @Transactional
    public CartResponseDto updateItem(
            UUID ownerId,
            UUID id,
            String lang,
            UpdateCartItemRequestDto updateCartItemRequestDto
    ) {
        Optional<Cart> existingCart = cartRepository.findByOwnerId(ownerId);

        if (existingCart.isEmpty()) {
            throw new CartItemNotFoundException();
        }

        Cart cart = existingCart.get();
        CartItem item = cart.getItems().stream()
                .filter(cartItem -> cartItem.getId().equals(id))
                .findFirst()
                .orElseThrow(CartItemNotFoundException::new);

        item.setQuantity(updateCartItemRequestDto.quantity());
        return toCartResponseDto(cartRepository.saveAndFlush(cart), lang);
    }

    @Override
    @Transactional
    public CartResponseDto removeItem(UUID ownerId, UUID id, String lang) {
        Optional<Cart> existingCart = cartRepository.findByOwnerId(ownerId);

        if (existingCart.isEmpty()) {
            return CartResponseDto.empty();
        }

        Cart cart = existingCart.get();
        CartItem item = cart.getItems().stream()
                .filter(cartItem -> cartItem.getId().equals(id))
                .findFirst()
                .orElseThrow(CartItemNotFoundException::new);

        cart.removeItem(item);
        return toCartResponseDto(cartRepository.saveAndFlush(cart), lang);
    }

    @Override
    @Transactional
    public void clear(UUID ownerId) {
        Optional<Cart> existingCart = cartRepository.findByOwnerId(ownerId);

        if (existingCart.isEmpty()) {
            return;
        }

        Cart cart = existingCart.get();
        cart.clear();
        cartRepository.save(cart);
    }

    @Override
    @Transactional
    public void clear(Cart cart) {
        if (cart.getItems().isEmpty()) {
            return;
        }

        cart.clear();
        cartRepository.save(cart);
    }

    private CartResponseDto toCartResponseDto(Cart cart, String lang) {
        List<CartItemDto> items = cartItemRepository.findAllWithLocalizedBooks(
                cart.getId(),
                localizationProps.resolveLanguage(lang)
        );

        BigDecimal totalPrice = items.stream()
                .filter(CartItemDto::isAvailable)
                .map(CartItemDto::subtotalPrice)
                .reduce(BigDecimal.ZERO, BigDecimal::add);

        return new CartResponseDto(items, totalPrice);
    }
}
