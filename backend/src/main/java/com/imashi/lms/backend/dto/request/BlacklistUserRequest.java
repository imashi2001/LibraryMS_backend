package com.imashi.lms.backend.dto.request;

import jakarta.validation.constraints.NotNull;
import lombok.Data;

@Data
public class BlacklistUserRequest {
    @NotNull(message = "Blacklist status is required")
    private Boolean isBlacklisted;
}

