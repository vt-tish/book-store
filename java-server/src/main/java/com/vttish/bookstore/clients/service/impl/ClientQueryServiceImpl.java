package com.vttish.bookstore.clients.service.impl;

import com.vttish.bookstore.auth.entity.enums.Role;
import com.vttish.bookstore.auth.service.UserService;
import com.vttish.bookstore.clients.dto.AdminClientResponseDto;
import com.vttish.bookstore.clients.entity.Client;
import com.vttish.bookstore.clients.mapper.ClientMapper;
import com.vttish.bookstore.clients.repository.ClientRepository;
import com.vttish.bookstore.clients.service.ClientQueryService;
import com.vttish.bookstore.common.events.UserRegisteredEvent;
import com.vttish.bookstore.common.util.PaginationUtils;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.transaction.event.TransactionPhase;
import org.springframework.transaction.event.TransactionalEventListener;

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
