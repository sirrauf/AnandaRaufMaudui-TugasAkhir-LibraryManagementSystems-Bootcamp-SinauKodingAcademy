package controller;

import dto.BorrowingDto;
import entity.Borrowing;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import service.BorrowingService;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/borrowings")
@RequiredArgsConstructor
public class BorrowingController {
    private final BorrowingService borrowingService;

    @GetMapping
    @PreAuthorize("hasAnyRole('ADMIN', 'LIBRARIAN')")
    public ResponseEntity<List<Borrowing>> getAllBorrowings() {
        return ResponseEntity.ok(borrowingService.getAllBorrowings());
    }

    @GetMapping("/{id}")
    @PreAuthorize("hasAnyRole('ADMIN', 'LIBRARIAN')")
    public ResponseEntity<Borrowing> getBorrowingById(@PathVariable UUID id) {
        return borrowingService.getBorrowingById(id)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @PostMapping
    @PreAuthorize("hasAnyRole('ADMIN', 'LIBRARIAN')")
    public ResponseEntity<?> createBorrowing(@Valid @RequestBody BorrowingDto borrowingDto) {
        try {
            Borrowing createdBorrowing = borrowingService.createBorrowing(borrowingDto);
            return ResponseEntity.status(HttpStatus.CREATED).body(createdBorrowing);
        } catch (RuntimeException e) {
            // Kembalikan pesan error dengan tipe ResponseEntity<String>
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }

    @PutMapping("/{id}/return")
    @PreAuthorize("hasAnyRole('ADMIN', 'LIBRARIAN')")
    public ResponseEntity<?> returnBook(@PathVariable UUID id) {
        try {
            Borrowing returnedBorrowing = borrowingService.returnBook(id);
            return ResponseEntity.ok(returnedBorrowing);
        } catch (RuntimeException e) {
            // Kembalikan pesan error dengan tipe ResponseEntity<String>
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }

    @GetMapping("/member/{memberId}")
    @PreAuthorize("hasAnyRole('ADMIN', 'LIBRARIAN')")
    public ResponseEntity<List<Borrowing>> getBorrowingsByMember(@PathVariable Long memberId) {
        return ResponseEntity.ok(borrowingService.getBorrowingsByMember(memberId));
    }

    @GetMapping("/overdue")
    @PreAuthorize("hasAnyRole('ADMIN', 'LIBRARIAN')")
    public ResponseEntity<List<Borrowing>> getOverdueBorrowings() {
        return ResponseEntity.ok(borrowingService.getOverdueBorrowings());
    }
}
