package com.vttish.bookstore.orders.service;

import com.vttish.bookstore.orders.dto.OrderDetailsResponseDto;

import java.util.UUID;

public interface OrderSubmissionService {
    OrderDetailsResponseDto submitByClientId(UUID clientId);
}
