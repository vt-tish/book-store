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

    @Mapping(target = "isArchived", source = "archived")
    AdminBookDetailsResponseDto toAdminBookDetailsDto(Book book);

    BookTranslationDto toBookTranslationDto(BookTranslation translation);

    @Mapping(target = "id", source = "book.id")
    @Mapping(target = "isArchived", source = "book.archived")
    AdminBookCardResponseDto toAdminBookCardDto(Book book, BookTranslation translation);

    @Mapping(target = "id", source = "book.id")
    @Mapping(target = "isAvailable", expression = "java(!book.isArchived())")
    BookDetailsResponseDto toBookDetailsDto(Book book, BookTranslation translation);

    @Mapping(target = "id", source = "book.id")
    BookCardResponseDto toBookCardDto(Book book, BookTranslation translation);

    @Mapping(target = "id", ignore = true)
    @Mapping(target = "translations", ignore = true)
    void update(BookRequestDto bookRequestDto, @MappingTarget Book book);

    @Mapping(target = "id", ignore = true)
    @Mapping(target = "languageCode", ignore = true)
    @Mapping(target = "book", ignore = true)
    void updateTranslation(BookTranslationDto dto, @MappingTarget BookTranslation translation);
}
