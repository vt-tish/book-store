package com.vttish.bookstore.orders.service;

import com.vttish.bookstore.orders.dto.OrderDetailsDto;

import java.util.UUID;

public interface OrderSubmissionService {
    OrderDetailsDto submitByClientId(UUID clientId);
}
