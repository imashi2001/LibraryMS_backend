package com.imashi.lms.backend.service;

import com.imashi.lms.backend.dto.request.ReserveBookRequest;
import com.imashi.lms.backend.dto.response.BookResponse;
import com.imashi.lms.backend.dto.response.CategoryResponse;
import com.imashi.lms.backend.dto.response.ReservationResponse;
import com.imashi.lms.backend.dto.response.UserResponse;
import com.imashi.lms.backend.entity.*;
import com.imashi.lms.backend.exception.ResourceAlreadyExistsException;
import com.imashi.lms.backend.exception.ResourceNotFoundException;
import com.imashi.lms.backend.exception.UnauthorizedException;
import com.imashi.lms.backend.repository.BookRepository;
import com.imashi.lms.backend.repository.ReservationRepository;
import com.imashi.lms.backend.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class UserService {
    
    @Autowired
    private BookRepository bookRepository;
    
    @Autowired
    private ReservationRepository reservationRepository;
    
    @Autowired
    private UserRepository userRepository;
    
    // Get current authenticated user
    private User getCurrentUser() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication == null) {
            throw new UnauthorizedException("User not authenticated");
        }
        
        String email = authentication.getName();
        return userRepository.findByEmail(email)
                .orElseThrow(() -> new ResourceNotFoundException("User not found"));
    }
    
    // Get all books with filtering
    public List<BookResponse> getAllBooks(Long categoryId, String author, String genre, String language) {
        List<Book> books = bookRepository.findBooksWithFilters(categoryId, author, genre, language);
        return books.stream()
                .map(this::mapToBookResponse)
                .collect(Collectors.toList());
    }
    
    // Reserve a book
    @Transactional
    public ReservationResponse reserveBook(Long bookId, ReserveBookRequest request) {
        User user = getCurrentUser();
        
        // Validate reservation days (7, 14, or 21)
        if (request.getReservationDays() != 7 && 
            request.getReservationDays() != 14 && 
            request.getReservationDays() != 21) {
            throw new IllegalArgumentException("Reservation period must be 7, 14, or 21 days");
        }
        
        // Check if user is blacklisted
        if (user.getIsBlacklisted()) {
            throw new UnauthorizedException("User is blacklisted and cannot reserve books");
        }
        
        // Get book
        Book book = bookRepository.findById(bookId)
                .orElseThrow(() -> new ResourceNotFoundException("Book not found with id: " + bookId));
        
        // Check if book is available
        if (book.getAvailableCopies() <= 0) {
            throw new ResourceNotFoundException("Book is not available for reservation");
        }
        
        if (book.getStatus() != BookStatus.AVAILABLE) {
            throw new ResourceNotFoundException("Book is not available for reservation");
        }
        
        // Check if user already has an active reservation for this book
        if (reservationRepository.existsByUserIdAndBookIdAndStatus(
                user.getId(), bookId, ReservationStatus.ACTIVE)) {
            throw new ResourceAlreadyExistsException("You already have an active reservation for this book");
        }
        
        // Create reservation
        Reservation reservation = new Reservation();
        reservation.setUser(user);
        reservation.setBook(book);
        reservation.setReservationDate(LocalDateTime.now());
        reservation.setDueDate(LocalDateTime.now().plusDays(request.getReservationDays()));
        reservation.setStatus(ReservationStatus.ACTIVE);
        
        Reservation savedReservation = reservationRepository.save(reservation);
        
        // Update book status and available copies
        book.setAvailableCopies(book.getAvailableCopies() - 1);
        if (book.getAvailableCopies() == 0) {
            book.setStatus(BookStatus.RESERVED);
        }
        bookRepository.save(book);
        
        return mapToReservationResponse(savedReservation);
    }
    
    // Helper methods
    private BookResponse mapToBookResponse(Book book) {
        BookResponse response = new BookResponse();
        response.setId(book.getId());
        response.setTitle(book.getTitle());
        response.setAuthor(book.getAuthor());
        response.setIsbn(book.getIsbn());
        response.setCategory(book.getCategory() != null ? 
            mapToCategoryResponse(book.getCategory()) : null);
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
    
    private ReservationResponse mapToReservationResponse(Reservation reservation) {
        ReservationResponse response = new ReservationResponse();
        response.setId(reservation.getId());
        response.setUser(mapToUserResponse(reservation.getUser()));
        response.setBook(mapToBookResponse(reservation.getBook()));
        response.setReservationDate(reservation.getReservationDate());
        response.setDueDate(reservation.getDueDate());
        response.setReturnDate(reservation.getReturnDate());
        response.setStatus(reservation.getStatus());
        response.setCreatedAt(reservation.getCreatedAt());
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

