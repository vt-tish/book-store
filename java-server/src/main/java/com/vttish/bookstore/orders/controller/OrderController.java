package com.vttish.bookstore.orders.controller;

import com.vttish.bookstore.orders.dto.OrderCardDto;
import com.vttish.bookstore.orders.dto.OrderDetailsDto;
import com.vttish.bookstore.orders.service.OrderQueryService;
import com.vttish.bookstore.orders.service.OrderSubmissionService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

import java.net.URI;
import java.util.UUID;

@RestController
@RequestMapping("/v1/orders")
@RequiredArgsConstructor
public class OrderController {
    private final OrderQueryService orderQueryService;
    private final OrderSubmissionService orderSubmissionService;

    @PostMapping
    public ResponseEntity<OrderDetailsDto> submit(UUID clientId) {
        OrderDetailsDto createdOrder = orderSubmissionService.submitByClientId(clientId);

        URI uri = ServletUriComponentsBuilder
                .fromCurrentRequest()
                .path("{id}")
                .buildAndExpand(createdOrder.id())
                .toUri();

        return ResponseEntity.created(uri).body(createdOrder);
    }

    @GetMapping
    public Page<OrderCardDto> getAll(UUID clientId, Pageable pageable) {
        return orderQueryService.getByClientId(clientId, pageable);
    }

    @GetMapping("{id}")
    public OrderDetailsDto get(@PathVariable UUID id, UUID clientId) {
        return orderQueryService.getByIdAndClientId(id, clientId);
    }
}
