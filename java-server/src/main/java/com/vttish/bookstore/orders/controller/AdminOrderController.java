package com.vttish.bookstore.orders.controller;

import com.vttish.bookstore.common.constant.ApiRoutingConstants;
import com.vttish.bookstore.orders.dto.OrderCardResponseDto;
import com.vttish.bookstore.orders.dto.OrderDetailsResponseDto;
import com.vttish.bookstore.orders.entity.enums.OrderStatus;
import com.vttish.bookstore.orders.service.OrderManagementService;
import com.vttish.bookstore.orders.service.OrderQueryService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;

@RestController
@RequestMapping(ApiRoutingConstants.API_V1 + "/admin/orders")
@RequiredArgsConstructor
@PreAuthorize("hasAnyRole('ADMIN', 'EMPLOYEE')")
public class AdminOrderController {
    private final OrderManagementService orderManagementService;
    private final OrderQueryService orderQueryService;

    @GetMapping
    public Page<OrderCardResponseDto> getAll(
            @RequestParam(required = false) UUID employeeId,
            @RequestParam(required = false) OrderStatus status,
            Pageable pageable
    ) {
        return orderQueryService.getAll(employeeId, status, pageable);
    }

    @GetMapping("/{id}")
    public OrderDetailsResponseDto getById(@PathVariable UUID id) {
        return orderQueryService.getById(id);
    }

    @PatchMapping("/{id}/accept")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void acceptOrder(@PathVariable UUID id, @AuthenticationPrincipal UUID employeeId) {
        orderManagementService.accept(employeeId, id);
    }

    @PatchMapping("/{id}/complete")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void completeOrder(@PathVariable UUID id, @AuthenticationPrincipal UUID employeeId) {
        orderManagementService.complete(employeeId, id);
    }

    @PatchMapping("/{id}/cancel")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void cancelOrder(@PathVariable UUID id, @AuthenticationPrincipal UUID employeeId) {
        orderManagementService.cancel(employeeId, id);
    }
}
