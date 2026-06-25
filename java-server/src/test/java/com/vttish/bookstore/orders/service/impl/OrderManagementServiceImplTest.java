package com.vttish.bookstore.orders.service.impl;

import com.vttish.bookstore.auth.entity.User;
import com.vttish.bookstore.auth.service.UserService;
import com.vttish.bookstore.orders.entity.Order;
import com.vttish.bookstore.orders.entity.enums.OrderStatus;
import com.vttish.bookstore.orders.exception.OrderNotFoundException;
import com.vttish.bookstore.orders.repository.OrderRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Optional;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class OrderManagementServiceImplTest {

    @Mock
    private OrderRepository orderRepository;

    @Mock
    private UserService userService;

    @InjectMocks
    private OrderManagementServiceImpl orderManagementService;

    @Test
    void accept_ShouldAcceptOrderAndSave() {
        UUID employeeId = UUID.randomUUID();
        UUID orderId = UUID.randomUUID();
        Order order = mock(Order.class);
        User employee = mock(User.class);

        when(orderRepository.findById(orderId)).thenReturn(Optional.of(order));
        when(userService.getRefById(employeeId)).thenReturn(employee);

        orderManagementService.accept(employeeId, orderId);

        verify(order).accept(employee);
        verify(orderRepository).save(order);
    }

    @Test
    void cancel_ShouldCancelOrderAndSave() {
        UUID employeeId = UUID.randomUUID();
        UUID orderId = UUID.randomUUID();
        Order order = mock(Order.class);
        User employee = mock(User.class);

        when(orderRepository.findById(orderId)).thenReturn(Optional.of(order));
        when(userService.getRefById(employeeId)).thenReturn(employee);

        orderManagementService.cancel(employeeId, orderId);

        verify(order).cancel(employee);
        verify(orderRepository).save(order);
    }

    @Test
    void complete_ShouldCompleteOrderAndSave() {
        UUID employeeId = UUID.randomUUID();
        UUID orderId = UUID.randomUUID();
        Order order = mock(Order.class);
        User employee = mock(User.class);

        when(orderRepository.findById(orderId)).thenReturn(Optional.of(order));
        when(userService.getRefById(employeeId)).thenReturn(employee);

        orderManagementService.complete(employeeId, orderId);

        verify(order).complete(employee);
        verify(orderRepository).save(order);
    }

    @Test
    void accept_WhenOrderNotFound_ShouldThrowException() {
        UUID orderId = UUID.randomUUID();
        when(orderRepository.findById(orderId)).thenReturn(Optional.empty());

        assertThrows(OrderNotFoundException.class, () ->
                orderManagementService.accept(UUID.randomUUID(), orderId)
        );
    }
}
