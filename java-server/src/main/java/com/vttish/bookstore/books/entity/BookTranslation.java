package com.vttish.bookstore.books.entity;

import com.vttish.bookstore.common.entity.BaseEntity;
import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "book_translations")
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class BookTranslation extends BaseEntity {

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "book_id")
    private Book book;

    private String languageCode;

    private String name;
    private String genre;
    private String author;
    private String characteristics;
    private String description;
}
