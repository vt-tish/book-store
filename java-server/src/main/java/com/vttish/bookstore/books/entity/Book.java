package com.vttish.bookstore.books.entity;

import com.vttish.bookstore.books.entity.enums.AgeGroup;
import com.vttish.bookstore.books.entity.enums.Language;
import com.vttish.bookstore.common.entity.BaseEntity;
import jakarta.persistence.*;
import lombok.*;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.HashMap;
import java.util.Map;

@Entity
@Table(name = "books")
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class Book extends BaseEntity {
    private String previewUrl;

    @Enumerated(EnumType.STRING)
    private AgeGroup ageGroup;

    private BigDecimal price;
    private LocalDate publicationDate;
    private Integer pages;

    @Enumerated(EnumType.STRING)
    private Language language;

    private boolean isArchived = false;

    @OneToMany(mappedBy = "book", cascade = CascadeType.ALL, orphanRemoval = true)
    @MapKey(name = "languageCode")
    private Map<String, BookTranslation> translations = new HashMap<>();

    public void addTranslation(String lang, BookTranslation translation) {
        translation.setLanguageCode(lang);
        translation.setBook(this);
        translations.put(lang, translation);
    }

    public String getName(String lang, String defaultLang) {
        return getTranslation(lang, defaultLang).getName();
    }

    public String getGenre(String lang, String defaultLang) {
        return getTranslation(lang, defaultLang).getGenre();
    }

    public String getAuthor(String lang, String defaultLang) {
        return getTranslation(lang, defaultLang).getAuthor();
    }

    public String getCharacteristics(String lang, String defaultLang) {
        return getTranslation(lang, defaultLang).getCharacteristics();
    }

    public String getDescription(String lang, String defaultLang) {
        return getTranslation(lang, defaultLang).getDescription();
    }

    private BookTranslation getTranslation(String lang, String defaultLang) {
        return translations.getOrDefault(lang, translations.get(defaultLang));
    }
}
