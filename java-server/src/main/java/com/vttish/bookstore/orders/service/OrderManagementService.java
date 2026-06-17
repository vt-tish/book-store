package com.vttish.bookstore.orders.service;

import java.util.UUID;

public interface OrderManagementService {
    void accept(UUID employeeId, UUID id);
    void cancel(UUID employeeId, UUID id);
    void complete(UUID employeeId, UUID id);
}
