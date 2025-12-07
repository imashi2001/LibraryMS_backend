package com.imashi.lms.backend.controller;

import com.imashi.lms.backend.dto.request.ReserveBookRequest;
import com.imashi.lms.backend.dto.response.ApiResponse;
import com.imashi.lms.backend.dto.response.BookResponse;
import com.imashi.lms.backend.dto.response.ReservationResponse;
import com.imashi.lms.backend.service.UserService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/user")
@CrossOrigin(origins = "http://localhost:3000")
@PreAuthorize("hasRole('USER')")
public class UserController {
    
    @Autowired
    private UserService userService;
    
    @GetMapping("/books")
    public ResponseEntity<ApiResponse<List<BookResponse>>> getAllBooks(
            @RequestParam(required = false) Long categoryId,
            @RequestParam(required = false) String author,
            @RequestParam(required = false) String genre,
            @RequestParam(required = false) String language) {
        
        List<BookResponse> books = userService.getAllBooks(categoryId, author, genre, language);
        ApiResponse<List<BookResponse>> response = new ApiResponse<>(
            "SUCCESS", 
            "Books retrieved successfully", 
            books
        );
        return ResponseEntity.ok(response);
    }
    
    @PostMapping("/books/{bookId}/reserve")
    public ResponseEntity<ApiResponse<ReservationResponse>> reserveBook(
            @PathVariable Long bookId,
            @Valid @RequestBody ReserveBookRequest request) {
        
        ReservationResponse reservation = userService.reserveBook(bookId, request);
        ApiResponse<ReservationResponse> response = new ApiResponse<>(
            "SUCCESS", 
            "Book reserved successfully", 
            reservation
        );
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }
}

