package com.vttish.bookstore.employees.service.impl;

import com.vttish.bookstore.auth.dto.RegisterRequestDto;
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
import com.vttish.bookstore.employees.service.EmployeeManagementService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.UUID;

@Service
@RequiredArgsConstructor
public class EmployeeManagementServiceImpl implements EmployeeManagementService {
    private final EmployeeRepository employeeRepository;
    private final AuthService authService;
    private final EmployeeMapper mapper;

    @Override
    @Transactional
    public void register(RegisterEmployeeRequestDto registerEmployeeRequestDto) {
        if (employeeRepository.existsByPhone(registerEmployeeRequestDto.phone())) {
            throw new PhoneTakenException();
        }

        User user = authService.registerEmployee(
                registerEmployeeRequestDto.email(),
                registerEmployeeRequestDto.password()
        );

        employeeRepository.save(new Employee(
                user,
                registerEmployeeRequestDto.phone(),
                registerEmployeeRequestDto.birthDate()
        ));
    }

    @Override
    @Transactional
    public void verify(VerifyEmployeeRequestDto verifyEmployeeRequestDto) {
        authService.verifyEmployee(verifyEmployeeRequestDto.token());
    }

    @Override
    @Transactional
    public AdminEmployeeResponseDto update(UUID employeeId, UpdateEmployeeRequestDto updateEmployeeRequestDto) {
        Employee employee = getEntityById(employeeId);

        mapper.update(updateEmployeeRequestDto, employee);
        return mapper.toAdminEmployeeDto(employeeRepository.save(employee));
    }

    @Override
    @Transactional
    public void block(UUID employeeId) {
        Employee employee = getEntityById(employeeId);

        if (employee.getUser().isBlocked()) {
            return;
        }

        employee.getUser().setBlocked(true);
        employeeRepository.save(employee);
    }

    @Override
    @Transactional
    public void unblock(UUID employeeId) {
        Employee employee = getEntityById(employeeId);

        if (!employee.getUser().isBlocked()) {
            return;
        }

        employee.getUser().setBlocked(false);
        employeeRepository.save(employee);
    }

    private Employee getEntityById(UUID employeeId) {
        return employeeRepository.findById(employeeId).orElseThrow(
                EmployeeNotFoundException::new
        );
    }
}
