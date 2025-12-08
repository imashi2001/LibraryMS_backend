package com.imashi.lms.backend.controller;

import com.imashi.lms.backend.dto.request.UpdateBookRequest;
import com.imashi.lms.backend.dto.response.ApiResponse;
import com.imashi.lms.backend.dto.response.BookResponse;
import com.imashi.lms.backend.service.BookService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/books")
@CrossOrigin(origins = {"http://localhost:3000", "http://localhost:3001"})
public class BookController {
    
    @Autowired
    private BookService bookService;
    
    @GetMapping("/{id}")
    public ResponseEntity<ApiResponse<BookResponse>> getBookById(@PathVariable Long id) {
        BookResponse book = bookService.getBookById(id);
        ApiResponse<BookResponse> response = new ApiResponse<>(
            "SUCCESS",
            "Book retrieved successfully",
            book
        );
        return ResponseEntity.ok(response);
    }
    
    @GetMapping
    public ResponseEntity<ApiResponse<Page<BookResponse>>> getAllBooks(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "20") int size,
            @RequestParam(required = false) Long categoryId,
            @RequestParam(required = false) String author,
            @RequestParam(required = false) String genre,
            @RequestParam(required = false) String language,
            @RequestParam(required = false) String title) {
        
        Pageable pageable = PageRequest.of(page, size);
        Page<BookResponse> books = bookService.getAllBooks(pageable, categoryId, author, genre, language, title);
        ApiResponse<Page<BookResponse>> response = new ApiResponse<>(
            "SUCCESS",
            "Books retrieved successfully",
            books
        );
        return ResponseEntity.ok(response);
    }
    
    @PutMapping("/{id}")
    @PreAuthorize("hasRole('LIBRARIAN')")
    public ResponseEntity<ApiResponse<BookResponse>> updateBook(
            @PathVariable Long id,
            @Valid @RequestBody UpdateBookRequest request) {
        BookResponse book = bookService.updateBook(id, request);
        ApiResponse<BookResponse> response = new ApiResponse<>(
            "SUCCESS",
            "Book updated successfully",
            book
        );
        return ResponseEntity.ok(response);
    }
    
    @DeleteMapping("/{id}")
    @PreAuthorize("hasRole('LIBRARIAN')")
    public ResponseEntity<ApiResponse<Void>> deleteBook(@PathVariable Long id) {
        bookService.deleteBook(id);
        ApiResponse<Void> response = new ApiResponse<>(
            "SUCCESS",
            "Book deleted successfully",
            null
        );
        return ResponseEntity.ok(response);
    }
}
