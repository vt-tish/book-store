package com.vttish.bookstore.employees.repository;

import com.vttish.bookstore.employees.entity.Employee;
import org.jspecify.annotations.NonNull;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.UUID;

public interface EmployeeRepository extends JpaRepository<Employee, UUID> {

    @EntityGraph(attributePaths = { "user" })
    @NonNull Page<Employee> findAll(@NonNull Pageable pageable);
}
