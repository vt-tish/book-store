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
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class User extends BaseEntity {
    private String email;
    private String password;

    @Enumerated(value = EnumType.STRING)
    private Role role;

    @Setter(value = AccessLevel.NONE)
    private boolean isVerified = false;

    private boolean isBlocked = false;

    public User(String email, String password, Role role) {
        this.email = email;
        this.password = password;
        this.role = role;
    }

    public void verify() {
        isVerified = true;
    }
}
