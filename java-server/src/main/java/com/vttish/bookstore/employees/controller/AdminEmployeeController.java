package com.vttish.bookstore.employees.controller;

import com.vttish.bookstore.common.constant.ApiRoutingConstants;
import com.vttish.bookstore.common.dto.ResponseDto;
import com.vttish.bookstore.employees.dto.AdminEmployeeResponseDto;
import com.vttish.bookstore.employees.dto.RegisterEmployeeRequestDto;
import com.vttish.bookstore.employees.dto.UpdateEmployeeRequestDto;
import com.vttish.bookstore.employees.dto.VerifyEmployeeRequestDto;
import com.vttish.bookstore.employees.service.EmployeeManagementService;
import com.vttish.bookstore.employees.service.EmployeeQueryService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.context.MessageSource;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.HttpStatus;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.Locale;
import java.util.UUID;

@RestController
@RequestMapping(ApiRoutingConstants.API_V1 + "/admin/employees")
@Validated
@RequiredArgsConstructor
@PreAuthorize("hasRole('ADMIN')")
public class AdminEmployeeController {
    private final EmployeeManagementService employeeManagementService;
    private final EmployeeQueryService employeeQueryService;
    private final MessageSource messageSource;

    @PostMapping("/register")
    @ResponseStatus(HttpStatus.CREATED)
    public ResponseDto register(
            @Valid @RequestBody RegisterEmployeeRequestDto registerRequestDto,
            Locale locale
    ) {
        employeeManagementService.register(registerRequestDto);
        return new ResponseDto(
                messageSource.getMessage("success.auth.register", null, locale)
        );
    }

    @PostMapping("/verify")
    public ResponseDto verify(
            @Valid @RequestBody VerifyEmployeeRequestDto verifyEmployeeRequestDto,
            Locale locale
    ) {
        employeeManagementService.verify(verifyEmployeeRequestDto);
        return new ResponseDto(
                messageSource.getMessage("success.auth.verify", null, locale)
        );
    }

    @GetMapping
    public Page<AdminEmployeeResponseDto> getAll(
            @PageableDefault(sort = "isBlocked", direction = Sort.Direction.ASC) Pageable pageable
    ) {
        return employeeQueryService.getAll(pageable);
    }

    @PutMapping("/{id}")
    public AdminEmployeeResponseDto update(
            @PathVariable UUID id,
            @Valid @RequestBody UpdateEmployeeRequestDto updateEmployeeRequestDto
    ) {
        return employeeManagementService.update(id, updateEmployeeRequestDto);
    }

    @PutMapping("/{id}/block")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void block(@PathVariable UUID id) {
        employeeManagementService.block(id);
    }

    @PutMapping("/{id}/unblock")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void unblock(@PathVariable UUID id) {
        employeeManagementService.unblock(id);
    }
}
