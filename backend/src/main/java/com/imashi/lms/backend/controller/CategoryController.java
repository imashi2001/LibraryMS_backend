package com.imashi.lms.backend.controller;

import com.imashi.lms.backend.dto.request.UpdateCategoryRequest;
import com.imashi.lms.backend.dto.response.ApiResponse;
import com.imashi.lms.backend.dto.response.CategoryResponse;
import com.imashi.lms.backend.service.CategoryService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/categories")
@CrossOrigin(origins = {"http://localhost:3000", "http://localhost:3001"})
public class CategoryController {
    
    @Autowired
    private CategoryService categoryService;
    
    @GetMapping
    public ResponseEntity<ApiResponse<List<CategoryResponse>>> getAllCategories() {
        List<CategoryResponse> categories = categoryService.getAllCategories();
        ApiResponse<List<CategoryResponse>> response = new ApiResponse<>(
            "SUCCESS",
            "Categories retrieved successfully",
            categories
        );
        return ResponseEntity.ok(response);
    }
    
    @GetMapping("/{id}")
    public ResponseEntity<ApiResponse<CategoryResponse>> getCategoryById(@PathVariable Long id) {
        CategoryResponse category = categoryService.getCategoryById(id);
        ApiResponse<CategoryResponse> response = new ApiResponse<>(
            "SUCCESS",
            "Category retrieved successfully",
            category
        );
        return ResponseEntity.ok(response);
    }
    
    @PutMapping("/{id}")
    @PreAuthorize("hasRole('LIBRARIAN')")
    public ResponseEntity<ApiResponse<CategoryResponse>> updateCategory(
            @PathVariable Long id,
            @Valid @RequestBody UpdateCategoryRequest request) {
        CategoryResponse category = categoryService.updateCategory(id, request);
        ApiResponse<CategoryResponse> response = new ApiResponse<>(
            "SUCCESS",
            "Category updated successfully",
            category
        );
        return ResponseEntity.ok(response);
    }
    
    @DeleteMapping("/{id}")
    @PreAuthorize("hasRole('LIBRARIAN')")
    public ResponseEntity<ApiResponse<Void>> deleteCategory(@PathVariable Long id) {
        categoryService.deleteCategory(id);
        ApiResponse<Void> response = new ApiResponse<>(
            "SUCCESS",
            "Category deleted successfully",
            null
        );
        return ResponseEntity.ok(response);
    }
}

