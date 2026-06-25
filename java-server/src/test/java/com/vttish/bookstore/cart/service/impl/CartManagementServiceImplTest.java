package com.vttish.bookstore.cart.service.impl;

import com.vttish.bookstore.books.entity.Book;
import com.vttish.bookstore.books.service.BookQueryService;
import com.vttish.bookstore.cart.dto.AddCartItemRequestDto;
import com.vttish.bookstore.cart.dto.CartResponseDto;
import com.vttish.bookstore.cart.dto.UpdateCartItemRequestDto;
import com.vttish.bookstore.cart.entity.Cart;
import com.vttish.bookstore.cart.entity.CartItem;
import com.vttish.bookstore.cart.exception.CartItemNotFoundException;
import com.vttish.bookstore.cart.exception.CartNotFoundException;
import com.vttish.bookstore.cart.mapper.CartMapper;
import com.vttish.bookstore.cart.repository.CartRepository;
import com.vttish.bookstore.cart.service.CartInitializer;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.test.util.ReflectionTestUtils;

import java.math.BigDecimal;
import java.util.Collections;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class CartManagementServiceImplTest {

    @Mock
    private CartRepository cartRepository;

    @Mock
    private BookQueryService bookQueryService;

    @Mock
    private CartInitializer cartInitializer;

    @Mock
    private CartMapper mapper;

    @InjectMocks
    private CartManagementServiceImpl cartManagementService;

    private UUID ownerId;
    private Cart cart;

    @BeforeEach
    void setUp() {
        ownerId = UUID.randomUUID();
        cart = new Cart(ownerId);
    }

    @Test
    void get_WhenCartExists_ShouldReturnMappedDto() {
        when(cartRepository.findWithBooksByOwnerId(ownerId)).thenReturn(Optional.of(cart));
        when(bookQueryService.getTranslations(anyList(), eq("en"))).thenReturn(Collections.emptyMap());
        CartResponseDto expectedResponse = new CartResponseDto(Collections.emptyList(), BigDecimal.ZERO);
        when(mapper.toCartResponseDto(cart, Collections.emptyMap())).thenReturn(expectedResponse);

        CartResponseDto response = cartManagementService.get(ownerId, "en");

        assertEquals(expectedResponse, response);
    }

    @Test
    void get_WhenCartDoesNotExist_ShouldReturnEmptyDto() {
        when(cartRepository.findWithBooksByOwnerId(ownerId)).thenReturn(Optional.empty());

        CartResponseDto response = cartManagementService.get(ownerId, "en");

        assertNotNull(response);
        assertTrue(response.cartItems().isEmpty());
    }

    @Test
    void addItem_WhenItemAlreadyExists_ShouldIncreaseQuantity() {
        UUID bookId = UUID.randomUUID();
        Book book = mock(Book.class);
        when(book.getId()).thenReturn(bookId);
        CartItem cartItem = new CartItem(book, 2);
        cart.addItem(cartItem);

        when(cartInitializer.getOrCreate(ownerId)).thenReturn(cart);
        when(cartRepository.save(cart)).thenReturn(cart);
        when(bookQueryService.getTranslations(anyList(), eq("en"))).thenReturn(Collections.emptyMap());
        CartResponseDto expectedResponse = new CartResponseDto(Collections.emptyList(), BigDecimal.ZERO);
        when(mapper.toCartResponseDto(cart, Collections.emptyMap())).thenReturn(expectedResponse);

        AddCartItemRequestDto request = new AddCartItemRequestDto(bookId, 3);
        
        CartResponseDto response = cartManagementService.addItem(ownerId, "en", request);

        assertEquals(5, cartItem.getQuantity());
        assertEquals(expectedResponse, response);
        verify(bookQueryService, never()).getAvailableById(any());
    }

    @Test
    void addItem_WhenItemIsNew_ShouldAddNewItem() {
        UUID bookId = UUID.randomUUID();
        Book book = mock(Book.class);

        when(cartInitializer.getOrCreate(ownerId)).thenReturn(cart);
        when(bookQueryService.getAvailableById(bookId)).thenReturn(book);
        when(cartRepository.save(cart)).thenReturn(cart);
        when(bookQueryService.getTranslations(anyList(), eq("en"))).thenReturn(Collections.emptyMap());
        CartResponseDto expectedResponse = new CartResponseDto(Collections.emptyList(), BigDecimal.ZERO);
        when(mapper.toCartResponseDto(cart, Collections.emptyMap())).thenReturn(expectedResponse);

        AddCartItemRequestDto request = new AddCartItemRequestDto(bookId, 3);
        
        CartResponseDto response = cartManagementService.addItem(ownerId, "en", request);

        assertEquals(1, cart.getItems().size());
        assertEquals(3, cart.getItems().iterator().next().getQuantity());
        assertEquals(expectedResponse, response);
    }

    @Test
    void addItem_WhenInitializerThrowsException_ShouldFindCart() {
        UUID bookId = UUID.randomUUID();
        Book book = mock(Book.class);

        when(cartInitializer.getOrCreate(ownerId)).thenThrow(new DataIntegrityViolationException("Conflict"));
        when(cartRepository.findWithBooksByOwnerId(ownerId)).thenReturn(Optional.of(cart));
        when(bookQueryService.getAvailableById(bookId)).thenReturn(book);
        when(cartRepository.save(cart)).thenReturn(cart);
        when(bookQueryService.getTranslations(anyList(), eq("en"))).thenReturn(Collections.emptyMap());
        CartResponseDto expectedResponse = new CartResponseDto(Collections.emptyList(), BigDecimal.ZERO);
        when(mapper.toCartResponseDto(cart, Collections.emptyMap())).thenReturn(expectedResponse);

        AddCartItemRequestDto request = new AddCartItemRequestDto(bookId, 3);
        
        cartManagementService.addItem(ownerId, "en", request);

        verify(cartRepository).findWithBooksByOwnerId(ownerId);
        verify(cartRepository).save(cart);
    }

    @Test
    void updateItem_WhenCartDoesNotExist_ShouldThrowException() {
        when(cartRepository.findWithBooksByOwnerId(ownerId)).thenReturn(Optional.empty());

        assertThrows(CartItemNotFoundException.class, () -> 
                cartManagementService.updateItem(
                        ownerId, UUID.randomUUID(), "en", new UpdateCartItemRequestDto(2)
                ));
    }

    @Test
    void updateItem_WhenItemDoesNotExist_ShouldThrowException() {
        when(cartRepository.findWithBooksByOwnerId(ownerId)).thenReturn(Optional.of(cart));

        assertThrows(CartItemNotFoundException.class, () -> 
                cartManagementService.updateItem(
                        ownerId, UUID.randomUUID(), "en", new UpdateCartItemRequestDto(2)
                ));
    }

    @Test
    void updateItem_WhenItemExists_ShouldUpdateQuantity() {
        Book book = mock(Book.class);
        CartItem cartItem = new CartItem(book, 2);
        UUID itemId = UUID.randomUUID();
        ReflectionTestUtils.setField(cartItem, "id", itemId);
        cart.addItem(cartItem);

        when(cartRepository.findWithBooksByOwnerId(ownerId)).thenReturn(Optional.of(cart));
        when(cartRepository.save(cart)).thenReturn(cart);
        when(bookQueryService.getTranslations(anyList(), eq("en"))).thenReturn(Collections.emptyMap());
        CartResponseDto expectedResponse = new CartResponseDto(Collections.emptyList(), BigDecimal.ZERO);
        when(mapper.toCartResponseDto(cart, Collections.emptyMap())).thenReturn(expectedResponse);

        CartResponseDto response = cartManagementService.updateItem(
                ownerId, itemId, "en", new UpdateCartItemRequestDto(5)
        );

        assertEquals(5, cartItem.getQuantity());
        assertEquals(expectedResponse, response);
    }

    @Test
    void removeItem_WhenCartDoesNotExist_ShouldReturnEmptyDto() {
        when(cartRepository.findWithBooksByOwnerId(ownerId)).thenReturn(Optional.empty());

        CartResponseDto response = cartManagementService.removeItem(ownerId, UUID.randomUUID(), "en");

        assertTrue(response.cartItems().isEmpty());
    }

    @Test
    void removeItem_WhenItemDoesNotExist_ShouldThrowException() {
        when(cartRepository.findWithBooksByOwnerId(ownerId)).thenReturn(Optional.of(cart));

        assertThrows(CartItemNotFoundException.class, () -> 
                cartManagementService.removeItem(ownerId, UUID.randomUUID(), "en"));
    }

    @Test
    void removeItem_WhenItemExists_ShouldRemoveItem() {
        Book book = mock(Book.class);
        CartItem cartItem = new CartItem(book, 2);
        UUID itemId = UUID.randomUUID();
        ReflectionTestUtils.setField(cartItem, "id", itemId);
        cart.addItem(cartItem);

        when(cartRepository.findWithBooksByOwnerId(ownerId)).thenReturn(Optional.of(cart));
        when(cartRepository.save(cart)).thenReturn(cart);
        when(bookQueryService.getTranslations(anyList(), eq("en"))).thenReturn(Collections.emptyMap());
        CartResponseDto expectedResponse = new CartResponseDto(Collections.emptyList(), BigDecimal.ZERO);
        when(mapper.toCartResponseDto(cart, Collections.emptyMap())).thenReturn(expectedResponse);

        CartResponseDto response = cartManagementService.removeItem(ownerId, itemId, "en");

        assertTrue(cart.getItems().isEmpty());
        assertEquals(expectedResponse, response);
    }

    @Test
    void clearByOwnerId_WhenCartExists_ShouldClearCart() {
        Book book = mock(Book.class);
        cart.addItem(new CartItem(book, 2));
        
        when(cartRepository.findByOwnerId(ownerId)).thenReturn(Optional.of(cart));

        cartManagementService.clear(ownerId);

        assertTrue(cart.getItems().isEmpty());
        verify(cartRepository).save(cart);
    }

    @Test
    void clearByOwnerId_WhenCartDoesNotExist_ShouldDoNothing() {
        when(cartRepository.findByOwnerId(ownerId)).thenReturn(Optional.empty());

        cartManagementService.clear(ownerId);

        verify(cartRepository, never()).save(any());
    }

    @Test
    void clearByCart_WhenCartNotEmpty_ShouldClearCart() {
        Book book = mock(Book.class);
        cart.addItem(new CartItem(book, 2));

        cartManagementService.clear(cart);

        assertTrue(cart.getItems().isEmpty());
        verify(cartRepository).save(cart);
    }

    @Test
    void clearByCart_WhenCartIsEmpty_ShouldDoNothing() {
        cartManagementService.clear(cart);
        verify(cartRepository, never()).save(any());
    }
}
