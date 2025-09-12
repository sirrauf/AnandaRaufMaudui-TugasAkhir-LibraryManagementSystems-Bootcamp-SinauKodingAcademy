package com.sinaukoding.tugasakhir.tugasakhirbootcamp.service;

import dto.BookDTO;
import dto.BorrowingDto;
import dto.MemberDto;
import entity.Book;
import entity.Borrowing;
import entity.Member;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import repository.BookRepository;
import repository.RoleRepository;
import repository.MemberRepository;

import java.util.Arrays;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class LibraryServiceTest {

    @Mock
    private BookRepository bookRepository;

    @Mock
    private MemberRepository memberRepository;

    @Mock
    private RoleRepository roleRepository;

    @InjectMocks
    private BookService bookService;

    @InjectMocks
    private MemberService memberService;

    @InjectMocks
    private BorrowingService borrowingService;

    private Book testBook;
    private Member testMember;
    private UUID testBookId;
    private UUID testMemberId;
    private UUID testBorrowingId;
    private BookDTO testBookDTO;
    private MemberDto testMemberDto;

    @BeforeEach
    void setUp() {
        // Setup UUIDs
        testBookId = UUID.randomUUID();
        testMemberId = UUID.randomUUID();
        testBorrowingId = UUID.randomUUID();

        // Setup test book
        testBook = new Book();
        testBook.setId(testBookId);
        testBook.setTitle("Test Book");
        testBook.setAuthor("Test Author");
        testBook.setIsbn("123-456-789");
        testBook.setDescription("Test Description");
        testBook.setAvailableQuantity(5);
        testBook.setTotalQuantity(10);

        // Setup test member
        testMember = new Member();
        testMember.setId(testMemberId);
        testMember.setName("Test Member");
        testMember.setEmail("test@example.com");
        testMember.setPhone("1234567890");
        testMember.setAddress("Test Address");

        // Setup test DTOs
        testBookDTO = new BookDTO();
        testBookDTO.setTitle("Test Book");
        testBookDTO.setAuthor("Test Author");
        testBookDTO.setIsbn("123-456-789");
        testBookDTO.setDescription("Test Description");
        testBookDTO.setAvailableQuantity(5);
        testBookDTO.setTotalQuantity(10);

        testMemberDto = new MemberDto();
        testMemberDto.setName("Test Member");
        testMemberDto.setEmail("test@example.com");
        testMemberDto.setPhone("1234567890");
        testMemberDto.setAddress("Test Address");
    }

    // ---------------- Book Service Tests ----------------
    @Test
    void getAllBooks_ShouldReturnListOfBooks() {
        when(bookRepository.findAll()).thenReturn(Arrays.asList(testBook));
        List<Book> books = bookService.getAllBooks();
        assertThat(books).hasSize(1);
        assertThat(books.get(0).getTitle()).isEqualTo("Test Book");
        verify(bookRepository).findAll();
    }

    @Test
    void getBookById_WhenBookExists_ShouldReturnBook() {
        when(bookRepository.findById(testBookId)).thenReturn(Optional.of(testBook));
        Optional<Book> book = bookService.getBookById(testBookId);
        assertThat(book).isPresent();
        assertThat(book.get().getTitle()).isEqualTo("Test Book");
        verify(bookRepository).findById(testBookId);
    }

    @Test
    void getBookById_WhenBookDoesNotExist_ShouldReturnEmpty() {
        when(bookRepository.findById(testBookId)).thenReturn(Optional.empty());
        Optional<Book> book = bookService.getBookById(testBookId);
        assertThat(book).isEmpty();
        verify(bookRepository).findById(testBookId);
    }

    @Test
    void createBook_ShouldReturnCreatedBook() {
        when(bookRepository.save(any(Book.class))).thenReturn(testBook);
        Book book = bookService.createBook(testBookDTO);
        assertThat(book.getTitle()).isEqualTo("Test Book");
        verify(bookRepository).save(any(Book.class));
    }

    @Test
    void updateBook_WhenBookExists_ShouldReturnUpdatedBook() {
        when(bookRepository.findById(testBookId)).thenReturn(Optional.of(testBook));
        when(bookRepository.save(any(Book.class))).thenReturn(testBook);
        Book book = bookService.updateBook(testBookId, testBookDTO);
        assertThat(book.getTitle()).isEqualTo("Test Book");
        verify(bookRepository).findById(testBookId);
        verify(bookRepository).save(any(Book.class));
    }

    @Test
    void updateBook_WhenBookDoesNotExist_ShouldThrowException() {
        when(bookRepository.findById(testBookId)).thenReturn(Optional.empty());
        assertThatThrownBy(() -> bookService.updateBook(testBookId, testBookDTO))
                .isInstanceOf(RuntimeException.class)
                .hasMessageContaining("Book not found with id: " + testBookId);
    }

    @Test
    void deleteBook_ShouldCallDeleteById() {
        bookService.deleteBook(testBookId);
        verify(bookRepository).deleteById(testBookId);
    }

    @Test
    void searchBooks_ShouldReturnMatchingBooks() {
        when(bookRepository.findByTitleOrAuthorContaining("Test")).thenReturn(Arrays.asList(testBook));
        List<Book> books = bookService.searchBooks("Test");
        assertThat(books).hasSize(1);
        assertThat(books.get(0).getTitle()).isEqualTo("Test Book");
        verify(bookRepository).findByTitleOrAuthorContaining("Test");
    }

    @Test
    void getAvailableBooks_ShouldReturnOnlyAvailableBooks() {
        when(bookRepository.findAvailableBooks()).thenReturn(Arrays.asList(testBook));
        List<Book> books = bookService.getAvailableBooks();
        assertThat(books).hasSize(1);
        assertThat(books.get(0).getAvailableQuantity()).isGreaterThan(0);
        verify(bookRepository).findAvailableBooks();
    }

    // ---------------- Member Service Tests ----------------
    @Test
    void getAllMembers_ShouldReturnListOfMembers() {
        when(memberRepository.findAll()).thenReturn(Arrays.asList(testMember));
        List<Member> members = memberService.getAllMembers();
        assertThat(members).hasSize(1);
        assertThat(members.get(0).getName()).isEqualTo("Test Member");
        verify(memberRepository).findAll();
    }

    @Test
    void createMember_ShouldReturnCreatedMember() {
        when(memberRepository.save(any(Member.class))).thenReturn(testMember);
        Member member = memberService.createMember(testMemberDto);
        assertThat(member.getName()).isEqualTo("Test Member");
        verify(memberRepository).save(any(Member.class));
    }

    // ---------------- Borrowing Service Tests ----------------
    @Test
    void createBorrowing_WhenBookIsAvailable_ShouldReturnBorrowing() {
        BorrowingDto dto = new BorrowingDto();
        dto.setBookId(testBookId);
        dto.setMemberId(testMemberId);

        Borrowing expected = new Borrowing();
        expected.setBook(testBook);
        expected.setMember(testMember);
        expected.setStatus(Borrowing.BorrowingStatus.BORROWED);

        when(bookRepository.findById(testBookId)).thenReturn(Optional.of(testBook));
        when(memberRepository.findById(testMemberId)).thenReturn(Optional.of(testMember));
        when(roleRepository.save(any(Borrowing.class))).thenReturn(expected);
        when(bookRepository.save(any(Book.class))).thenReturn(testBook);

        Borrowing result = borrowingService.createBorrowing(dto);
        assertThat(result.getBook()).isEqualTo(testBook);
        assertThat(result.getMember()).isEqualTo(testMember);
        assertThat(result.getStatus()).isEqualTo(Borrowing.BorrowingStatus.BORROWED);
        verify(bookRepository).save(any(Book.class));
    }

    @Test
    void createBorrowing_WhenBookIsNotAvailable_ShouldThrowException() {
        testBook.setAvailableQuantity(0);
        BorrowingDto dto = new BorrowingDto();
        dto.setBookId(testBookId);
        dto.setMemberId(testMemberId);

        when(bookRepository.findById(testBookId)).thenReturn(Optional.of(testBook));
        when(memberRepository.findById(testMemberId)).thenReturn(Optional.of(testMember));

        assertThatThrownBy(() -> borrowingService.createBorrowing(dto))
                .isInstanceOf(RuntimeException.class)
                .hasMessageContaining("Book is not available for borrowing");
    }

    @Test
    void createBorrowing_WhenBookDoesNotExist_ShouldThrowException() {
        UUID nonExistentBookId = UUID.randomUUID();
        BorrowingDto dto = new BorrowingDto();
        dto.setBookId(nonExistentBookId);
        dto.setMemberId(testMemberId);

        when(bookRepository.findById(nonExistentBookId)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> borrowingService.createBorrowing(dto))
                .isInstanceOf(RuntimeException.class)
                .hasMessageContaining("Book not found");
    }

    @Test
    void returnBook_WhenBorrowingExists_ShouldReturnUpdatedBorrowing() {
        Borrowing borrowing = new Borrowing();
        borrowing.setId(testBorrowingId);
        borrowing.setBook(testBook);
        borrowing.setMember(testMember);
        borrowing.setStatus(Borrowing.BorrowingStatus.BORROWED);

        when(roleRepository.findById(testBorrowingId)).thenReturn(Optional.of(borrowing));
        when(roleRepository.save(any(Borrowing.class))).thenReturn(borrowing);
        when(bookRepository.save(any(Book.class))).thenReturn(testBook);

        Borrowing result = borrowingService.returnBook(testBorrowingId);
        assertThat(result.getStatus()).isEqualTo(Borrowing.BorrowingStatus.RETURNED);
        verify(bookRepository).save(any(Book.class));
    }

    @Test
    void returnBook_WhenAlreadyReturned_ShouldThrowException() {
        Borrowing borrowing = new Borrowing();
        borrowing.setId(testBorrowingId);
        borrowing.setStatus(Borrowing.BorrowingStatus.RETURNED);

        when(roleRepository.findById(testBorrowingId)).thenReturn(Optional.of(borrowing));

        assertThatThrownBy(() -> borrowingService.returnBook(testBorrowingId))
                .isInstanceOf(RuntimeException.class)
                .hasMessageContaining("Book is already returned");
    }
}