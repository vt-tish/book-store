package com.vttish.bookstore.employees.controller;

import com.vttish.bookstore.common.constant.ApiRoutingConstants;
import com.vttish.bookstore.employees.dto.EmployeeResponseDto;
import com.vttish.bookstore.employees.service.EmployeeQueryService;
import lombok.RequiredArgsConstructor;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.UUID;

@RestController
@RequestMapping(ApiRoutingConstants.API_V1 + "/employees")
@RequiredArgsConstructor
@PreAuthorize("hasRole('EMPLOYEE')")
public class EmployeeController {
    private final EmployeeQueryService employeeQueryService;

    @GetMapping("/me")
    public EmployeeResponseDto profile(@AuthenticationPrincipal UUID employeeId) {
        return employeeQueryService.getById(employeeId);
    }
}
