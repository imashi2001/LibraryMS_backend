package com.imashi.lms.backend.dto.response;

import com.imashi.lms.backend.entity.ReservationStatus;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class ReservationResponse {
    private Long id;
    private UserResponse user;
    private BookResponse book;
    private LocalDateTime reservationDate;
    private LocalDateTime dueDate;
    private LocalDateTime returnDate;
    private ReservationStatus status;
    private LocalDateTime createdAt;
}

