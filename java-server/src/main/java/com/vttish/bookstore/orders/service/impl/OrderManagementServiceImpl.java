package com.vttish.bookstore.orders.service.impl;

import com.vttish.bookstore.orders.entity.Order;
import com.vttish.bookstore.orders.exception.OrderNotFoundException;
import com.vttish.bookstore.orders.mapper.OrderMapper;
import com.vttish.bookstore.orders.repository.OrderRepository;
import com.vttish.bookstore.orders.service.OrderManagementService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.UUID;

@Service
@RequiredArgsConstructor
public class OrderManagementServiceImpl implements OrderManagementService {
    private final OrderRepository orderRepository;
    private final OrderMapper mapper;

    @Override
    @Transactional
    public void accept(UUID employeeId, UUID id) {
        Order order = getEntityById(id);
        order.accept(employeeId);
        orderRepository.save(order);
    }

    @Override
    @Transactional
    public void cancel(UUID employeeId, UUID id) {
        Order order = getEntityById(id);
        order.cancel(employeeId);
        orderRepository.save(order);
    }

    @Override
    @Transactional
    public void complete(UUID employeeId, UUID id) {
        Order order = getEntityById(id);
        order.complete(employeeId);
        orderRepository.save(order);
    }

    private Order getEntityById(UUID id) {
        return orderRepository.findById(id).orElseThrow(OrderNotFoundException::new);
    }
}
