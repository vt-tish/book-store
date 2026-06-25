package com.vttish.bookstore.cart.service.impl;

import com.vttish.bookstore.cart.entity.Cart;
import com.vttish.bookstore.cart.exception.CartNotFoundException;
import com.vttish.bookstore.cart.repository.CartItemRepository;
import com.vttish.bookstore.cart.repository.CartRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Optional;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class CartQueryServiceImplTest {

    @Mock
    private CartRepository cartRepository;

    @Mock
    private CartItemRepository cartItemRepository;

    @InjectMocks
    private CartQueryServiceImpl cartQueryService;

    @Test
    void containsBook_ShouldReturnTrueIfItemExists() {
        UUID bookId = UUID.randomUUID();
        when(cartItemRepository.existsByBookId(bookId)).thenReturn(true);

        boolean result = cartQueryService.containsBook(bookId);

        assertTrue(result);
    }

    @Test
    void containsBook_ShouldReturnFalseIfItemDoesNotExist() {
        UUID bookId = UUID.randomUUID();
        when(cartItemRepository.existsByBookId(bookId)).thenReturn(false);

        boolean result = cartQueryService.containsBook(bookId);

        assertFalse(result);
    }

    @Test
    void getCartForSubmission_WhenCartExists_ShouldReturnCart() {
        UUID ownerId = UUID.randomUUID();
        Cart cart = new Cart(ownerId);
        when(cartRepository.findWithBooksByOwnerId(ownerId)).thenReturn(Optional.of(cart));

        Cart result = cartQueryService.getCartForSubmission(ownerId);

        assertEquals(cart, result);
    }

    @Test
    void getCartForSubmission_WhenCartDoesNotExist_ShouldThrowException() {
        UUID ownerId = UUID.randomUUID();
        when(cartRepository.findWithBooksByOwnerId(ownerId)).thenReturn(Optional.empty());

        assertThrows(CartNotFoundException.class, () -> cartQueryService.getCartForSubmission(ownerId));
    }
}
