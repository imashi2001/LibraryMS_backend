package com.imashi.lms.backend.dto.request;

import com.imashi.lms.backend.entity.BookStatus;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

@Data
public class UpdateBookStatusRequest {
    @NotNull(message = "Status is required")
    private BookStatus status;
}

