package com.imashi.lms.backend.service;

import com.imashi.lms.backend.dto.request.UpdateCategoryRequest;
import com.imashi.lms.backend.dto.response.CategoryResponse;
import com.imashi.lms.backend.entity.Category;
import com.imashi.lms.backend.exception.ResourceAlreadyExistsException;
import com.imashi.lms.backend.exception.ResourceNotFoundException;
import com.imashi.lms.backend.repository.BookRepository;
import com.imashi.lms.backend.repository.CategoryRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class CategoryService {
    
    @Autowired
    private CategoryRepository categoryRepository;
    
    @Autowired
    private BookRepository bookRepository;
    
    public List<CategoryResponse> getAllCategories() {
        List<Category> categories = categoryRepository.findAll();
        return categories.stream()
                .map(this::mapToCategoryResponse)
                .collect(Collectors.toList());
    }
    
    public CategoryResponse getCategoryById(Long id) {
        Category category = categoryRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Category not found with id: " + id));
        return mapToCategoryResponse(category);
    }
    
    @Transactional
    public CategoryResponse updateCategory(Long id, UpdateCategoryRequest request) {
        Category category = categoryRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Category not found with id: " + id));
        
        // Check if name is being changed and if new name already exists
        if (!category.getName().equals(request.getName()) && 
            categoryRepository.existsByName(request.getName())) {
            throw new ResourceAlreadyExistsException("Category with name " + request.getName() + " already exists");
        }
        
        category.setName(request.getName());
        category.setDescription(request.getDescription());
        
        Category updatedCategory = categoryRepository.save(category);
        return mapToCategoryResponse(updatedCategory);
    }
    
    @Transactional
    public void deleteCategory(Long id) {
        Category category = categoryRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Category not found with id: " + id));
        
        // Check if any books are using this category
        if (bookRepository.findByCategoryId(id).size() > 0) {
            throw new IllegalArgumentException("Cannot delete category. Books are still associated with this category.");
        }
        
        categoryRepository.delete(category);
    }
    
    private CategoryResponse mapToCategoryResponse(Category category) {
        CategoryResponse response = new CategoryResponse();
        response.setId(category.getId());
        response.setName(category.getName());
        response.setDescription(category.getDescription());
        response.setCreatedAt(category.getCreatedAt());
        return response;
    }
}
