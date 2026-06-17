package com.vttish.bookstore.common.entity;

import jakarta.persistence.*;
import lombok.EqualsAndHashCode;
import lombok.Getter;

import java.time.Instant;
import java.util.UUID;

@MappedSuperclass
@Getter
@EqualsAndHashCode(of = "id")
public abstract class BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    private Instant createdAt;
    private Instant updatedAt;

    @PrePersist
    protected void onCreate() {
        createdAt = Instant.now();
        updatedAt = Instant.now();
    }

    @PreUpdate
    protected void onUpdate() {
        updatedAt = Instant.now();
    }
}
