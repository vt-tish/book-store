package com.vttish.book_store.books.mapper;

import com.vttish.book_store.books.dto.BookCardDto;
import com.vttish.book_store.books.dto.BookDetailsDto;
import com.vttish.book_store.books.dto.BookDto;
import com.vttish.book_store.books.entity.Book;
import org.mapstruct.Mapper;
import org.mapstruct.MappingConstants;
import org.mapstruct.MappingTarget;

@Mapper(componentModel = MappingConstants.ComponentModel.SPRING)
public interface BookMapper {
    BookDetailsDto toBookDetailsDto(Book book);
    BookCardDto toBookCardDto(Book book);
    Book toBook(BookDto bookDto);

    void update(BookDto bookDto, @MappingTarget Book book);
}
