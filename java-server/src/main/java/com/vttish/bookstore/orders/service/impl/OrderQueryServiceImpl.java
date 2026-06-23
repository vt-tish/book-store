package com.vttish.bookstore.orders.service.impl;

import com.vttish.bookstore.common.config.LocalizationProperties;
import com.vttish.bookstore.orders.dto.AdminOrderCardResponseDto;
import com.vttish.bookstore.orders.dto.AdminOrderDetailsResponseDto;
import com.vttish.bookstore.orders.dto.OrderCardResponseDto;
import com.vttish.bookstore.orders.dto.OrderDetailsResponseDto;
import com.vttish.bookstore.orders.entity.Order;
import com.vttish.bookstore.orders.entity.enums.OrderStatus;
import com.vttish.bookstore.orders.exception.OrderNotFoundException;
import com.vttish.bookstore.orders.mapper.OrderMapper;
import com.vttish.bookstore.orders.mapper.TranslationContext;
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
    private final LocalizationProperties localizationProps;
    private final OrderMapper mapper;

    @Override
    public Page<OrderCardResponseDto> getByClientId(UUID clientId, Pageable pageable) {
        return orderRepository.findAllCardsByClientId(clientId, pageable);
    }

    @Override
    public OrderDetailsResponseDto getByIdAndClientId(UUID id, UUID clientId, String lang) {
        Order order = orderRepository.findFullByIdAndClientId(id, clientId).orElseThrow(
                OrderNotFoundException::new
        );

        return mapper.toOrderDetailsDto(
                order,
                new TranslationContext(lang, localizationProps.defaultLanguage())
        );
    }

    @Override
    public Page<AdminOrderCardResponseDto> getAll(UUID employeeId, OrderStatus status, Pageable pageable) {
        return orderRepository.findAllAdminCardsByFilter(employeeId, status, pageable);
    }

    @Override
    public AdminOrderDetailsResponseDto getById(UUID id, String lang) {
        Order order = orderRepository.findFullById(id).orElseThrow(OrderNotFoundException::new);
        return mapper.toAdminOrderDetailsDto(
                order,
                new TranslationContext(lang, localizationProps.defaultLanguage())
        );
    }

    @Override
    public boolean hasBookBeenOrdered(UUID bookId) {
        return orderRepository.existsByBookId(bookId);
    }
}
