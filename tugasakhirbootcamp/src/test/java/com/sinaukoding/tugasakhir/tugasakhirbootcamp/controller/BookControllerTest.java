package com.sinaukoding.tugasakhir.tugasakhirbootcamp.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import controller.BookController;
import dto.BookDTO;
import exception.BookNotFoundException;
import exception.DuplicateIsbnException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
//import org.springframework.boot.test.mock.mockito.MockitoBean; // ✅ pakai MockitoBean
import org.springframework.context.annotation.Import;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;
import secruity.jwt.AuthEntryPointJwt;
import secruity.jwt.AuthTokenFilter;
import service.BookService;
import config.SecurityConfig;
import service.UserService;

import java.math.BigDecimal;
import java.util.Optional;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(BookController.class)
@Import({SecurityConfig.class, AuthEntryPointJwt.class, AuthTokenFilter.class})
class BookControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private BookService bookService;

    @MockitoBean
    private UserService userService;

    @Autowired
    private ObjectMapper objectMapper;

    private BookDTO bookDTO;

    @BeforeEach
    void setUp() {
        bookDTO = new BookDTO();
        bookDTO.setId(1L);
        bookDTO.setTitle("Test Book");
        bookDTO.setAuthor("Test Author");
        bookDTO.setIsbn("1234567890123");
        bookDTO.setPrice(new BigDecimal("100.00"));
        bookDTO.setStock(10);
        bookDTO.setPublishedYear(2020);
        bookDTO.setCategory("Fiction");
    }

    @Test
    @WithMockUser(roles = {"ADMIN", "LIBRARIAN", "MEMBER"})
    void createBook_Success() throws Exception {
        when(bookService.createBook(any(BookDTO.class))).thenReturn(bookDTO);

        mockMvc.perform(post("/api/books")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(bookDTO)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.title").value("Test Book"));
    }

    @Test
    @WithMockUser(roles = {"ADMIN", "LIBRARIAN", "MEMBER"})
    void createBook_InvalidInput() throws Exception {
        bookDTO.setTitle(""); // Invalid title
        mockMvc.perform(post("/api/books")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(bookDTO)))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.validationErrors.title").value("Title is required"));
    }

    @Test
    @WithMockUser(roles = {"ADMIN", "LIBRARIAN", "MEMBER"})
    void createBook_DuplicateIsbn() throws Exception {
        when(bookService.createBook(any(BookDTO.class)))
                .thenThrow(new DuplicateIsbnException("Book with ISBN 1234567890123 already exists"));

        mockMvc.perform(post("/api/books")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(bookDTO)))
                .andExpect(status().isConflict())
                .andExpect(jsonPath("$.message").value("Book with ISBN 1234567890123 already exists"));
    }

    @Test
    @WithMockUser(roles = {"ADMIN", "LIBRARIAN", "MEMBER"})
    void getBookById_Found() throws Exception {
        when(bookService.getBookById(anyLong())).thenReturn(Optional.of(bookDTO));

        mockMvc.perform(get("/api/books/1")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(1L))
                .andExpect(jsonPath("$.title").value("Test Book"));
    }

    @Test
    @WithMockUser(roles = {"ADMIN", "LIBRARIAN", "MEMBER"})
    void getBookById_NotFound() throws Exception {
        when(bookService.getBookById(anyLong())).thenReturn(Optional.empty());

        mockMvc.perform(get("/api/books/99")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isNotFound());
    }

    @Test
    @WithMockUser(roles = {"ADMIN", "LIBRARIAN", "MEMBER"})
    void updateBook_Success() throws Exception {
        BookDTO updatedBookDTO = new BookDTO();
        updatedBookDTO.setId(1L);
        updatedBookDTO.setTitle("Updated Title");
        updatedBookDTO.setAuthor("Updated Author");
        updatedBookDTO.setIsbn("1234567890123");
        updatedBookDTO.setPrice(new BigDecimal("120.00"));
        updatedBookDTO.setStock(12);
        updatedBookDTO.setPublishedYear(2021);
        updatedBookDTO.setCategory("Fantasy");

        when(bookService.updateBook(anyLong(), any(BookDTO.class))).thenReturn(updatedBookDTO);

        mockMvc.perform(put("/api/books/1")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(updatedBookDTO)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.title").value("Updated Title"));
    }

    @Test
    @WithMockUser(roles = {"ADMIN", "LIBRARIAN", "MEMBER"})
    void updateBook_NotFound() throws Exception {
        when(bookService.updateBook(anyLong(), any(BookDTO.class)))
                .thenThrow(new BookNotFoundException("Book not found with ID: 99"));

        mockMvc.perform(put("/api/books/99")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(bookDTO)))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.message").value("Book not found with ID: 99"));
    }

    @Test
    @WithMockUser(roles = {"ADMIN", "LIBRARIAN", "MEMBER"})
    void deleteBook_Success() throws Exception {
        when(bookService.deleteBook(anyLong())).thenReturn(true);

        mockMvc.perform(delete("/api/books/1")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isNoContent());
    }

    @Test
    @WithMockUser(roles = {"ADMIN", "LIBRARIAN", "MEMBER"})
    void deleteBook_NotFound() throws Exception {
        when(bookService.deleteBook(anyLong()))
                .thenThrow(new BookNotFoundException("Book not found with ID: 99"));

        mockMvc.perform(delete("/api/books/99")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.message").value("Book not found with ID: 99"));
    }

    @Test
    void accessBookApi_Unauthorized() throws Exception {
        // No @WithMockUser, so it's an unauthenticated request
        mockMvc.perform(get("/api/books/1"))
                .andExpect(status().isUnauthorized());
    }
}
