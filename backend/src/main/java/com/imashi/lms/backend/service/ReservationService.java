package com.imashi.lms.backend.service;

import com.imashi.lms.backend.dto.response.BookResponse;
import com.imashi.lms.backend.dto.response.ReservationResponse;
import com.imashi.lms.backend.dto.response.UserResponse;
import com.imashi.lms.backend.entity.*;
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
public class ReservationService {
    
    @Autowired
    private ReservationRepository reservationRepository;
    
    @Autowired
    private BookRepository bookRepository;
    
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
    
    // Get user's own reservations
    public List<ReservationResponse> getUserReservations() {
        User user = getCurrentUser();
        List<Reservation> reservations = reservationRepository.findByUserId(user.getId());
        return reservations.stream()
                .map(this::mapToReservationResponse)
                .collect(Collectors.toList());
    }
    
    // Get all reservations (LIBRARIAN only)
    public List<ReservationResponse> getAllReservations() {
        checkLibrarianRole();
        List<Reservation> reservations = reservationRepository.findAll();
        return reservations.stream()
                .map(this::mapToReservationResponse)
                .collect(Collectors.toList());
    }
    
    // Return book (LIBRARIAN only)
    @Transactional
    public ReservationResponse returnBook(Long reservationId) {
        checkLibrarianRole();
        
        Reservation reservation = reservationRepository.findById(reservationId)
                .orElseThrow(() -> new ResourceNotFoundException("Reservation not found with id: " + reservationId));
        
        if (reservation.getStatus() != ReservationStatus.ACTIVE) {
            throw new IllegalArgumentException("Reservation is not active");
        }
        
        // Update reservation
        reservation.setReturnDate(LocalDateTime.now());
        reservation.setStatus(ReservationStatus.RETURNED);
        Reservation updatedReservation = reservationRepository.save(reservation);
        
        // Update book availability
        Book book = reservation.getBook();
        book.setAvailableCopies(book.getAvailableCopies() + 1);
        if (book.getStatus() == BookStatus.RESERVED && book.getAvailableCopies() > 0) {
            book.setStatus(BookStatus.AVAILABLE);
        }
        bookRepository.save(book);
        
        return mapToReservationResponse(updatedReservation);
    }
    
    // Cancel reservation
    @Transactional
    public void cancelReservation(Long reservationId) {
        User user = getCurrentUser();
        
        Reservation reservation = reservationRepository.findById(reservationId)
                .orElseThrow(() -> new ResourceNotFoundException("Reservation not found with id: " + reservationId));
        
        // Check if user owns this reservation or is librarian
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        boolean isLibrarian = authentication.getAuthorities().stream()
                .anyMatch(a -> a.getAuthority().equals("ROLE_LIBRARIAN"));
        
        if (!isLibrarian && !reservation.getUser().getId().equals(user.getId())) {
            throw new UnauthorizedException("You can only cancel your own reservations");
        }
        
        if (reservation.getStatus() != ReservationStatus.ACTIVE) {
            throw new IllegalArgumentException("Only active reservations can be cancelled");
        }
        
        // Update reservation
        reservation.setStatus(ReservationStatus.CANCELLED);
        reservationRepository.save(reservation);
        
        // Update book availability
        Book book = reservation.getBook();
        book.setAvailableCopies(book.getAvailableCopies() + 1);
        if (book.getStatus() == BookStatus.RESERVED && book.getAvailableCopies() > 0) {
            book.setStatus(BookStatus.AVAILABLE);
        }
        bookRepository.save(book);
    }
    
    // Helper method to check if current user is librarian
    private void checkLibrarianRole() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication == null || !authentication.getAuthorities().stream()
                .anyMatch(a -> a.getAuthority().equals("ROLE_LIBRARIAN"))) {
            throw new UnauthorizedException("Only librarians can perform this action");
        }
    }
    
    // Helper methods to map entities to DTOs
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
        response.setName(user.getName());
        response.setRole(user.getRole());
        response.setIsBlacklisted(user.getIsBlacklisted());
        response.setCreatedAt(user.getCreatedAt());
        return response;
    }
    
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
        response.setImageUrl(book.getImageUrl());
        response.setCreatedAt(book.getCreatedAt());
        response.setUpdatedAt(book.getUpdatedAt());
        return response;
    }
    
    private com.imashi.lms.backend.dto.response.CategoryResponse mapToCategoryResponse(Category category) {
        com.imashi.lms.backend.dto.response.CategoryResponse response = new com.imashi.lms.backend.dto.response.CategoryResponse();
        response.setId(category.getId());
        response.setName(category.getName());
        response.setDescription(category.getDescription());
        response.setCreatedAt(category.getCreatedAt());
        return response;
    }
}
