package com.imashi.lms.backend.dto.response;

import com.imashi.lms.backend.entity.BookStatus;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class BookResponse {
    private Long id;
    private String title;
    private String author;
    private String isbn;
    private CategoryResponse category;
    private BookStatus status;
    private Integer totalCopies;
    private Integer availableCopies;
    private String description;
    private String genre;
    private String language;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
}

