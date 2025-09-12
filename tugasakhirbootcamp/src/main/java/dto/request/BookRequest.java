package dto.request;

import jakarta.validation.constraints.*;
import lombok.Data;

@Data
public class BookRequest {
    @NotBlank(message = "Title is required")
    private String title;

    @NotBlank(message = "Author is required")
    private String author;

    @NotBlank(message = "ISBN is required")
    @Pattern(regexp = "^[0-9]{13}$", message = "ISBN must be 13 digits")
    private String isbn;

    @NotNull(message = "Publication year is required")
    @Min(value = 1000, message = "Invalid publication year")
    @Max(value = 9999, message = "Invalid publication year")
    private Integer publicationYear;

    private String description;

    @NotNull(message = "Quantity is required")
    @Min(value = 0, message = "Quantity cannot be negative")
    private Integer quantity;
}