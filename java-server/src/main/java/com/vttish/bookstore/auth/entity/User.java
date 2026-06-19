package com.vttish.bookstore.auth.entity;

import com.vttish.bookstore.auth.entity.enums.Role;
import com.vttish.bookstore.common.entity.BaseEntity;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.Table;
import lombok.*;

@Entity
@Table(name = "users")
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class User extends BaseEntity {
    private String email;
    private String password;

    @Enumerated(value = EnumType.STRING)
    private Role role;
}
