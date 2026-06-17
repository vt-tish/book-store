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
    BookDetailsDto toBookDetailsDto(Book book);

    BookCardDto toBookCardDto(Book book);
    Book toBook(BookDto bookDto);
    AdminBookDetailsDto toAminBookDetailsDto(Book book);
    AdminBookCardDto toAdminBookCardDto(Book book);

    void update(BookDto bookDto, @MappingTarget Book book);
}
