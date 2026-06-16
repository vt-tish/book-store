package com.vttish.bookstore.cart.service.impl;

import com.vttish.bookstore.cart.entity.Cart;
import com.vttish.bookstore.cart.repository.CartRepository;
import com.vttish.bookstore.cart.service.CartInitializer;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import java.util.UUID;

@Component
@RequiredArgsConstructor
public class CartInitializerImpl implements CartInitializer {
    private final CartRepository cartRepository;

    // Creates new transaction to get or create Cart to omit database rollback issues
    @Override
    @Transactional(propagation = Propagation.REQUIRES_NEW)
    public Cart getOrCreate(UUID ownerId) {
        return cartRepository.findByOwnerId(ownerId)
                .orElseGet(() -> cartRepository.save(new Cart(ownerId)));
    }
}
