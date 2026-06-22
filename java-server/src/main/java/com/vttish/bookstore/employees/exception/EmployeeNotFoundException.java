package com.vttish.bookstore.employees.exception;

import com.vttish.bookstore.common.exception.NotFoundException;

public class EmployeeNotFoundException extends NotFoundException {
    public EmployeeNotFoundException() {
        super("error.employees.employee.not_found");
    }
}
