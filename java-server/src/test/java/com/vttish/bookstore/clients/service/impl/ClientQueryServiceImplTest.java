package com.vttish.bookstore.clients.service.impl;

import com.vttish.bookstore.clients.dto.AdminClientResponseDto;
import com.vttish.bookstore.clients.entity.Client;
import com.vttish.bookstore.clients.mapper.ClientMapper;
import com.vttish.bookstore.clients.repository.ClientRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class ClientQueryServiceImplTest {

    @Mock
    private ClientRepository clientRepository;

    @Mock
    private ClientMapper mapper;

    @InjectMocks
    private ClientQueryServiceImpl clientQueryService;

    @Test
    void getAll_ShouldReturnMappedPageAndTranslateSort() {
        Pageable pageable = PageRequest.of(0, 10, Sort.by("email"));
        
        Client client = mock(Client.class);
        Page<Client> clientPage = new PageImpl<>(List.of(client));
        
        AdminClientResponseDto dto = mock(AdminClientResponseDto.class);
        
        when(clientRepository.findAll(any(Pageable.class))).thenReturn(clientPage);
        when(mapper.toAdminClientDto(client)).thenReturn(dto);

        Page<AdminClientResponseDto> result = clientQueryService.getAll(pageable);

        assertFalse(result.isEmpty());
        assertEquals(dto, result.getContent().get(0));
    }

    @Test
    void getAll_WhenEmpty_ShouldReturnEmptyPage() {
        Pageable pageable = PageRequest.of(0, 10);
        when(clientRepository.findAll(any(Pageable.class))).thenReturn(Page.empty());

        Page<AdminClientResponseDto> result = clientQueryService.getAll(pageable);

        assertTrue(result.isEmpty());
    }
}
