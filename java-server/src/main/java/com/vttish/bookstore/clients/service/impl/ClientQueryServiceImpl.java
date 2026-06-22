package com.vttish.bookstore.clients.service.impl;

import com.vttish.bookstore.clients.dto.AdminClientResponseDto;
import com.vttish.bookstore.clients.mapper.ClientMapper;
import com.vttish.bookstore.clients.repository.ClientRepository;
import com.vttish.bookstore.clients.service.ClientQueryService;
import com.vttish.bookstore.common.util.PaginationUtils;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Map;

@Service
@Transactional(readOnly = true)
@RequiredArgsConstructor
public class ClientQueryServiceImpl implements ClientQueryService {
    private final ClientRepository clientRepository;
    private final ClientMapper mapper;

    @Override
    public Page<AdminClientResponseDto> getAll(Pageable pageable) {
        Pageable translated = PaginationUtils.translate(pageable, Map.of(
                "email", "user.email",
                "isBlocked", "user.isBlocked"
        ));
        return clientRepository.findAll(translated).map(mapper::toAdminClientDto);
    }
}
