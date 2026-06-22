package com.vttish.bookstore.clients.service;

import com.vttish.bookstore.clients.dto.AdminClientResponseDto;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

public interface ClientQueryService {
    Page<AdminClientResponseDto> getAll(Pageable pageable);
}
