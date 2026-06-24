package com.vttish.bookstore.employees.mapper;

import com.vttish.bookstore.employees.dto.AdminEmployeeResponseDto;
import com.vttish.bookstore.employees.dto.EmployeeResponseDto;
import com.vttish.bookstore.employees.dto.UpdateEmployeeRequestDto;
import com.vttish.bookstore.employees.entity.Employee;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingConstants;
import org.mapstruct.MappingTarget;

@Mapper(componentModel = MappingConstants.ComponentModel.SPRING)
public interface EmployeeMapper {

    @Mapping(target = "email", source = "user.email")
    EmployeeResponseDto toEmployeeDto(Employee employee);

    @Mapping(target = "email", source = "user.email")
    @Mapping(target = "isBlocked", source = "user.blocked")
    AdminEmployeeResponseDto toAdminEmployeeDto(Employee employee);

    void update(UpdateEmployeeRequestDto updateEmployeeRequestDto, @MappingTarget Employee employee);
}
