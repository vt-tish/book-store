package com.vttish.bookstore.orders.service.impl;

import com.vttish.bookstore.common.exception.EntityNotFoundException;
import com.vttish.bookstore.orders.entity.Order;
import com.vttish.bookstore.orders.mapper.OrderMapper;
import com.vttish.bookstore.orders.repository.OrderRepository;
import com.vttish.bookstore.orders.service.OrderManagementService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.UUID;

@Service
@RequiredArgsConstructor
@Transactional
public class OrderManagementServiceImpl implements OrderManagementService {
    private final OrderRepository orderRepository;
    private final OrderMapper mapper;

    @Override
    public void accept(UUID employeeId, UUID id) {
        getEntityById(id).accept(employeeId);
    }

    @Override
    public void cancel(UUID employeeId, UUID id) {
        getEntityById(id).cancel(employeeId);
    }

    @Override
    public void complete(UUID employeeId, UUID id) {
        getEntityById(id).complete(employeeId);
    }

    private Order getEntityById(UUID id) {
        return orderRepository.findById(id).orElseThrow(() ->
                new EntityNotFoundException(Order.class, id)
        );
    }
}
