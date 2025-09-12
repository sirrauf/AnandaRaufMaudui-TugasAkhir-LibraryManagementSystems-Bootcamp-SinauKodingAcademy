package service;
import dto.BookDTO;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;

public interface BookService {

    BookDTO createBook(BookDTO bookDTO);

    Optional<BookDTO> getBookById(Long id);

    Optional<BookDTO> getBookByIsbn(String isbn);

    List<BookDTO> getAllBooks();

    Page<BookDTO> getAllBooksPageable(Pageable pageable);

    BookDTO updateBook(Long id, BookDTO bookDTO);

    boolean deleteBook(Long id);

    List<BookDTO> searchBooksByTitle(String title);

    List<BookDTO> searchBooksByAuthor(String author);

    List<BookDTO> getBooksByCategory(String category);

    List<BookDTO> getBooksByPriceRange(BigDecimal minPrice, BigDecimal maxPrice);

    List<BookDTO> getBooksInStock();

    Page<BookDTO> searchBooks(String title, String author, String category, Pageable pageable);

    boolean updateStock(Long id, Integer newStock);

    boolean isBookExists(Long id);

    boolean isIsbnExists(String isbn);

    long getTotalBooksCount();
}

