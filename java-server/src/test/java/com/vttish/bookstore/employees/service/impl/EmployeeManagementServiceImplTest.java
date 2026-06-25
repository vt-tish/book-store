package com.vttish.bookstore.employees.service.impl;

import com.vttish.bookstore.auth.entity.User;
import com.vttish.bookstore.auth.service.AuthService;
import com.vttish.bookstore.employees.dto.AdminEmployeeResponseDto;
import com.vttish.bookstore.employees.dto.RegisterEmployeeRequestDto;
import com.vttish.bookstore.employees.dto.UpdateEmployeeRequestDto;
import com.vttish.bookstore.employees.dto.VerifyEmployeeRequestDto;
import com.vttish.bookstore.employees.entity.Employee;
import com.vttish.bookstore.employees.exception.EmployeeNotFoundException;
import com.vttish.bookstore.employees.exception.PhoneTakenException;
import com.vttish.bookstore.employees.mapper.EmployeeMapper;
import com.vttish.bookstore.employees.repository.EmployeeRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.dao.DataIntegrityViolationException;

import java.time.LocalDate;
import java.util.Optional;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class EmployeeManagementServiceImplTest {

    @Mock
    private EmployeeRepository employeeRepository;

    @Mock
    private AuthService authService;

    @Mock
    private EmployeeMapper mapper;

    @InjectMocks
    private EmployeeManagementServiceImpl employeeManagementService;

    @Test
    void register_ShouldRegisterUserAndSaveEmployee() {
        RegisterEmployeeRequestDto request = new RegisterEmployeeRequestDto(
                "email@test.com", "password", "1234567890", LocalDate.now()
        );
        User user = mock(User.class);
        
        when(authService.registerEmployee(request.email(), request.password())).thenReturn(user);

        employeeManagementService.register(request);

        verify(employeeRepository).saveAndFlush(any(Employee.class));
    }

    @Test
    void register_WhenPhoneTaken_ShouldThrowException() {
        RegisterEmployeeRequestDto request = new RegisterEmployeeRequestDto(
                "email@test.com", "password", "1234567890", LocalDate.now()
        );
        User user = mock(User.class);
        
        when(authService.registerEmployee(request.email(), request.password())).thenReturn(user);
        when(employeeRepository.saveAndFlush(any(Employee.class))).thenThrow(DataIntegrityViolationException.class);

        assertThrows(PhoneTakenException.class, () -> employeeManagementService.register(request));
    }

    @Test
    void verify_ShouldCallAuthService() {
        VerifyEmployeeRequestDto request = new VerifyEmployeeRequestDto("token");

        employeeManagementService.verify(request);

        verify(authService).verifyEmployee("token");
    }

    @Test
    void update_WhenExists_ShouldUpdateAndReturnDto() {
        UUID id = UUID.randomUUID();
        UpdateEmployeeRequestDto request = new UpdateEmployeeRequestDto("123", null);
        Employee employee = mock(Employee.class);
        AdminEmployeeResponseDto responseDto = mock(AdminEmployeeResponseDto.class);

        when(employeeRepository.findById(id)).thenReturn(Optional.of(employee));
        when(employeeRepository.save(employee)).thenReturn(employee);
        when(mapper.toAdminEmployeeDto(employee)).thenReturn(responseDto);

        AdminEmployeeResponseDto result = employeeManagementService.update(id, request);

        assertEquals(responseDto, result);
        verify(mapper).update(request, employee);
        verify(employeeRepository).save(employee);
    }

    @Test
    void update_WhenNotFound_ShouldThrowException() {
        UUID id = UUID.randomUUID();
        when(employeeRepository.findById(id)).thenReturn(Optional.empty());

        assertThrows(EmployeeNotFoundException.class, () ->
                employeeManagementService.update(id, mock(UpdateEmployeeRequestDto.class))
        );
    }
}
