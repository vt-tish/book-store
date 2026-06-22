package com.vttish.bookstore.employees.service.impl;

import com.vttish.bookstore.employees.dto.AdminEmployeeResponseDto;
import com.vttish.bookstore.employees.dto.EmployeeResponseDto;
import com.vttish.bookstore.employees.entity.Employee;
import com.vttish.bookstore.employees.exception.EmployeeNotFoundException;
import com.vttish.bookstore.employees.mapper.EmployeeMapper;
import com.vttish.bookstore.employees.repository.EmployeeRepository;
import com.vttish.bookstore.employees.service.EmployeeQueryService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class EmployeeQueryServiceImpl implements EmployeeQueryService {
    private final EmployeeRepository employeeRepository;
    private final EmployeeMapper mapper;

    @Override
    public Page<AdminEmployeeResponseDto> getAll(Pageable pageable) {
        return employeeRepository.findAll(translatePageable(pageable))
                .map(mapper::toAdminEmployeeDto);
    }

    @Override
    public EmployeeResponseDto getByUserId(UUID userId) {
        Employee employee = employeeRepository.findByUserId(userId).orElseThrow(
            EmployeeNotFoundException::new
        );

        return mapper.toEmployeeDto(employee);
    }

    private Pageable translatePageable(Pageable pageable) {
        List<Sort.Order> orders = new ArrayList<>();

        for (Sort.Order order : pageable.getSort()) {
            String property = switch (order.getProperty()) {
                case "email" -> "user.email";
                case "isBlocked" -> "user.isBlocked";
                default -> order.getProperty();
            };

            orders.add(order.withProperty(property));
        }

        return PageRequest.of(
                pageable.getPageNumber(),
                pageable.getPageSize(),
                Sort.by(orders)
        );
    }
}
