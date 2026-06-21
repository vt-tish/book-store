package com.vttish.bookstore.orders.service.impl;

import com.vttish.bookstore.orders.dto.OrderCardResponseDto;
import com.vttish.bookstore.orders.dto.OrderDetailsResponseDto;
import com.vttish.bookstore.orders.entity.Order;
import com.vttish.bookstore.orders.entity.enums.OrderStatus;
import com.vttish.bookstore.orders.exception.OrderNotFoundException;
import com.vttish.bookstore.orders.mapper.OrderMapper;
import com.vttish.bookstore.orders.repository.OrderRepository;
import com.vttish.bookstore.orders.service.OrderQueryService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.UUID;

@Service
@Transactional(readOnly = true)
@RequiredArgsConstructor
public class OrderQueryServiceImpl implements OrderQueryService {
    private final OrderRepository orderRepository;
    private final OrderMapper mapper;

    @Override
    public Page<OrderCardResponseDto> getByClientId(UUID clientId, Pageable pageable) {
        return orderRepository.findAllByClientId(clientId, pageable).map(mapper::toOrderCardDto);
    }

    @Override
    public OrderDetailsResponseDto getByIdAndClientId(UUID id, UUID clientId) {
        Order order = orderRepository.findByIdAndClientId(id, clientId).orElseThrow(
                OrderNotFoundException::new
        );

        return mapper.toOrderDetailsDto(order);
    }

    @Override
    public Page<OrderCardResponseDto> getAll(UUID employeeId, OrderStatus status, Pageable pageable) {
        return orderRepository.findByFilters(employeeId, status, pageable).map(mapper::toOrderCardDto);
    }

    @Override
    public OrderDetailsResponseDto getById(UUID id) {
        Order order = orderRepository.findById(id).orElseThrow(OrderNotFoundException::new);

        return mapper.toOrderDetailsDto(order);
    }

    @Override
    public boolean hasBookBeenOrdered(UUID bookId) {
        return orderRepository.hasBookBeenOrdered(bookId);
    }
}
