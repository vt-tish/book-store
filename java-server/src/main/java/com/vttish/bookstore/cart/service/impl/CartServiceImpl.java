package com.vttish.bookstore.cart.service.impl;

import com.vttish.bookstore.books.dto.CartBookView;
import com.vttish.bookstore.books.service.BookQueryService;
import com.vttish.bookstore.cart.dto.AddCartItemDto;
import com.vttish.bookstore.cart.dto.CartDto;
import com.vttish.bookstore.cart.dto.UpdateCartItemDto;
import com.vttish.bookstore.cart.entity.Cart;
import com.vttish.bookstore.cart.entity.CartItem;
import com.vttish.bookstore.cart.mapper.CartMapper;
import com.vttish.bookstore.cart.repository.CartRepository;
import com.vttish.bookstore.cart.service.CartInitializer;
import com.vttish.bookstore.cart.service.CartService;
import com.vttish.bookstore.common.exception.EntityCreationConflictException;
import com.vttish.bookstore.common.exception.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.*;
import java.util.stream.Collectors;

@Service
@Transactional(readOnly = true)
@RequiredArgsConstructor
public class CartServiceImpl implements CartService {
    private final CartRepository cartRepository;
    private final BookQueryService bookQueryService;
    private final CartInitializer cartInitializer;
    private final CartMapper cartMapper;

    @Override
    public CartDto get(UUID ownerId) {
        Cart cart = cartRepository.findByOwnerId(ownerId).orElse(null);

        if (cart == null) {
            return CartDto.empty();
        }

        return cartMapper.toCartDto(cart, fetchPrices(cart));
    }

    @Override
    @Transactional
    public CartDto addItem(UUID ownerId, AddCartItemDto addCartItemDto) {
        Cart cart;

        try {
            cart = cartInitializer.getOrCreate(ownerId);
        } catch (DataIntegrityViolationException ex) {
            cart = cartRepository.findByOwnerId(ownerId)
                    .orElseThrow(() -> new EntityCreationConflictException("Cart is not found after conflict", ex));
        }

        CartBookView book = bookQueryService.getBookDetailsForCart(addCartItemDto.bookId());

        CartItem item = cart.getItems().stream()
                .filter(cartItem -> cartItem.getBookId().equals(addCartItemDto.bookId()))
                .findFirst()
                .orElse(null);

        if (item != null) {
            item.setQuantity(addCartItemDto.quantity());
        } else {
            cart.addItem(new CartItem(addCartItemDto.bookId(), book.getName(), addCartItemDto.quantity()));
        }

        cart = cartRepository.save(cart);
        return cartMapper.toCartDto(cart, fetchPrices(cart));
    }

    @Override
    @Transactional
    public CartDto updateItem(UUID ownerId, UUID bookId, UpdateCartItemDto updateCartItemDto) {
        Cart cart = cartRepository.findByOwnerId(ownerId).orElse(null);

        if (cart == null) {
            throw new EntityNotFoundException(CartItem.class, bookId);
        }

        CartItem item = cart.getItems().stream()
                .filter(cartItem -> cartItem.getBookId().equals(bookId))
                .findFirst()
                .orElseThrow(() -> new EntityNotFoundException(CartItem.class, bookId));

        item.setQuantity(updateCartItemDto.quantity());

        cart = cartRepository.save(cart);
        return cartMapper.toCartDto(cart, fetchPrices(cart));
    }

    @Override
    @Transactional
    public CartDto removeItem(UUID ownerId, UUID bookId) {
        Cart cart = cartRepository.findByOwnerId(ownerId).orElse(null);

        if (cart == null) {
            return CartDto.empty();
        }

        if (cart.getItems().removeIf(cartItem -> cartItem.getBookId().equals(bookId))) {
            cart = cartRepository.save(cart);
        }

        return cartMapper.toCartDto(cart, fetchPrices(cart));
    }

    @Override
    @Transactional
    public void clear(UUID ownerId) {
        Cart cart = cartRepository.findByOwnerId(ownerId).orElse(null);

        if (cart == null) {
            return;
        }

        cart.getItems().clear();
        cartRepository.save(cart);
    }

    private Map<UUID, BigDecimal> fetchPrices(Cart cart) {
        Set<UUID> bookIds = cart.getItems().stream()
                .map(CartItem::getBookId)
                .collect(Collectors.toSet());

        return bookQueryService.getPricesByIds(bookIds);
    }
}
