package com.imashi.lms.backend.service;

import com.imashi.lms.backend.dto.request.*;
import com.imashi.lms.backend.dto.response.BookResponse;
import com.imashi.lms.backend.dto.response.CategoryResponse;
import com.imashi.lms.backend.dto.response.UserResponse;
import com.imashi.lms.backend.entity.*;
import com.imashi.lms.backend.exception.ResourceAlreadyExistsException;
import com.imashi.lms.backend.exception.ResourceNotFoundException;
import com.imashi.lms.backend.exception.UnauthorizedException;
import com.imashi.lms.backend.repository.BookRepository;
import com.imashi.lms.backend.repository.CategoryRepository;
import com.imashi.lms.backend.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class LibrarianService {
    
    @Autowired
    private BookRepository bookRepository;
    
    @Autowired
    private CategoryRepository categoryRepository;
    
    @Autowired
    private UserRepository userRepository;
    
    // Helper method to check if current user is librarian
    private void checkLibrarianRole() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication == null || !authentication.getAuthorities().stream()
                .anyMatch(a -> a.getAuthority().equals("ROLE_LIBRARIAN"))) {
            throw new UnauthorizedException("Only librarians can perform this action");
        }
    }
    
    // Add new book
    @Transactional
    public BookResponse addBook(AddBookRequest request) {
        checkLibrarianRole();
        
        // Check if ISBN already exists
        if (request.getIsbn() != null && !request.getIsbn().isEmpty()) {
            if (bookRepository.existsByIsbn(request.getIsbn())) {
                throw new ResourceAlreadyExistsException("Book with ISBN " + request.getIsbn() + " already exists");
            }
        }
        
        // Check if category exists
        Category category = categoryRepository.findById(request.getCategoryId())
                .orElseThrow(() -> new ResourceNotFoundException("Category not found with id: " + request.getCategoryId()));
        
        Book book = new Book();
        book.setTitle(request.getTitle());
        book.setAuthor(request.getAuthor());
        book.setIsbn(request.getIsbn());
        book.setCategory(category);
        book.setTotalCopies(request.getTotalCopies());
        book.setAvailableCopies(request.getTotalCopies());
        book.setStatus(BookStatus.AVAILABLE);
        book.setDescription(request.getDescription());
        book.setGenre(request.getGenre());
        book.setLanguage(request.getLanguage() != null ? request.getLanguage() : "English");
        
        Book savedBook = bookRepository.save(book);
        return mapToBookResponse(savedBook);
    }
    
    // Update book status
    @Transactional
    public BookResponse updateBookStatus(Long bookId, UpdateBookStatusRequest request) {
        checkLibrarianRole();
        
        Book book = bookRepository.findById(bookId)
                .orElseThrow(() -> new ResourceNotFoundException("Book not found with id: " + bookId));
        
        book.setStatus(request.getStatus());
        Book updatedBook = bookRepository.save(book);
        return mapToBookResponse(updatedBook);
    }
    
    // Add new category
    @Transactional
    public CategoryResponse addCategory(AddCategoryRequest request) {
        checkLibrarianRole();
        
        if (categoryRepository.existsByName(request.getName())) {
            throw new ResourceAlreadyExistsException("Category with name " + request.getName() + " already exists");
        }
        
        Category category = new Category();
        category.setName(request.getName());
        category.setDescription(request.getDescription());
        
        Category savedCategory = categoryRepository.save(category);
        return mapToCategoryResponse(savedCategory);
    }
    
    // Blacklist/Unblacklist user
    @Transactional
    public UserResponse blacklistUser(Long userId, BlacklistUserRequest request) {
        checkLibrarianRole();
        
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new ResourceNotFoundException("User not found with id: " + userId));
        
        // Prevent blacklisting librarians
        if (user.getRole() == Role.LIBRARIAN) {
            throw new UnauthorizedException("Cannot blacklist a librarian");
        }
        
        user.setIsBlacklisted(request.getIsBlacklisted());
        User updatedUser = userRepository.save(user);
        return mapToUserResponse(updatedUser);
    }
    
    // Helper methods to map entities to DTOs
    private BookResponse mapToBookResponse(Book book) {
        BookResponse response = new BookResponse();
        response.setId(book.getId());
        response.setTitle(book.getTitle());
        response.setAuthor(book.getAuthor());
        response.setIsbn(book.getIsbn());
        response.setCategory(book.getCategory() != null ? mapToCategoryResponse(book.getCategory()) : null);
        response.setStatus(book.getStatus());
        response.setTotalCopies(book.getTotalCopies());
        response.setAvailableCopies(book.getAvailableCopies());
        response.setDescription(book.getDescription());
        response.setGenre(book.getGenre());
        response.setLanguage(book.getLanguage());
        response.setCreatedAt(book.getCreatedAt());
        response.setUpdatedAt(book.getUpdatedAt());
        return response;
    }
    
    private CategoryResponse mapToCategoryResponse(Category category) {
        CategoryResponse response = new CategoryResponse();
        response.setId(category.getId());
        response.setName(category.getName());
        response.setDescription(category.getDescription());
        response.setCreatedAt(category.getCreatedAt());
        return response;
    }
    
    private UserResponse mapToUserResponse(User user) {
        UserResponse response = new UserResponse();
        response.setId(user.getId());
        response.setEmail(user.getEmail());
        response.setRole(user.getRole());
        response.setIsBlacklisted(user.getIsBlacklisted());
        response.setCreatedAt(user.getCreatedAt());
        return response;
    }
}

