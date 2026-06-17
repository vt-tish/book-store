package com.vttish.bookstore.orders.service;

import com.vttish.bookstore.orders.dto.OrderCardDto;
import com.vttish.bookstore.orders.dto.OrderDetailsDto;
import com.vttish.bookstore.orders.entity.enums.OrderStatus;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.UUID;

public interface OrderQueryService {
    Page<OrderCardDto> getByClientId(UUID clientId, Pageable pageable);
    OrderDetailsDto getByIdAndClientId(UUID id, UUID clientId);
    Page<OrderCardDto> getAll(UUID employeeId, OrderStatus status, Pageable pageable);
    OrderDetailsDto getById(UUID id);

    boolean hasBookBeenOrdered(UUID bookId);
}
