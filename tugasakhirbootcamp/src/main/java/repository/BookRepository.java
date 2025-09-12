package repository;//import com.example.bookstore.entity.Book;
import entity.Book;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;

@Repository
public interface BookRepository extends JpaRepository<Book, Long> {

    Optional<Book> findByIsbn(String isbn);

    List<Book> findByAuthorContainingIgnoreCase(String author);

    List<Book> findByTitleContainingIgnoreCase(String title);

    List<Book> findByCategoryIgnoreCase(String category);

    List<Book> findByPriceBetween(BigDecimal minPrice, BigDecimal maxPrice);

    @Query("SELECT b FROM Book b WHERE b.stock > 0")
    List<Book> findBooksInStock();

    @Query("SELECT b FROM Book b WHERE " +
            "(:title IS NULL OR LOWER(b.title) LIKE LOWER(CONCAT('%', :title, '%'))) AND " +
            "(:author IS NULL OR LOWER(b.author) LIKE LOWER(CONCAT('%', :author, '%'))) AND " +
            "(:category IS NULL OR LOWER(b.category) = LOWER(:category))")
    Page<Book> searchBooks(@Param("title") String title,
                           @Param("author") String author,
                           @Param("category") String category,
                           Pageable pageable);

    boolean existsByIsbn(String isbn);
}
