package com.vttish.bookstore.books.service.impl;

import com.vttish.bookstore.books.dto.AdminBookCardResponseDto;
import com.vttish.bookstore.books.dto.AdminBookDetailsResponseDto;
import com.vttish.bookstore.books.dto.BookCardResponseDto;
import com.vttish.bookstore.books.dto.BookDetailsResponseDto;
import com.vttish.bookstore.books.dto.BookFilterRequestDto;
import com.vttish.bookstore.books.entity.Book;
import com.vttish.bookstore.books.entity.BookTranslation;
import com.vttish.bookstore.books.exception.BookNotFoundException;
import com.vttish.bookstore.books.mapper.BookMapper;
import com.vttish.bookstore.books.repository.BookRepository;
import com.vttish.bookstore.books.repository.BookTranslationRepository;
import com.vttish.bookstore.common.config.LocalizationProperties;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;

import java.math.BigDecimal;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyList;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class BookQueryServiceImplTest {

    @Mock
    private BookRepository bookRepository;
    @Mock
    private BookTranslationRepository bookTranslationRepository;
    @Mock
    private LocalizationProperties localizationProps;
    @Mock
    private BookMapper mapper;

    @InjectMocks
    private BookQueryServiceImpl bookQueryService;

    @Test
    void getAvailable_WhenBooksExist_ShouldReturnPageOfDto() {
        BookFilterRequestDto filter = new BookFilterRequestDto("search", BigDecimal.ZERO, BigDecimal.TEN);
        Pageable pageable = PageRequest.of(0, 10);
        Book book = mock(Book.class);
        UUID bookId = UUID.randomUUID();
        when(book.getId()).thenReturn(bookId);
        Page<Book> bookPage = new PageImpl<>(List.of(book));

        BookTranslation translation = mock(BookTranslation.class);

        BookCardResponseDto responseDto = mock(BookCardResponseDto.class);

        when(bookRepository.findAll(any(Specification.class), eq(pageable))).thenReturn(bookPage);
        when(localizationProps.resolveLanguage("en")).thenReturn("en");
        when(localizationProps.defaultLanguage()).thenReturn("en");
        when(bookTranslationRepository.findByBookInAndLanguageCodeIn(
                anyList(), anyList())).thenReturn(List.of(translation)
        );
        when(mapper.toBookCardDto(book, translation)).thenReturn(responseDto);

        // the translation getBook is called to build the map in getTranslations
        when(translation.getBook()).thenReturn(book);

        Page<BookCardResponseDto> result = bookQueryService.getAvailable(filter, "en", pageable);

        assertFalse(result.isEmpty());
        assertEquals(responseDto, result.getContent().get(0));
    }

    @Test
    void getAvailable_WhenNoBooks_ShouldReturnEmptyPage() {
        Pageable pageable = PageRequest.of(0, 10);
        when(bookRepository.findAll(any(Specification.class), eq(pageable))).thenReturn(Page.empty());

        Page<BookCardResponseDto> result = bookQueryService.getAvailable(
                mock(BookFilterRequestDto.class), "en", pageable
        );

        assertTrue(result.isEmpty());
    }

    @Test
    void getAll_WhenBooksExist_ShouldReturnPageOfAdminDto() {
        BookFilterRequestDto filter = new BookFilterRequestDto("search", BigDecimal.ZERO, BigDecimal.TEN);
        Pageable pageable = PageRequest.of(0, 10);
        Book book = mock(Book.class);
        UUID bookId = UUID.randomUUID();
        when(book.getId()).thenReturn(bookId);
        Page<Book> bookPage = new PageImpl<>(List.of(book));

        BookTranslation translation = mock(BookTranslation.class);

        AdminBookCardResponseDto responseDto = mock(AdminBookCardResponseDto.class);

        when(bookRepository.findAll(any(Specification.class), eq(pageable))).thenReturn(bookPage);
        when(localizationProps.resolveLanguage("en")).thenReturn("en");
        when(localizationProps.defaultLanguage()).thenReturn("en");
        when(bookTranslationRepository.findByBookInAndLanguageCodeIn(anyList(), anyList()))
                .thenReturn(List.of(translation));
        when(mapper.toAdminBookCardDto(book, translation)).thenReturn(responseDto);

        when(translation.getBook()).thenReturn(book);

        Page<AdminBookCardResponseDto> result = bookQueryService.getAll(filter, "en", pageable);

        assertFalse(result.isEmpty());
        assertEquals(responseDto, result.getContent().get(0));
    }

    @Test
    void getAll_WhenNoBooks_ShouldReturnEmptyPage() {
        Pageable pageable = PageRequest.of(0, 10);
        when(bookRepository.findAll(any(Specification.class), eq(pageable))).thenReturn(Page.empty());

        Page<AdminBookCardResponseDto> result = bookQueryService.getAll(
                mock(BookFilterRequestDto.class), "en", pageable
        );

        assertTrue(result.isEmpty());
    }

    @Test
    void getById_WhenExists_ShouldReturnDto() {
        UUID id = UUID.randomUUID();
        Book book = mock(Book.class);
        when(book.getId()).thenReturn(id);
        BookTranslation translation = mock(BookTranslation.class);
        
        BookDetailsResponseDto responseDto = mock(BookDetailsResponseDto.class);

        when(bookRepository.findById(id)).thenReturn(Optional.of(book));
        when(localizationProps.resolveLanguage("en")).thenReturn("en");
        when(localizationProps.defaultLanguage()).thenReturn("en");
        when(bookTranslationRepository.findByBookInAndLanguageCodeIn(anyList(), anyList()))
                .thenReturn(List.of(translation));
        when(mapper.toBookDetailsDto(book, translation)).thenReturn(responseDto);

        when(translation.getBook()).thenReturn(book);

        BookDetailsResponseDto result = bookQueryService.getById(id, "en");

        assertEquals(responseDto, result);
    }

    @Test
    void getByIdAdmin_WhenExists_ShouldReturnAdminDto() {
        UUID id = UUID.randomUUID();
        Book book = mock(Book.class);
        when(book.getId()).thenReturn(id);
        BookTranslation translation = mock(BookTranslation.class);
        
        AdminBookDetailsResponseDto responseDto = mock(AdminBookDetailsResponseDto.class);

        when(bookRepository.findById(id)).thenReturn(Optional.of(book));
        when(localizationProps.resolveLanguage("en")).thenReturn("en");
        when(localizationProps.defaultLanguage()).thenReturn("en");
        when(bookTranslationRepository.findByBookInAndLanguageCodeIn(anyList(), anyList()))
                .thenReturn(List.of(translation));
        when(mapper.toAdminBookDetailsDto(book)).thenReturn(responseDto);

        when(translation.getBook()).thenReturn(book);

        AdminBookDetailsResponseDto result = bookQueryService.getByIdAdmin(id);

        assertEquals(responseDto, result);
    }

    @Test
    void getAvailableById_WhenAvailable_ShouldReturnBook() {
        UUID id = UUID.randomUUID();
        Book book = mock(Book.class);
        when(bookRepository.findByIdAndIsArchivedFalse(id)).thenReturn(Optional.of(book));

        Book result = bookQueryService.getAvailableById(id);

        assertEquals(book, result);
    }

    @Test
    void getAvailableById_WhenNotAvailable_ShouldThrowException() {
        UUID id = UUID.randomUUID();
        when(bookRepository.findByIdAndIsArchivedFalse(id)).thenReturn(Optional.empty());

        assertThrows(BookNotFoundException.class, () -> bookQueryService.getAvailableById(id));
    }

    @Test
    void getTranslations_WhenMultipleTranslationsExist_AndPreferredIsFirst_ShouldReturnPreferred() {
        Book book = mock(Book.class);
        UUID bookId = UUID.randomUUID();
        when(book.getId()).thenReturn(bookId);
        BookTranslation preferred = mock(BookTranslation.class);
        when(preferred.getLanguageCode()).thenReturn("es");
        when(preferred.getBook()).thenReturn(book);
        BookTranslation defaultTrans = mock(BookTranslation.class);
        when(defaultTrans.getBook()).thenReturn(book);
        when(localizationProps.resolveLanguage("es")).thenReturn("es");
        when(localizationProps.defaultLanguage()).thenReturn("en");
        when(bookTranslationRepository.findByBookInAndLanguageCodeIn(anyList(), anyList()))
            .thenReturn(List.of(preferred, defaultTrans));
        Map<UUID, BookTranslation> result = bookQueryService.getTranslations(List.of(book), "es");
        assertEquals(1, result.size());
        assertEquals(preferred, result.get(bookId));
    }

    @Test
    void getTranslations_WhenMultipleTranslationsExist_AndDefaultIsFirst_ShouldReturnPreferred() {
        Book book = mock(Book.class);
        UUID bookId = UUID.randomUUID();

        when(book.getId()).thenReturn(bookId);

        BookTranslation preferred = mock(BookTranslation.class);

        when(preferred.getBook()).thenReturn(book);

        BookTranslation defaultTrans = mock(BookTranslation.class);

        when(defaultTrans.getLanguageCode()).thenReturn("en");
        when(defaultTrans.getBook()).thenReturn(book);
        when(localizationProps.resolveLanguage("es")).thenReturn("es");
        when(localizationProps.defaultLanguage()).thenReturn("en");
        when(bookTranslationRepository.findByBookInAndLanguageCodeIn(anyList(), anyList()))
            .thenReturn(List.of(defaultTrans, preferred));

        Map<UUID, BookTranslation> result = bookQueryService.getTranslations(List.of(book), "es");

        assertEquals(1, result.size());
        assertEquals(preferred, result.get(bookId));
    }
}
