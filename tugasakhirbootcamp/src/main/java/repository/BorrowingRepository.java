package repository;

import entity.Borrowing;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.List;
import java.util.UUID; // Import UUID

@Repository
public interface BorrowingRepository extends JpaRepository<Borrowing, UUID> { // Menggunakan UUID sebagai tipe ID

    List<Borrowing> findByMember_Id(Long memberId); // Perbaikan: Gunakan member_Id sesuai relasi

    @Query("SELECT b FROM Borrowing b WHERE b.dueDate < :currentDate AND b.status = 'BORROWED'")
    List<Borrowing> findOverdueBorrowings(LocalDate currentDate);
}