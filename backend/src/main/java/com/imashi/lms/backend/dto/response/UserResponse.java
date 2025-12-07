package com.imashi.lms.backend.dto.response;

import com.imashi.lms.backend.entity.Role;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class UserResponse {
    private Long id;
    private String email;
    private Role role;
    private Boolean isBlacklisted;
    private LocalDateTime createdAt;
}

