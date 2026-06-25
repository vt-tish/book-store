package com.vttish.bookstore.orders.service.impl;

import com.vttish.bookstore.auth.entity.User;
import com.vttish.bookstore.auth.service.UserService;
import com.vttish.bookstore.books.entity.Book;
import com.vttish.bookstore.books.service.BookQueryService;
import com.vttish.bookstore.cart.entity.Cart;
import com.vttish.bookstore.cart.entity.CartItem;
import com.vttish.bookstore.cart.service.CartManagementService;
import com.vttish.bookstore.cart.service.CartQueryService;
import com.vttish.bookstore.orders.dto.OrderDetailsResponseDto;
import com.vttish.bookstore.orders.entity.Order;
import com.vttish.bookstore.orders.exception.EmptyCartException;
import com.vttish.bookstore.orders.exception.UnavailableBookException;
import com.vttish.bookstore.orders.mapper.OrderMapper;
import com.vttish.bookstore.orders.repository.OrderRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.util.Set;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class OrderSubmissionServiceImplTest {

    @Mock
    private OrderRepository orderRepository;

    @Mock
    private CartQueryService cartQueryService;

    @Mock
    private BookQueryService bookQueryService;

    @Mock
    private UserService userService;

    @Mock
    private CartManagementService cartManagementService;

    @Mock
    private OrderMapper mapper;

    @InjectMocks
    private OrderSubmissionServiceImpl orderSubmissionService;

    @Test
    void submitByClientId_ShouldCreateOrderAndClearCart() {
        UUID clientId = UUID.randomUUID();
        Cart cart = mock(Cart.class);
        CartItem cartItem = mock(CartItem.class);
        Book book = mock(Book.class);
        User clientUser = mock(User.class);
        OrderDetailsResponseDto dto = mock(OrderDetailsResponseDto.class);

        when(cartQueryService.getCartForSubmission(clientId)).thenReturn(cart);
        when(cart.getItems()).thenReturn(Set.of(cartItem));
        when(cartItem.getBook()).thenReturn(book);
        when(book.isArchived()).thenReturn(false);
        when(book.getPrice()).thenReturn(BigDecimal.TEN);
        when(cartItem.getQuantity()).thenReturn(2);
        
        when(userService.getRefById(clientId)).thenReturn(clientUser);
        when(orderRepository.save(any(Order.class))).thenAnswer(i -> i.getArguments()[0]);
        when(mapper.toOrderDetailsDto(any(Order.class), any())).thenReturn(dto);

        OrderDetailsResponseDto result = orderSubmissionService.submitByClientId(clientId, "en");

        assertEquals(dto, result);
        verify(cartManagementService).clear(cart);
    }

    @Test
    void submitByClientId_WhenCartEmpty_ShouldThrowException() {
        UUID clientId = UUID.randomUUID();
        Cart cart = mock(Cart.class);
        when(cartQueryService.getCartForSubmission(clientId)).thenReturn(cart);
        when(cart.getItems()).thenReturn(Set.of());

        assertThrows(EmptyCartException.class, () ->
                orderSubmissionService.submitByClientId(clientId, "en")
        );
    }

    @Test
    void submitByClientId_WhenBookArchived_ShouldThrowException() {
        UUID clientId = UUID.randomUUID();
        Cart cart = mock(Cart.class);
        CartItem cartItem = mock(CartItem.class);
        Book book = mock(Book.class);

        when(cartQueryService.getCartForSubmission(clientId)).thenReturn(cart);
        when(cart.getItems()).thenReturn(Set.of(cartItem));
        when(cartItem.getBook()).thenReturn(book);
        when(book.isArchived()).thenReturn(true);

        assertThrows(UnavailableBookException.class, () ->
                orderSubmissionService.submitByClientId(clientId, "en")
        );
    }
}
