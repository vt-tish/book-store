package com.vttish.bookstore.employees.repository;

import com.vttish.bookstore.employees.entity.Employee;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.Optional;
import java.util.UUID;

public interface EmployeeRepository extends JpaRepository<Employee, UUID> {
    boolean existsByPhone(String phone);

    @Query("SELECT e FROM Employee e WHERE e.user.id = :userId")
    Optional<Employee> findByUserId(@Param("userId") UUID userId);
}
