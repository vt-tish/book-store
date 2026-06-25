package com.vttish.bookstore.orders.service;

import com.vttish.bookstore.orders.dto.*;
import com.vttish.bookstore.orders.entity.enums.OrderStatus;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.UUID;

public interface OrderQueryService {
    Page<OrderCardResponseDto> getByClientId(UUID clientId, Pageable pageable);
    OrderDetailsResponseDto getByIdAndClientId(UUID id, UUID clientId, String lang);
    Page<AdminOrderCardResponseDto> getAll(OrderFilterRequestDto filter, Pageable pageable);
    AdminOrderDetailsResponseDto getById(UUID id, String lang);

    boolean hasBookBeenOrdered(UUID bookId);
}
