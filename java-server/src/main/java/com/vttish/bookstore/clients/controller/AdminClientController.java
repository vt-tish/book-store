package com.vttish.bookstore.clients.controller;

import com.vttish.bookstore.auth.service.UserService;
import com.vttish.bookstore.clients.dto.AdminClientResponseDto;
import com.vttish.bookstore.clients.service.ClientQueryService;
import com.vttish.bookstore.common.constant.ApiRoutingConstants;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.HttpStatus;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;

@RestController
@RequestMapping(ApiRoutingConstants.API_V1 + "/admin/clients")
@RequiredArgsConstructor
@PreAuthorize("hasAnyRole('EMPLOYEE', 'ADMIN')")
public class AdminClientController {
    private final ClientQueryService clientQueryService;
    private final UserService userService;

    @GetMapping
    public Page<AdminClientResponseDto> getAll(
            @PageableDefault(sort = "isBlocked", direction = Sort.Direction.ASC) Pageable pageable
    ) {
        return clientQueryService.getAll(pageable);
    }

    @PutMapping("/{id}/block")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void block(@PathVariable UUID id) {
        userService.block(id);
    }

    @PutMapping("/{id}/unblock")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void unblock(@PathVariable UUID id) {
        userService.unblock(id);
    }
}
