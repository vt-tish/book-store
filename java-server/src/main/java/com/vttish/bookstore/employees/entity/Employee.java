package com.vttish.bookstore.employees.entity;

import com.vttish.bookstore.auth.entity.User;
import com.vttish.bookstore.common.entity.BaseEntity;
import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDate;

@Entity
@Table(name = "employees")
@Getter
@AllArgsConstructor
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class Employee extends BaseEntity {

    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id")
    private User user;

    @Setter
    private String phone;

    @Setter
    private LocalDate birthDate;
}
