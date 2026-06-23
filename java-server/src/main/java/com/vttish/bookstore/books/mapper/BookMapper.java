package com.vttish.bookstore.books.mapper;

import com.vttish.bookstore.books.dto.*;
import com.vttish.bookstore.books.entity.Book;
import com.vttish.bookstore.books.entity.BookTranslation;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingConstants;
import org.mapstruct.MappingTarget;

@Mapper(componentModel = MappingConstants.ComponentModel.SPRING)
public interface BookMapper {

    @Mapping(target = "translations", ignore = true)
    Book toBook(BookRequestDto bookRequestDto);

    BookTranslation toBookTranslation(BookTranslationDto bookTranslationDto);

    @Mapping(target = "name", expression = "java(book.getName(lang, defaultLang))")
    @Mapping(target = "genre", expression = "java(book.getGenre(lang, defaultLang))")
    @Mapping(target = "author", expression = "java(book.getAuthor(lang, defaultLang))")
    @Mapping(target = "description", expression = "java(book.getDescription(lang, defaultLang))")
    @Mapping(target = "characteristics", expression = "java(book.getCharacteristics(lang, defaultLang))")
    AdminBookDetailsResponseDto toAdminBookDetails(Book book, String lang, String defaultLang);

    @Mapping(target = "name", expression = "java(book.getName(lang, defaultLang))")
    @Mapping(target = "genre", expression = "java(book.getGenre(lang, defaultLang))")
    @Mapping(target = "author", expression = "java(book.getAuthor(lang, defaultLang))")
    AdminBookCardResponseDto toAdminBookCard(Book book, String lang, String defaultLang);

    @Mapping(target = "name", expression = "java(book.getName(lang, defaultLang))")
    @Mapping(target = "genre", expression = "java(book.getGenre(lang, defaultLang))")
    @Mapping(target = "author", expression = "java(book.getAuthor(lang, defaultLang))")
    BookCardResponseDto toBookCard(Book book, String lang, String defaultLang);

    @Mapping(target = "id", ignore = true)
    @Mapping(target = "translations", ignore = true)
    void update(BookRequestDto bookRequestDto, @MappingTarget Book book);

    @Mapping(target = "id", ignore = true)
    @Mapping(target = "languageCode", ignore = true)
    @Mapping(target = "book", ignore = true)
    void updateTranslation(BookTranslationDto dto, @MappingTarget BookTranslation translation);
}
