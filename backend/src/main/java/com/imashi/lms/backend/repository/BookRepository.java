package com.imashi.lms.backend.repository;

import com.imashi.lms.backend.entity.Book;
import com.imashi.lms.backend.entity.BookStatus;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface BookRepository extends JpaRepository<Book, Long> {
    Optional<Book> findByIsbn(String isbn);
    boolean existsByIsbn(String isbn);
    List<Book> findByStatus(BookStatus status);
    List<Book> findByCategoryId(Long categoryId);
    
    @Query("SELECT b FROM Book b WHERE " +
           "(:categoryId IS NULL OR b.category.id = :categoryId) AND " +
           "(:author IS NULL OR LOWER(b.author) LIKE LOWER(CONCAT('%', :author, '%'))) AND " +
           "(:genre IS NULL OR LOWER(b.genre) LIKE LOWER(CONCAT('%', :genre, '%'))) AND " +
           "(:language IS NULL OR LOWER(b.language) LIKE LOWER(CONCAT('%', :language, '%')))")
    List<Book> findBooksWithFilters(
        @Param("categoryId") Long categoryId,
        @Param("author") String author,
        @Param("genre") String genre,
        @Param("language") String language
    );
}

