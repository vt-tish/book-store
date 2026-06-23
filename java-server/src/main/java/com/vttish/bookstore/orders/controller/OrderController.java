package com.vttish.bookstore.orders.controller;

import com.vttish.bookstore.common.constant.ApiRoutingConstants;
import com.vttish.bookstore.orders.dto.OrderCardResponseDto;
import com.vttish.bookstore.orders.dto.OrderDetailsResponseDto;
import com.vttish.bookstore.orders.service.OrderQueryService;
import com.vttish.bookstore.orders.service.OrderSubmissionService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

import java.net.URI;
import java.util.Locale;
import java.util.UUID;

@RestController
@RequestMapping(ApiRoutingConstants.API_V1 + "/orders")
@RequiredArgsConstructor
@PreAuthorize("hasRole('CLIENT')")
public class OrderController {
    private final OrderQueryService orderQueryService;
    private final OrderSubmissionService orderSubmissionService;

    @PostMapping
    public ResponseEntity<OrderDetailsResponseDto> submit(
            @AuthenticationPrincipal UUID clientId,
            Locale locale
    ) {
        OrderDetailsResponseDto createdOrder = orderSubmissionService.submitByClientId(
                clientId, locale.getLanguage()
        );

        URI uri = ServletUriComponentsBuilder
                .fromCurrentRequest()
                .path("/{id}")
                .buildAndExpand(createdOrder.id())
                .toUri();

        return ResponseEntity.created(uri).body(createdOrder);
    }

    @GetMapping
    public Page<OrderCardResponseDto> getAll(
            @AuthenticationPrincipal UUID clientId,
            Pageable pageable
    ) {
        return orderQueryService.getByClientId(clientId, pageable);
    }

    @GetMapping("/{id}")
    public OrderDetailsResponseDto get(
            @AuthenticationPrincipal UUID clientId,
            @PathVariable UUID id,
            Locale locale
    ) {
        return orderQueryService.getByIdAndClientId(id, clientId, locale.getLanguage());
    }
}
