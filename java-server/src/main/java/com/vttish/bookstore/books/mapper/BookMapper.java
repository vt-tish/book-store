package com.vttish.bookstore.books.mapper;

import com.vttish.bookstore.books.dto.*;
import com.vttish.bookstore.books.entity.Book;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingConstants;
import org.mapstruct.MappingTarget;

@Mapper(componentModel = MappingConstants.ComponentModel.SPRING)
public interface BookMapper {

    @Mapping(target = "isAvailable", expression = "java(!book.isArchived())")
    BookDetailsResponseDto toBookDetailsDto(Book book);

    BookCardResponseDto toBookCardDto(Book book);
    Book toBook(BookRequestDto bookRequestDto);
    AdminBookDetailsResponseDto toAminBookDetailsDto(Book book);
    AdminBookCardResponseDto toAdminBookCardDto(Book book);

    void update(BookRequestDto bookRequestDto, @MappingTarget Book book);
}
