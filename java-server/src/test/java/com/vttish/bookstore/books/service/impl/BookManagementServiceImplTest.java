package com.vttish.bookstore.books.service.impl;

import com.vttish.bookstore.books.dto.AdminBookDetailsResponseDto;
import com.vttish.bookstore.books.dto.BookRequestDto;
import com.vttish.bookstore.books.dto.BookTranslationDto;
import com.vttish.bookstore.books.entity.Book;
import com.vttish.bookstore.books.entity.BookTranslation;
import com.vttish.bookstore.books.entity.enums.AgeGroup;
import com.vttish.bookstore.books.entity.enums.Language;
import com.vttish.bookstore.books.exception.BookHasOrdersException;
import com.vttish.bookstore.books.exception.BookNotFoundException;
import com.vttish.bookstore.books.exception.BookWithinCartException;
import com.vttish.bookstore.books.mapper.BookMapper;
import com.vttish.bookstore.books.repository.BookRepository;
import com.vttish.bookstore.cart.service.CartQueryService;
import com.vttish.bookstore.common.config.LocalizationProperties;
import com.vttish.bookstore.orders.service.OrderQueryService;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class BookManagementServiceImplTest {

    @Mock
    private BookRepository bookRepository;

    @Mock
    private OrderQueryService orderQueryService;

    @Mock
    private CartQueryService cartQueryService;

    @Mock
    private LocalizationProperties localizationProps;

    @Mock
    private BookMapper mapper;

    @InjectMocks
    private BookManagementServiceImpl bookManagementService;

    @Test
    void create_ShouldSaveBookAndReturnDto() {
        BookRequestDto request = new BookRequestDto(
                "url", AgeGroup.ADULT, BigDecimal.TEN, LocalDate.now(), 100, Language.ENGLISH,
                Map.of("en", new BookTranslationDto(
                        "Title", "Author", "Desc", "Pub", "Desc")
                )
        );
        Book book = mock(Book.class);
        BookTranslation translation = mock(BookTranslation.class);
        AdminBookDetailsResponseDto responseDto = mock(AdminBookDetailsResponseDto.class);

        when(mapper.toBook(request)).thenReturn(book);
        when(mapper.toBookTranslation(any())).thenReturn(translation);
        when(bookRepository.save(book)).thenReturn(book);
        
        Map<String, BookTranslation> translations = new HashMap<>();
        translations.put("en", translation);
        
        when(mapper.toAdminBookDetailsDto(book)).thenReturn(responseDto);

        AdminBookDetailsResponseDto result = bookManagementService.create(request);

        assertEquals(responseDto, result);
        verify(bookRepository).save(book);
        verify(book).addTranslation("en", translation);
    }

    @Test
    void update_WhenBookExists_ShouldUpdateBookAndReturnDto() {
        UUID id = UUID.randomUUID();
        BookRequestDto request = new BookRequestDto(
                "url", AgeGroup.ADULT, BigDecimal.TEN, LocalDate.now(), 100, Language.ENGLISH,
                Map.of("en", new BookTranslationDto(
                        "Title", "Author", "Desc", "Pub", "Desc")
                )
        );
        Book book = mock(Book.class);
        BookTranslation existingTranslation = mock(BookTranslation.class);
        
        Map<String, BookTranslation> translations = new HashMap<>();
        translations.put("en", existingTranslation);
        when(book.getTranslations()).thenReturn(translations);
        
        AdminBookDetailsResponseDto responseDto = mock(AdminBookDetailsResponseDto.class);

        when(bookRepository.findById(id)).thenReturn(Optional.of(book));
        when(bookRepository.save(book)).thenReturn(book);
        when(mapper.toAdminBookDetailsDto(book)).thenReturn(responseDto);

        AdminBookDetailsResponseDto result = bookManagementService.update(id, request);

        assertEquals(responseDto, result);
        verify(mapper).update(request, book);
        verify(mapper).updateTranslation(any(), eq(existingTranslation));
        verify(bookRepository).save(book);
    }

    @Test
    void update_WhenTranslationDoesNotExist_ShouldAddNewTranslation() {
        UUID id = UUID.randomUUID();
        BookRequestDto request = new BookRequestDto(
                "url", AgeGroup.ADULT, BigDecimal.TEN, LocalDate.now(), 100, Language.ENGLISH,
                Map.of("en", new BookTranslationDto(
                        "Title", "Author", "Desc", "Pub", "Desc")
                )
        );
        Book book = mock(Book.class);
        
        // Empty map meaning translation doesn't exist yet
        Map<String, BookTranslation> translations = new HashMap<>();
        when(book.getTranslations()).thenReturn(translations);
        
        BookTranslation newTranslation = mock(BookTranslation.class);
        when(mapper.toBookTranslation(any())).thenReturn(newTranslation);
        
        AdminBookDetailsResponseDto responseDto = mock(AdminBookDetailsResponseDto.class);
        when(bookRepository.findById(id)).thenReturn(Optional.of(book));
        when(bookRepository.save(book)).thenReturn(book);
        when(mapper.toAdminBookDetailsDto(any())).thenReturn(responseDto);

        bookManagementService.update(id, request);

        verify(mapper).update(request, book);
        verify(book).addTranslation("en", newTranslation);
    }

    @Test
    void update_WhenBookNotFound_ShouldThrowException() {
        UUID id = UUID.randomUUID();
        when(bookRepository.findById(id)).thenReturn(Optional.empty());

        assertThrows(BookNotFoundException.class, () ->
                bookManagementService.update(id, mock(BookRequestDto.class)));
    }

    @Test
    void delete_WhenHasOrders_ShouldThrowException() {
        UUID id = UUID.randomUUID();
        when(orderQueryService.hasBookBeenOrdered(id)).thenReturn(true);

        assertThrows(BookHasOrdersException.class, () -> bookManagementService.delete(id));
        verify(bookRepository, never()).deleteById(any());
    }

    @Test
    void delete_WhenInCart_ShouldThrowException() {
        UUID id = UUID.randomUUID();
        when(orderQueryService.hasBookBeenOrdered(id)).thenReturn(false);
        when(cartQueryService.containsBook(id)).thenReturn(true);

        assertThrows(BookWithinCartException.class, () -> bookManagementService.delete(id));
        verify(bookRepository, never()).deleteById(any());
    }

    @Test
    void delete_WhenCanDelete_ShouldDeleteBook() {
        UUID id = UUID.randomUUID();
        when(orderQueryService.hasBookBeenOrdered(id)).thenReturn(false);
        when(cartQueryService.containsBook(id)).thenReturn(false);

        bookManagementService.delete(id);

        verify(bookRepository).deleteById(id);
    }

    @Test
    void archive_WhenBookNotArchived_ShouldArchiveBook() {
        UUID id = UUID.randomUUID();
        Book book = mock(Book.class);
        when(book.isArchived()).thenReturn(false);
        when(bookRepository.findById(id)).thenReturn(Optional.of(book));

        bookManagementService.archive(id);

        verify(book).setArchived(true);
        verify(bookRepository).save(book);
    }

    @Test
    void archive_WhenBookAlreadyArchived_ShouldDoNothing() {
        UUID id = UUID.randomUUID();
        Book book = mock(Book.class);
        when(book.isArchived()).thenReturn(true);
        when(bookRepository.findById(id)).thenReturn(Optional.of(book));

        bookManagementService.archive(id);

        verify(bookRepository, never()).save(any());
    }

    @Test
    void unarchive_WhenBookArchived_ShouldUnarchiveBook() {
        UUID id = UUID.randomUUID();
        Book book = mock(Book.class);
        when(book.isArchived()).thenReturn(true);
        when(bookRepository.findById(id)).thenReturn(Optional.of(book));

        bookManagementService.unarchive(id);

        verify(book).setArchived(false);
        verify(bookRepository).save(book);
    }

    @Test
    void unarchive_WhenBookNotArchived_ShouldDoNothing() {
        UUID id = UUID.randomUUID();
        Book book = mock(Book.class);
        when(book.isArchived()).thenReturn(false);
        when(bookRepository.findById(id)).thenReturn(Optional.of(book));

        bookManagementService.unarchive(id);

        verify(bookRepository, never()).save(any());
    }
}
