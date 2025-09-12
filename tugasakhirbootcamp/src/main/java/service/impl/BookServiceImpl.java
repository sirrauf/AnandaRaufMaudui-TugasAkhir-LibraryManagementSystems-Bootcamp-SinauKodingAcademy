package service.impl;

import dto.BookDTO;
import entity.Book;
import exception.BookNotFoundException;
import exception.DuplicateIsbnException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import repository.BookRepository;
import service.BookService;

import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Slf4j
@Transactional
public class BookServiceImpl implements BookService {

    private final BookRepository bookRepository;

    @Override
    public BookDTO createBook(BookDTO bookDTO) {
        log.info("Creating new book with ISBN: {}", bookDTO.getIsbn());


        if (bookRepository.existsByIsbn(bookDTO.getIsbn())) {
            throw new DuplicateIsbnException("Book with ISBN " + bookDTO.getIsbn() + " already exists");
        }

        Book book = convertToEntity(bookDTO);
        Book savedBook = bookRepository.save(book);

        log.info("Successfully created book with ID: {}", savedBook.getId());
        return convertToDTO(savedBook);
    }

    @Override
    @Transactional(readOnly = true)
    public Optional<BookDTO> getBookById(Long id) {
        log.debug("Fetching book with ID: {}", id);
        return bookRepository.findById(id)
                .map(this::convertToDTO);
    }

    @Override
    @Transactional(readOnly = true)
    public Optional<BookDTO> getBookByIsbn(String isbn) {
        log.debug("Fetching book with ISBN: {}", isbn);
        return bookRepository.findByIsbn(isbn)
                .map(this::convertToDTO);
    }

    @Override
    @Transactional(readOnly = true)
    public List<BookDTO> getAllBooks() {
        log.debug("Fetching all books");
        return bookRepository.findAll().stream()
                .map(this::convertToDTO)
                .collect(Collectors.toList());
    }

    @Override
    @Transactional(readOnly = true)
    public Page<BookDTO> getAllBooksPageable(Pageable pageable) {
        log.debug("Fetching books with pagination: {}", pageable);
        return bookRepository.findAll(pageable)
                .map(this::convertToDTO);
    }

    @Override
    public BookDTO updateBook(Long id, BookDTO bookDTO) {
        log.info("Updating book with ID: {}", id);

        Book existingBook = bookRepository.findById(id)
                .orElseThrow(() -> new BookNotFoundException("Book not found with ID: " + id));

        if (!existingBook.getIsbn().equals(bookDTO.getIsbn()) &&
                bookRepository.existsByIsbn(bookDTO.getIsbn())) {
            throw new DuplicateIsbnException("Book with ISBN " + bookDTO.getIsbn() + " already exists");
        }

        updateBookFields(existingBook, bookDTO);
        Book updatedBook = bookRepository.save(existingBook);

        log.info("Successfully updated book with ID: {}", updatedBook.getId());
        return convertToDTO(updatedBook);
    }

    @Override
    public boolean deleteBook(Long id) {
        log.info("Deleting book with ID: {}", id);

        if (!bookRepository.existsById(id)) {
            throw new BookNotFoundException("Book not found with ID: " + id);
        }

        bookRepository.deleteById(id);
        log.info("Successfully deleted book with ID: {}", id);
        return true;
    }

    @Override
    @Transactional(readOnly = true)
    public List<BookDTO> searchBooksByTitle(String title) {
        log.debug("Searching books by title: {}", title);
        return bookRepository.findByTitleContainingIgnoreCase(title).stream()
                .map(this::convertToDTO)
                .collect(Collectors.toList());
    }

    @Override
    @Transactional(readOnly = true)
    public List<BookDTO> searchBooksByAuthor(String author) {
        log.debug("Searching books by author: {}", author);
        return bookRepository.findByAuthorContainingIgnoreCase(author).stream()
                .map(this::convertToDTO)
                .collect(Collectors.toList());
    }

    @Override
    @Transactional(readOnly = true)
    public List<BookDTO> getBooksByCategory(String category) {
        log.debug("Fetching books by category: {}", category);
        return bookRepository.findByCategoryIgnoreCase(category).stream()
                .map(this::convertToDTO)
                .collect(Collectors.toList());
    }

    @Override
    @Transactional(readOnly = true)
    public List<BookDTO> getBooksByPriceRange(BigDecimal minPrice, BigDecimal maxPrice) {
        log.debug("Fetching books by price range: {} - {}", minPrice, maxPrice);
        return bookRepository.findByPriceBetween(minPrice, maxPrice).stream()
                .map(this::convertToDTO)
                .collect(Collectors.toList());
    }

    @Override
    @Transactional(readOnly = true)
    public List<BookDTO> getBooksInStock() {
        log.debug("Fetching books in stock");
        return bookRepository.findBooksInStock().stream()
                .map(this::convertToDTO)
                .collect(Collectors.toList());
    }

    @Override
    @Transactional(readOnly = true)
    public Page<BookDTO> searchBooks(String title, String author, String category, Pageable pageable) {
        log.debug("Searching books with filters - Title: {}, Author: {}, Category: {}", title, author, category);
        return bookRepository.searchBooks(title, author, category, pageable)
                .map(this::convertToDTO);
    }

    @Override
    public boolean updateStock(Long id, Integer newStock) {
        log.info("Updating stock for book ID: {} to {}", id, newStock);

        Book book = bookRepository.findById(id)
                .orElseThrow(() -> new BookNotFoundException("Book not found with ID: " + id));

        book.setStock(newStock);
        bookRepository.save(book);

        log.info("Successfully updated stock for book ID: {}", id);
        return true;
    }

    @Override
    @Transactional(readOnly = true)
    public boolean isBookExists(Long id) {
        return bookRepository.existsById(id);
    }

    @Override
    @Transactional(readOnly = true)
    public boolean isIsbnExists(String isbn) {
        return bookRepository.existsByIsbn(isbn);
    }

    @Override
    @Transactional(readOnly = true)
    public long getTotalBooksCount() {
        return bookRepository.count();
    }

    // Helper methods for conversion
    private BookDTO convertToDTO(Book book) {
        BookDTO dto = new BookDTO();
        dto.setId(book.getId());
        dto.setTitle(book.getTitle());
        dto.setAuthor(book.getAuthor());
        dto.setIsbn(book.getIsbn());
        dto.setPrice(book.getPrice());
        dto.setDescription(book.getDescription());
        dto.setStock(book.getStock());
        dto.setPublishedYear(book.getPublishedYear());
        dto.setCategory(book.getCategory());
        return dto;
    }

    private Book convertToEntity(BookDTO dto) {
        Book book = new Book();
        book.setId(dto.getId());
        book.setTitle(dto.getTitle());
        book.setAuthor(dto.getAuthor());
        book.setIsbn(dto.getIsbn());
        book.setPrice(dto.getPrice());
        book.setDescription(dto.getDescription());
        book.setStock(dto.getStock());
        book.setPublishedYear(dto.getPublishedYear());
        book.setCategory(dto.getCategory());
        return book;
    }

    private void updateBookFields(Book existingBook, BookDTO bookDTO) {
        existingBook.setTitle(bookDTO.getTitle());
        existingBook.setAuthor(bookDTO.getAuthor());
        existingBook.setIsbn(bookDTO.getIsbn());
        existingBook.setPrice(bookDTO.getPrice());
        existingBook.setDescription(bookDTO.getDescription());
        existingBook.setStock(bookDTO.getStock());
        existingBook.setPublishedYear(bookDTO.getPublishedYear());
        existingBook.setCategory(bookDTO.getCategory());
    }
}

