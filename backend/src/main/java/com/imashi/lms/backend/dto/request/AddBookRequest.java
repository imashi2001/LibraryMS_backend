package com.imashi.lms.backend.dto.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import lombok.Data;

@Data
public class AddBookRequest {
    @NotBlank(message = "Title is required")
    private String title;
    
    @NotBlank(message = "Author is required")
    private String author;
    
    private String isbn;
    
    @NotNull(message = "Category ID is required")
    private Long categoryId;
    
    @NotNull(message = "Total copies is required")
    @Positive(message = "Total copies must be positive")
    private Integer totalCopies;
    
    private String description;
    
    private String genre;
    
    private String language;
}

