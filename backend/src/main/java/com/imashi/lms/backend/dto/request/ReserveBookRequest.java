package com.imashi.lms.backend.dto.request;

import jakarta.validation.constraints.NotNull;
import lombok.Data;

@Data
public class ReserveBookRequest {
    @NotNull(message = "Reservation period is required")
    private Integer reservationDays; // 7, 14, or 21
}

