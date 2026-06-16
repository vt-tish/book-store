package com.vttish.bookstore.books.entity;

import com.vttish.bookstore.books.entity.enums.AgeGroup;
import com.vttish.bookstore.books.entity.enums.Language;
import com.vttish.bookstore.common.entity.BaseEntity;
import jakarta.persistence.*;
import lombok.*;

import java.math.BigDecimal;
import java.time.LocalDate;

@Entity
@Table(name = "books")
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class Book extends BaseEntity {
    private String name;
    private String genre;
    private String previewUrl;

    @Enumerated(EnumType.STRING)
    private AgeGroup ageGroup;

    private BigDecimal price;
    private LocalDate publicationDate;
    private String author;
    private Integer pages;
    private String characteristics;
    private String description;

    @Enumerated(EnumType.STRING)
    private Language language;

    private boolean isArchived = false;
}
