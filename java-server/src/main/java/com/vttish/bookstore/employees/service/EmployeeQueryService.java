package com.vttish.bookstore.employees.service;

import com.vttish.bookstore.employees.dto.AdminEmployeeResponseDto;
import com.vttish.bookstore.employees.dto.EmployeeResponseDto;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.UUID;

public interface EmployeeQueryService {
    Page<AdminEmployeeResponseDto> getAll(Pageable pageable);
    EmployeeResponseDto getById(UUID id);
}
