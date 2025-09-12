package entity;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;
import lombok.EqualsAndHashCode;
import lombok.experimental.SuperBuilder;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.UUID;

@EqualsAndHashCode()
@Data
@SuperBuilder
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(
        name = "m_borrowings",
        indexes = {
                @Index(name = "idx_borrowing_created_at", columnList = "created_at"),
                @Index(name = "idx_borrowing_updated_at", columnList = "updated_at"),
                @Index(name = "idx_borrowing_status", columnList = "status"),
                @Index(name = "idx_borrowing_borrow_date", columnList = "borrow_date"),
                @Index(name = "idx_borrowing_due_date", columnList = "due_date")
        }
)
public class Borrowing {
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    @ManyToOne
    @JoinColumn(name = "book_id", nullable = false)
    @NotNull(message = "Book is required")
    private Book book;

    @ManyToOne
    @JoinColumn(name = "member_id", nullable = false)
    @NotNull(message = "Member is required")
    private Member member;

    @Column(name = "borrow_date", nullable = false)
    private LocalDate borrowDate;

    @Column(name = "due_date", nullable = false)
    private LocalDate dueDate;

    @Column(name = "return_date")
    private LocalDate returnDate;

    @Enumerated(EnumType.STRING)
    private BorrowingStatus status;

    @Column(name = "created_at")
    private LocalDateTime createdAt;

    @Column(name = "updated_at")
    private LocalDateTime updatedAt;

    public enum BorrowingStatus {
        BORROWED, RETURNED, OVERDUE
    }

    @PrePersist
    protected void onCreate() {
        createdAt = LocalDateTime.now();
        updatedAt = LocalDateTime.now();
        if (borrowDate == null) {
            borrowDate = LocalDate.now();
        }
        if (status == null) {
            status = BorrowingStatus.BORROWED;
        }
    }
}