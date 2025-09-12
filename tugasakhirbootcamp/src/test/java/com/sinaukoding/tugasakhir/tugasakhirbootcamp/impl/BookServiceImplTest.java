package com.sinaukoding.tugasakhir.tugasakhirbootcamp.impl;


import dto.BookDTO;
import entity.Book;
import exception.BookNotFoundException;
import exception.DuplicateIsbnException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import repository.BookRepository;
import service.impl.BookServiceImpl;

import java.math.BigDecimal;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class BookServiceImplTest {

    @Mock
    private BookRepository bookRepository;

    @InjectMocks
    private BookServiceImpl bookService;

    private Book book;
    private BookDTO bookDTO;

    @BeforeEach
    void setUp() {
        book = new Book();
        book.setId(1L);
        book.setTitle("Test Book");
        book.setAuthor("Test Author");
        book.setIsbn("1234567890123");
        book.setPrice(new BigDecimal("100.00"));
        book.setStock(10);
        book.setPublishedYear(2020);
        book.setCategory("Fiction");

        bookDTO = new BookDTO();
        bookDTO.setId(1L);
        bookDTO.setTitle("Test Book");
        bookDTO.setAuthor("Test Author");
        bookDTO.setIsbn("1234567890123");
        bookDTO.setPrice(new BigDecimal("100.00"));
        bookDTO.setStock(10);
        bookDTO.setPublishedYear(2020);
        bookDTO.setCategory("Fiction");
    }

    @Test
    void createBook_Success() {
        when(bookRepository.existsByIsbn(anyString())).thenReturn(false);
        when(bookRepository.save(any(Book.class))).thenReturn(book);

        BookDTO createdBook = bookService.createBook(bookDTO);

        assertNotNull(createdBook);
        assertEquals(bookDTO.getTitle(), createdBook.getTitle());
        verify(bookRepository, times(1)).existsByIsbn(bookDTO.getIsbn());
        verify(bookRepository, times(1)).save(any(Book.class));
    }

    @Test
    void createBook_DuplicateIsbn_ThrowsException() {
        when(bookRepository.existsByIsbn(anyString())).thenReturn(true);

        assertThrows(DuplicateIsbnException.class, () -> bookService.createBook(bookDTO));
        verify(bookRepository, times(1)).existsByIsbn(bookDTO.getIsbn());
        verify(bookRepository, never()).save(any(Book.class));
    }

    @Test
    void getBookById_Found() {
        when(bookRepository.findById(anyLong())).thenReturn(Optional.of(book));

        Optional<BookDTO> foundBook = bookService.getBookById(1L);

        assertTrue(foundBook.isPresent());
        assertEquals(bookDTO.getTitle(), foundBook.get().getTitle());
        verify(bookRepository, times(1)).findById(1L);
    }

    @Test
    void getBookById_NotFound() {
        when(bookRepository.findById(anyLong())).thenReturn(Optional.empty());

        Optional<BookDTO> foundBook = bookService.getBookById(1L);

        assertFalse(foundBook.isPresent());
        verify(bookRepository, times(1)).findById(1L);
    }

    @Test
    void updateBook_Success() {
        Book updatedBook = new Book();
        updatedBook.setId(1L);
        updatedBook.setTitle("Updated Title");
        updatedBook.setAuthor("Updated Author");
        updatedBook.setIsbn("1234567890123"); // Same ISBN
        updatedBook.setPrice(new BigDecimal("120.00"));
        updatedBook.setStock(12);
        updatedBook.setPublishedYear(2021);
        updatedBook.setCategory("Fantasy");

        BookDTO updatedBookDTO = new BookDTO();
        updatedBookDTO.setId(1L);
        updatedBookDTO.setTitle("Updated Title");
        updatedBookDTO.setAuthor("Updated Author");
        updatedBookDTO.setIsbn("1234567890123");
        updatedBookDTO.setPrice(new BigDecimal("120.00"));
        updatedBookDTO.setStock(12);
        updatedBookDTO.setPublishedYear(2021);
        updatedBookDTO.setCategory("Fantasy");

        when(bookRepository.findById(anyLong())).thenReturn(Optional.of(book)); // Return original book
        when(bookRepository.existsByIsbn(anyString())).thenReturn(false); // No duplicate ISBN
        when(bookRepository.save(any(Book.class))).thenReturn(updatedBook);

        BookDTO result = bookService.updateBook(1L, updatedBookDTO);

        assertNotNull(result);
        assertEquals("Updated Title", result.getTitle());
        assertEquals("Updated Author", result.getAuthor());
        verify(bookRepository, times(1)).findById(1L);
        verify(bookRepository, times(1)).save(any(Book.class));
    }

    @Test
    void updateBook_NotFound_ThrowsException() {
        when(bookRepository.findById(anyLong())).thenReturn(Optional.empty());

        assertThrows(BookNotFoundException.class, () -> bookService.updateBook(1L, bookDTO));
        verify(bookRepository, times(1)).findById(1L);
        verify(bookRepository, never()).save(any(Book.class));
    }

    @Test
    void updateBook_NewIsbnDuplicate_ThrowsException() {
        BookDTO newIsbnBookDTO = new BookDTO();
        newIsbnBookDTO.setId(1L);
        newIsbnBookDTO.setTitle("Test Book");
        newIsbnBookDTO.setAuthor("Test Author");
        newIsbnBookDTO.setIsbn("9999999999999"); // New ISBN
        newIsbnBookDTO.setPrice(new BigDecimal("100.00"));
        newIsbnBookDTO.setStock(10);
        newIsbnBookDTO.setPublishedYear(2020);
        newIsbnBookDTO.setCategory("Fiction");

        when(bookRepository.findById(anyLong())).thenReturn(Optional.of(book)); // Original book has ISBN 123...
        when(bookRepository.existsByIsbn("9999999999999")).thenReturn(true); // New ISBN already exists

        assertThrows(DuplicateIsbnException.class, () -> bookService.updateBook(1L, newIsbnBookDTO));
        verify(bookRepository, times(1)).findById(1L);
        verify(bookRepository, times(1)).existsByIsbn("9999999999999");
        verify(bookRepository, never()).save(any(Book.class));
    }

    @Test
    void deleteBook_Success() {
        when(bookRepository.existsById(anyLong())).thenReturn(true);
        doNothing().when(bookRepository).deleteById(anyLong());

        boolean result = bookService.deleteBook(1L);

        assertTrue(result);
        verify(bookRepository, times(1)).existsById(1L);
        verify(bookRepository, times(1)).deleteById(1L);
    }

    @Test
    void deleteBook_NotFound_ThrowsException() {
        when(bookRepository.existsById(anyLong())).thenReturn(false);

        assertThrows(BookNotFoundException.class, () -> bookService.deleteBook(1L));
        verify(bookRepository, times(1)).existsById(1L);
        verify(bookRepository, never()).deleteById(anyLong());
    }

    @Test
    void getAllBooksPageable_Success() {
        Pageable pageable = PageRequest.of(0, 10);
        Page<Book> bookPage = new PageImpl<>(Arrays.asList(book), pageable, 1);
        when(bookRepository.findAll(pageable)).thenReturn(bookPage);

        Page<BookDTO> result = bookService.getAllBooksPageable(pageable);

        assertNotNull(result);
        assertEquals(1, result.getTotalElements());
        assertEquals(bookDTO.getTitle(), result.getContent().get(0).getTitle());
        verify(bookRepository, times(1)).findAll(pageable);
    }

    @Test
    void updateStock_Success() {
        when(bookRepository.findById(anyLong())).thenReturn(Optional.of(book));
        when(bookRepository.save(any(Book.class))).thenReturn(book);

        boolean result = bookService.updateStock(1L, 5);

        assertTrue(result);
        assertEquals(5, book.getStock()); // Verify stock updated on the entity
        verify(bookRepository, times(1)).findById(1L);
        verify(bookRepository, times(1)).save(book);
    }

    @Test
    void updateStock_BookNotFound_ThrowsException() {
        when(bookRepository.findById(anyLong())).thenReturn(Optional.empty());

        assertThrows(BookNotFoundException.class, () -> bookService.updateStock(1L, 5));
        verify(bookRepository, times(1)).findById(1L);
        verify(bookRepository, never()).save(any(Book.class));
    }
}

