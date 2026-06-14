package com.vttish.book_store.books.entity;

import com.vttish.book_store.books.entity.enums.AgeGroup;
import com.vttish.book_store.books.entity.enums.Language;
import com.vttish.book_store.common.entity.BaseEntity;
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
}
