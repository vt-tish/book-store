package com.vttish.bookstore.orders.service.impl;

import com.vttish.bookstore.books.entity.Book;
import com.vttish.bookstore.books.service.BookQueryService;
import com.vttish.bookstore.orders.dto.*;
import com.vttish.bookstore.orders.entity.Order;
import com.vttish.bookstore.orders.entity.OrderItem;
import com.vttish.bookstore.orders.exception.OrderNotFoundException;
import com.vttish.bookstore.orders.mapper.OrderMapper;
import com.vttish.bookstore.orders.repository.OrderItemRepository;
import com.vttish.bookstore.orders.repository.OrderRepository;
import com.vttish.bookstore.orders.repository.OrderSpecifications;
import com.vttish.bookstore.orders.service.OrderQueryService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.UUID;

@Service
@Transactional(readOnly = true)
@RequiredArgsConstructor
public class OrderQueryServiceImpl implements OrderQueryService {
    private final OrderRepository orderRepository;
    private final OrderItemRepository orderItemRepository;
    private final BookQueryService bookQueryService;
    private final OrderMapper mapper;

    @Override
    public Page<OrderCardResponseDto> getByClientId(UUID clientId, Pageable pageable) {
        return orderRepository.findAllCardsByClientId(clientId, pageable);
    }

    @Override
    public OrderDetailsResponseDto getByIdAndClientId(UUID id, UUID clientId, String lang) {
        Order order = orderRepository.findWithBooksByIdAndClientId(id, clientId).orElseThrow(
                OrderNotFoundException::new
        );

        return mapper.toOrderDetailsDto(
                order,
                bookQueryService.getTranslations(getBooks(order), lang)
        );
    }

    @Override
    public Page<AdminOrderCardResponseDto> getAll(OrderFilterRequestDto filter, Pageable pageable) {
        Page<Order> orders = orderRepository.findAll(
                OrderSpecifications.byFilter(filter),
                pageable
        );

        if (orders.isEmpty()) {
            return Page.empty(pageable);
        }

        return orders.map(mapper::toAdminOrderCardDto);
    }

    @Override
    public AdminOrderDetailsResponseDto getById(UUID id, String lang) {
        Order order = orderRepository.findFullById(id).orElseThrow(OrderNotFoundException::new);

        return mapper.toAdminOrderDetailsDto(
                order,
                bookQueryService.getTranslations(getBooks(order), lang)
        );
    }

    @Override
    public boolean hasBookBeenOrdered(UUID bookId) {
        return orderItemRepository.existsByBookId(bookId);
    }

    private List<Book> getBooks(Order order) {
        return order.getItems().stream()
                .map(OrderItem::getBook)
                .toList();
    }
}
