package com.vttish.bookstore.common.entity;

import jakarta.persistence.*;
import lombok.EqualsAndHashCode;
import lombok.Getter;

import java.util.UUID;

@MappedSuperclass
@Getter
@EqualsAndHashCode(of = "id", callSuper = false)
public abstract class BaseEntity extends BaseTimestampEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

}
