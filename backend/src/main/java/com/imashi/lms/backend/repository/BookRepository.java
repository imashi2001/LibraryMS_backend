package com.imashi.lms.backend.repository;

import com.imashi.lms.backend.entity.Book;
import com.imashi.lms.backend.entity.BookStatus;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface BookRepository extends JpaRepository<Book, Long> {
    Optional<Book> findByIsbn(String isbn);
    boolean existsByIsbn(String isbn);
    List<Book> findByStatus(BookStatus status);
    List<Book> findByCategoryId(Long categoryId);
}

