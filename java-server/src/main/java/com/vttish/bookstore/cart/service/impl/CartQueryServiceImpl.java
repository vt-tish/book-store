package com.vttish.bookstore.cart.service.impl;

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

    @Override
    public boolean containsBook(UUID bookId) {
        return cartRepository.existsByBookId(bookId);
    }
}
