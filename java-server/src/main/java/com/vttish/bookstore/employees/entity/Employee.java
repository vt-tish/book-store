package com.vttish.bookstore.employees.entity;

import com.vttish.bookstore.auth.entity.User;
import com.vttish.bookstore.common.entity.BaseEntity;
import com.vttish.bookstore.common.entity.BaseTimestampEntity;
import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDate;
import java.util.UUID;

@Entity
@Table(name = "employees")
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class Employee extends BaseTimestampEntity {

    @Id
    private UUID id;

    @OneToOne(fetch = FetchType.LAZY)
    @MapsId
    @JoinColumn(name = "user_id")
    private User user;

    @Setter
    private String phone;

    @Setter
    private LocalDate birthDate;

    public Employee(User user, String phone, LocalDate birthDate) {
        this.user = user;
        this.phone = phone;
        this.birthDate = birthDate;
    }
}
