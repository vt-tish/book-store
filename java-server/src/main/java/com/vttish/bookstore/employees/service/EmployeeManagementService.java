package com.vttish.bookstore.employees.service;

import com.vttish.bookstore.employees.dto.AdminEmployeeResponseDto;
import com.vttish.bookstore.employees.dto.RegisterEmployeeRequestDto;
import com.vttish.bookstore.employees.dto.UpdateEmployeeRequestDto;
import com.vttish.bookstore.employees.dto.VerifyEmployeeRequestDto;

import java.util.UUID;

public interface EmployeeManagementService {
    void register(RegisterEmployeeRequestDto registerEmployeeRequestDto);
    void verify(VerifyEmployeeRequestDto verifyEmployeeRequestDto);
    AdminEmployeeResponseDto update(UUID employeeId, UpdateEmployeeRequestDto updateEmployeeRequestDto);
    void block(UUID employeeId);
    void unblock(UUID employeeId);
}
