package com.imashi.lms.backend.dto.request;

import jakarta.validation.constraints.NotBlank;
import lombok.Data;

@Data
public class AddCategoryRequest {
    @NotBlank(message = "Category name is required")
    private String name;
    
    private String description;
}

