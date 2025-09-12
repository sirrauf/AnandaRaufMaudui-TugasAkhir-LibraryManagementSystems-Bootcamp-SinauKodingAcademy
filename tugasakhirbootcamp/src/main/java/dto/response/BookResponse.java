package dto.response;

import lombok.Data;
import java.time.LocalDateTime;

@Data
public class BookResponse {
    private Long id;
    private String title;
    private String author;
    private String isbn;
    private Integer publicationYear;
    private String description;
    private Integer quantity;
    private Integer availableQuantity;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
}