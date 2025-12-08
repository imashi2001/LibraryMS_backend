package com.imashi.lms.backend.service;

import com.imashi.lms.backend.dto.request.UpdateBookRequest;
import com.imashi.lms.backend.dto.response.BookResponse;
import com.imashi.lms.backend.dto.response.CategoryResponse;
import com.imashi.lms.backend.entity.Book;
import com.imashi.lms.backend.entity.BookStatus;
import com.imashi.lms.backend.entity.Category;
import com.imashi.lms.backend.exception.ResourceAlreadyExistsException;
import com.imashi.lms.backend.exception.ResourceNotFoundException;
import com.imashi.lms.backend.repository.BookRepository;
import com.imashi.lms.backend.repository.CategoryRepository;
import com.imashi.lms.backend.repository.ReservationRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class BookService {
    
    @Autowired
    private BookRepository bookRepository;
    
    @Autowired
    private CategoryRepository categoryRepository;
    
    @Autowired
    private ReservationRepository reservationRepository;
    
    public BookResponse getBookById(Long id) {
        Book book = bookRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Book not found with id: " + id));
        return mapToBookResponse(book);
    }
    
    @Transactional
    public BookResponse updateBook(Long id, UpdateBookRequest request) {
        Book book = bookRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Book not found with id: " + id));
        
        // Check if ISBN is being changed and if new ISBN already exists
        if (request.getIsbn() != null && !request.getIsbn().isEmpty()) {
            if (!request.getIsbn().equals(book.getIsbn()) && 
                bookRepository.existsByIsbn(request.getIsbn())) {
                throw new ResourceAlreadyExistsException("Book with ISBN " + request.getIsbn() + " already exists");
            }
        }
        
        // Check if category exists
        Category category = categoryRepository.findById(request.getCategoryId())
                .orElseThrow(() -> new ResourceNotFoundException("Category not found with id: " + request.getCategoryId()));
        
        // Update book fields
        book.setTitle(request.getTitle());
        book.setAuthor(request.getAuthor());
        book.setIsbn(request.getIsbn());
        book.setCategory(category);
        book.setDescription(request.getDescription());
        book.setGenre(request.getGenre());
        book.setLanguage(request.getLanguage() != null ? request.getLanguage() : "English");
        
        // Handle total copies update
        int oldTotalCopies = book.getTotalCopies();
        int newTotalCopies = request.getTotalCopies();
        book.setTotalCopies(newTotalCopies);
        
        // Adjust available copies based on total copies change
        int difference = newTotalCopies - oldTotalCopies;
        book.setAvailableCopies(book.getAvailableCopies() + difference);
        
        // Ensure available copies doesn't go negative
        if (book.getAvailableCopies() < 0) {
            book.setAvailableCopies(0);
        }
        
        // Update status based on available copies
        if (book.getAvailableCopies() == 0) {
            book.setStatus(BookStatus.RESERVED);
        } else if (book.getStatus() == BookStatus.RESERVED && book.getAvailableCopies() > 0) {
            book.setStatus(BookStatus.AVAILABLE);
        }
        
        // Update image URL if provided
        if (request.getImageUrl() != null) {
            book.setImageUrl(request.getImageUrl());
        }
        
        Book updatedBook = bookRepository.save(book);
        return mapToBookResponse(updatedBook);
    }
    
    @Transactional
    public void deleteBook(Long id) {
        Book book = bookRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Book not found with id: " + id));
        
        // Check if there are any active reservations for this book
        long activeReservationsCount = reservationRepository.findByBookId(id).stream()
                .filter(r -> r.getStatus().name().equals("ACTIVE"))
                .count();
        
        if (activeReservationsCount > 0) {
            throw new IllegalArgumentException("Cannot delete book. There are active reservations for this book.");
        }
        
        bookRepository.delete(book);
    }
    
    // Get books with pagination
    public Page<BookResponse> getAllBooks(Pageable pageable, Long categoryId, String author, String genre, String language, String title) {
        // If no filters, return all books with pagination
        if (categoryId == null && (author == null || author.isEmpty()) && 
            (genre == null || genre.isEmpty()) && (language == null || language.isEmpty()) &&
            (title == null || title.isEmpty())) {
            Page<Book> books = bookRepository.findAll(pageable);
            return books.map(this::mapToBookResponse);
        }
        
        // Use filtered query - Note: This doesn't support pagination directly
        // For production, you might want to create a custom repository method with pagination
        var books = bookRepository.findBooksWithFilters(categoryId, author, genre, language, title);
        
        // Convert to page manually (for now - could be optimized with custom query)
        int start = (int) pageable.getOffset();
        int end = Math.min((start + pageable.getPageSize()), books.size());
        var pagedBooks = books.subList(start, end);
        
        return new org.springframework.data.domain.PageImpl<>(
            pagedBooks.stream().map(this::mapToBookResponse).toList(),
            pageable,
            books.size()
        );
    }
    
    private BookResponse mapToBookResponse(Book book) {
        BookResponse response = new BookResponse();
        response.setId(book.getId());
        response.setTitle(book.getTitle());
        response.setAuthor(book.getAuthor());
        response.setIsbn(book.getIsbn());
        response.setCategory(book.getCategory() != null ? mapToCategoryResponse(book.getCategory()) : null);
        response.setStatus(book.getStatus());
        response.setTotalCopies(book.getTotalCopies());
        response.setAvailableCopies(book.getAvailableCopies());
        response.setDescription(book.getDescription());
        response.setGenre(book.getGenre());
        response.setLanguage(book.getLanguage());
        response.setImageUrl(book.getImageUrl());
        response.setCreatedAt(book.getCreatedAt());
        response.setUpdatedAt(book.getUpdatedAt());
        return response;
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
