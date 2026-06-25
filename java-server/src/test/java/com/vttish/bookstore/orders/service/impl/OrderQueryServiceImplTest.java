package com.vttish.bookstore.orders.service.impl;

import com.vttish.bookstore.books.entity.Book;
import com.vttish.bookstore.books.service.BookQueryService;
import com.vttish.bookstore.orders.dto.AdminOrderCardResponseDto;
import com.vttish.bookstore.orders.dto.AdminOrderDetailsResponseDto;
import com.vttish.bookstore.orders.dto.OrderCardResponseDto;
import com.vttish.bookstore.orders.dto.OrderDetailsResponseDto;
import com.vttish.bookstore.orders.dto.OrderFilterRequestDto;
import com.vttish.bookstore.orders.entity.Order;
import com.vttish.bookstore.orders.entity.OrderItem;
import com.vttish.bookstore.orders.exception.OrderNotFoundException;
import com.vttish.bookstore.orders.mapper.OrderMapper;
import com.vttish.bookstore.orders.repository.OrderItemRepository;
import com.vttish.bookstore.orders.repository.OrderRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyList;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class OrderQueryServiceImplTest {

    @Mock
    private OrderRepository orderRepository;

    @Mock
    private OrderItemRepository orderItemRepository;

    @Mock
    private BookQueryService bookQueryService;

    @Mock
    private OrderMapper mapper;

    @InjectMocks
    private OrderQueryServiceImpl orderQueryService;

    @Test
    void getByClientId_ShouldReturnPage() {
        UUID clientId = UUID.randomUUID();
        Pageable pageable = PageRequest.of(0, 10);
        OrderCardResponseDto dto = mock(OrderCardResponseDto.class);
        Page<OrderCardResponseDto> page = new PageImpl<>(List.of(dto));

        when(orderRepository.findAllCardsByClientId(clientId, pageable)).thenReturn(page);

        Page<OrderCardResponseDto> result = orderQueryService.getByClientId(clientId, pageable);

        assertFalse(result.isEmpty());
        assertEquals(dto, result.getContent().get(0));
    }

    @Test
    void getByIdAndClientId_WhenExists_ShouldReturnDto() {
        UUID id = UUID.randomUUID();
        UUID clientId = UUID.randomUUID();
        Order order = mock(Order.class);
        OrderItem item = mock(OrderItem.class);
        Book book = mock(Book.class);
        OrderDetailsResponseDto dto = mock(OrderDetailsResponseDto.class);

        when(orderRepository.findWithBooksByIdAndClientId(id, clientId)).thenReturn(Optional.of(order));
        when(order.getItems()).thenReturn(List.of(item));
        when(item.getBook()).thenReturn(book);
        when(mapper.toOrderDetailsDto(eq(order), any())).thenReturn(dto);

        OrderDetailsResponseDto result = orderQueryService.getByIdAndClientId(id, clientId, "en");

        assertEquals(dto, result);
    }

    @Test
    void getByIdAndClientId_WhenNotExists_ShouldThrowException() {
        UUID id = UUID.randomUUID();
        UUID clientId = UUID.randomUUID();
        when(orderRepository.findWithBooksByIdAndClientId(id, clientId)).thenReturn(Optional.empty());

        assertThrows(OrderNotFoundException.class, () ->
                orderQueryService.getByIdAndClientId(id, clientId, "en")
        );
    }

    @Test
    void getAll_ShouldReturnMappedPage() {
        Pageable pageable = PageRequest.of(0, 10);
        OrderFilterRequestDto filter = new OrderFilterRequestDto(
                null, null, null, null, null
        );

        Order order = mock(Order.class);
        Page<Order> orderPage = new PageImpl<>(List.of(order));
        AdminOrderCardResponseDto dto = mock(AdminOrderCardResponseDto.class);

        when(orderRepository.findAll(any(Specification.class), eq(pageable))).thenReturn(orderPage);
        when(mapper.toAdminOrderCardDto(order)).thenReturn(dto);

        Page<AdminOrderCardResponseDto> result = orderQueryService.getAll(filter, pageable);

        assertFalse(result.isEmpty());
        assertEquals(dto, result.getContent().get(0));
    }

    @Test
    void getAll_WhenEmpty_ShouldReturnEmptyPage() {
        Pageable pageable = PageRequest.of(0, 10);
        OrderFilterRequestDto filter = new OrderFilterRequestDto(
                null, null, null, null, null
        );

        when(orderRepository.findAll(any(Specification.class), eq(pageable))).thenReturn(Page.empty());

        Page<AdminOrderCardResponseDto> result = orderQueryService.getAll(filter, pageable);

        assertTrue(result.isEmpty());
    }

    @Test
    void getById_WhenExists_ShouldReturnAdminDto() {
        UUID id = UUID.randomUUID();
        Order order = mock(Order.class);
        AdminOrderDetailsResponseDto dto = mock(AdminOrderDetailsResponseDto.class);

        when(orderRepository.findFullById(id)).thenReturn(Optional.of(order));
        when(order.getItems()).thenReturn(List.of());
        when(mapper.toAdminOrderDetailsDto(eq(order), any())).thenReturn(dto);

        AdminOrderDetailsResponseDto result = orderQueryService.getById(id, "en");

        assertEquals(dto, result);
    }

    @Test
    void getById_WhenNotExists_ShouldThrowException() {
        UUID id = UUID.randomUUID();
        when(orderRepository.findFullById(id)).thenReturn(Optional.empty());

        assertThrows(OrderNotFoundException.class, () -> orderQueryService.getById(id, "en"));
    }

    @Test
    void hasBookBeenOrdered_ShouldReturnTrueIfOrdered() {
        UUID bookId = UUID.randomUUID();
        when(orderItemRepository.existsByBookId(bookId)).thenReturn(true);
        assertTrue(orderQueryService.hasBookBeenOrdered(bookId));
    }
}
