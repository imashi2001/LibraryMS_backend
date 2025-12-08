package com.imashi.lms.backend.controller;

import com.imashi.lms.backend.dto.request.*;
import com.imashi.lms.backend.dto.response.ApiResponse;
import com.imashi.lms.backend.dto.response.BookResponse;
import com.imashi.lms.backend.dto.response.CategoryResponse;
import com.imashi.lms.backend.dto.response.UserResponse;
import com.imashi.lms.backend.service.FileStorageService;
import com.imashi.lms.backend.service.LibrarianService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

@RestController
@RequestMapping("/api/librarian")
@CrossOrigin(origins = {"http://localhost:3000", "http://localhost:3001"})
@PreAuthorize("hasRole('LIBRARIAN')")
public class LibrarianController {
    
    @Autowired
    private LibrarianService librarianService;
    
    @Autowired
    private FileStorageService fileStorageService;
    
    @PostMapping("/books")
    public ResponseEntity<ApiResponse<BookResponse>> addBook(
            @Valid @ModelAttribute AddBookRequest request,
            @RequestParam(required = false) MultipartFile image) {
        
        // Handle image upload if provided
        String imageUrl = null;
        if (image != null && !image.isEmpty()) {
            String filename = fileStorageService.uploadBookImage(image);
            imageUrl = fileStorageService.getImageUrl(filename);
        }
        
        BookResponse book = librarianService.addBook(request, imageUrl);
        ApiResponse<BookResponse> response = new ApiResponse<>("SUCCESS", "Book added successfully", book);
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }
    
    @PutMapping("/books/{bookId}/status")
    public ResponseEntity<ApiResponse<BookResponse>> updateBookStatus(
            @PathVariable Long bookId,
            @Valid @RequestBody UpdateBookStatusRequest request) {
        BookResponse book = librarianService.updateBookStatus(bookId, request);
        ApiResponse<BookResponse> response = new ApiResponse<>("SUCCESS", "Book status updated successfully", book);
        return ResponseEntity.ok(response);
    }
    
    @PostMapping("/categories")
    public ResponseEntity<ApiResponse<CategoryResponse>> addCategory(@Valid @RequestBody AddCategoryRequest request) {
        CategoryResponse category = librarianService.addCategory(request);
        ApiResponse<CategoryResponse> response = new ApiResponse<>("SUCCESS", "Category added successfully", category);
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }
    
    @PutMapping("/users/{userId}/blacklist")
    public ResponseEntity<ApiResponse<UserResponse>> blacklistUser(
            @PathVariable Long userId,
            @Valid @RequestBody BlacklistUserRequest request) {
        UserResponse user = librarianService.blacklistUser(userId, request);
        ApiResponse<UserResponse> response = new ApiResponse<>("SUCCESS", 
            request.getIsBlacklisted() ? "User blacklisted successfully" : "User unblacklisted successfully", user);
        return ResponseEntity.ok(response);
    }
    
    @GetMapping("/users")
    public ResponseEntity<ApiResponse<java.util.List<UserResponse>>> getAllUsers() {
        java.util.List<UserResponse> users = librarianService.getAllUsers();
        ApiResponse<java.util.List<UserResponse>> response = new ApiResponse<>(
            "SUCCESS",
            "Users retrieved successfully",
            users
        );
        return ResponseEntity.ok(response);
    }
}

