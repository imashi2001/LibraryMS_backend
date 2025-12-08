package com.imashi.lms.backend.controller;

import com.imashi.lms.backend.dto.response.ApiResponse;
import com.imashi.lms.backend.dto.response.ReservationResponse;
import com.imashi.lms.backend.service.ReservationService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/reservations")
@CrossOrigin(origins = "http://localhost:3000")
public class ReservationController {
    
    @Autowired
    private ReservationService reservationService;
    
    @GetMapping("/my-reservations")
    @PreAuthorize("hasRole('USER')")
    public ResponseEntity<ApiResponse<List<ReservationResponse>>> getMyReservations() {
        List<ReservationResponse> reservations = reservationService.getUserReservations();
        ApiResponse<List<ReservationResponse>> response = new ApiResponse<>(
            "SUCCESS",
            "Reservations retrieved successfully",
            reservations
        );
        return ResponseEntity.ok(response);
    }
    
    @GetMapping
    @PreAuthorize("hasRole('LIBRARIAN')")
    public ResponseEntity<ApiResponse<List<ReservationResponse>>> getAllReservations() {
        List<ReservationResponse> reservations = reservationService.getAllReservations();
        ApiResponse<List<ReservationResponse>> response = new ApiResponse<>(
            "SUCCESS",
            "All reservations retrieved successfully",
            reservations
        );
        return ResponseEntity.ok(response);
    }
    
    @PutMapping("/{id}/return")
    @PreAuthorize("hasRole('LIBRARIAN')")
    public ResponseEntity<ApiResponse<ReservationResponse>> returnBook(@PathVariable Long id) {
        ReservationResponse reservation = reservationService.returnBook(id);
        ApiResponse<ReservationResponse> response = new ApiResponse<>(
            "SUCCESS",
            "Book returned successfully",
            reservation
        );
        return ResponseEntity.ok(response);
    }
    
    @DeleteMapping("/{id}")
    public ResponseEntity<ApiResponse<Void>> cancelReservation(@PathVariable Long id) {
        reservationService.cancelReservation(id);
        ApiResponse<Void> response = new ApiResponse<>(
            "SUCCESS",
            "Reservation cancelled successfully",
            null
        );
        return ResponseEntity.ok(response);
    }
}

