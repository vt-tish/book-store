package com.vttish.bookstore.employees.service.impl;

import com.vttish.bookstore.employees.dto.AdminEmployeeResponseDto;
import com.vttish.bookstore.employees.dto.EmployeeResponseDto;
import com.vttish.bookstore.employees.entity.Employee;
import com.vttish.bookstore.employees.exception.EmployeeNotFoundException;
import com.vttish.bookstore.employees.mapper.EmployeeMapper;
import com.vttish.bookstore.employees.repository.EmployeeRepository;
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
import java.util.Optional;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class EmployeeQueryServiceImplTest {

    @Mock
    private EmployeeRepository employeeRepository;

    @Mock
    private EmployeeMapper mapper;

    @InjectMocks
    private EmployeeQueryServiceImpl employeeQueryService;

    @Test
    void getAll_ShouldReturnMappedPageAndTranslateSort() {
        Pageable pageable = PageRequest.of(0, 10, Sort.by("email", "isBlocked", "other"));
        
        Employee employee = mock(Employee.class);
        Page<Employee> employeePage = new PageImpl<>(List.of(employee));
        
        AdminEmployeeResponseDto dto = mock(AdminEmployeeResponseDto.class);
        
        when(employeeRepository.findAll(any(Pageable.class))).thenReturn(employeePage);
        when(mapper.toAdminEmployeeDto(employee)).thenReturn(dto);

        Page<AdminEmployeeResponseDto> result = employeeQueryService.getAll(pageable);

        assertFalse(result.isEmpty());
        assertEquals(dto, result.getContent().get(0));
    }

    @Test
    void getById_WhenExists_ShouldReturnDto() {
        UUID id = UUID.randomUUID();
        Employee employee = mock(Employee.class);
        EmployeeResponseDto dto = mock(EmployeeResponseDto.class);

        when(employeeRepository.findById(id)).thenReturn(Optional.of(employee));
        when(mapper.toEmployeeDto(employee)).thenReturn(dto);

        EmployeeResponseDto result = employeeQueryService.getById(id);

        assertEquals(dto, result);
    }

    @Test
    void getById_WhenNotExists_ShouldThrowException() {
        UUID id = UUID.randomUUID();
        when(employeeRepository.findById(id)).thenReturn(Optional.empty());

        assertThrows(EmployeeNotFoundException.class, () -> employeeQueryService.getById(id));
    }
}
