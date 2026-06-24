package com.vttish.bookstore.cart.service.impl;

import com.vttish.bookstore.cart.entity.Cart;
import com.vttish.bookstore.cart.exception.CartNotFoundException;
import com.vttish.bookstore.cart.repository.CartItemRepository;
import com.vttish.bookstore.cart.repository.CartRepository;
import com.vttish.bookstore.cart.service.CartQueryService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.UUID;

@Service
@Transactional(readOnly = true)
@RequiredArgsConstructor
public class CartQueryServiceImpl implements CartQueryService {
    private final CartRepository cartRepository;
    private final CartItemRepository cartItemRepository;

    @Override
    public boolean containsBook(UUID bookId) {
        return cartItemRepository.existsByBookId(bookId);
    }

    @Override
    public Cart getCartForSubmission(UUID ownerId) {
        return cartRepository.findWithBooksByOwnerId(ownerId).orElseThrow(
                CartNotFoundException::new
        );
    }
}
