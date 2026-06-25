package com.vttish.bookstore.cart.service.impl;

import com.vttish.bookstore.cart.entity.Cart;
import com.vttish.bookstore.cart.repository.CartRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Optional;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class CartInitializerImplTest {

    @Mock
    private CartRepository cartRepository;

    @InjectMocks
    private CartInitializerImpl cartInitializer;

    @Test
    void getOrCreate_WhenCartExists_ShouldReturnExistingCart() {
        UUID ownerId = UUID.randomUUID();
        Cart existingCart = new Cart(ownerId);
        
        when(cartRepository.findWithBooksByOwnerId(ownerId)).thenReturn(Optional.of(existingCart));

        Cart result = cartInitializer.getOrCreate(ownerId);

        assertEquals(existingCart, result);
        verify(cartRepository, never()).save(any());
    }

    @Test
    void getOrCreate_WhenCartDoesNotExist_ShouldCreateAndSaveNewCart() {
        UUID ownerId = UUID.randomUUID();
        Cart savedCart = new Cart(ownerId);
        
        when(cartRepository.findWithBooksByOwnerId(ownerId)).thenReturn(Optional.empty());
        when(cartRepository.save(any(Cart.class))).thenReturn(savedCart);

        Cart result = cartInitializer.getOrCreate(ownerId);

        assertNotNull(result);
        assertEquals(ownerId, result.getOwnerId());
        verify(cartRepository).save(any(Cart.class));
    }
}
