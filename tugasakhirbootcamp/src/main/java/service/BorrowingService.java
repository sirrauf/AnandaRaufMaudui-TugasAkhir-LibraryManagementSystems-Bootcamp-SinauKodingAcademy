package service;

import dto.BorrowingDto;
import entity.Book;
import entity.Borrowing;
import entity.Member;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import repository.BookRepository;
//import repository.BorrowingRepository; // Import BorrowingRepository
import repository.BorrowingRepository;
import repository.MemberRepository;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;
import java.util.UUID; // Import UUID

@Service
@RequiredArgsConstructor
public class BorrowingService {
    private final BorrowingRepository borrowingRepository; // Perbaikan: Gunakan BorrowingRepository
    private final BookRepository bookRepository;
    private final MemberRepository memberRepository;

    public List<Borrowing> getAllBorrowings() {
        return borrowingRepository.findAll();
    }

    public Optional<Borrowing> getBorrowingById(UUID id) { // Perbaikan: Menggunakan UUID
        return borrowingRepository.findById(id);
    }

    @Transactional
    public Borrowing createBorrowing(BorrowingDto borrowingDto) {
        Book book = bookRepository.findById(borrowingDto.getBookId())
                .orElseThrow(() -> new RuntimeException("Book not found"));

        Member member = memberRepository.findById(borrowingDto.getMemberId())
                .orElseThrow(() -> new RuntimeException("Member not found"));

        if (book.getStock() <= 0) { // Menggunakan 'stock' dari entity Book
            throw new RuntimeException("Book is not available for borrowing");
        }

        Borrowing borrowing = new Borrowing();
        borrowing.setBook(book);
        borrowing.setMember(member);
        borrowing.setBorrowDate(LocalDate.now());
        borrowing.setDueDate(LocalDate.now().plusDays(14)); // 2 weeks
        borrowing.setStatus(Borrowing.BorrowingStatus.BORROWED);

        book.setStock(book.getStock() - 1); // Mengurangi stock buku
        bookRepository.save(book);

        return borrowingRepository.save(borrowing);
    }

    @Transactional
    public Borrowing returnBook(UUID borrowingId) { // Perbaikan: Menggunakan UUID
        Borrowing borrowing = borrowingRepository.findById(borrowingId)
                .orElseThrow(() -> new RuntimeException("Borrowing not found"));

        if (borrowing.getStatus() != Borrowing.BorrowingStatus.BORROWED) {
            throw new RuntimeException("Book is already returned or not borrowed");
        }

        borrowing.setReturnDate(LocalDate.now());
        borrowing.setStatus(Borrowing.BorrowingStatus.RETURNED);

        Book book = borrowing.getBook();
        book.setStock(book.getStock() + 1); // Menambah stock buku
        bookRepository.save(book);

        return borrowingRepository.save(borrowing);
    }

    public List<Borrowing> getBorrowingsByMember(Long memberId) {
        return borrowingRepository.findByMember_Id(memberId);
    }

    public List<Borrowing> getOverdueBorrowings() {
        return borrowingRepository.findOverdueBorrowings(LocalDate.now());
    }
}