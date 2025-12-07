package com.imashi.lms.backend.repository;

import com.imashi.lms.backend.entity.Reservation;
import com.imashi.lms.backend.entity.ReservationStatus;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface ReservationRepository extends JpaRepository<Reservation, Long> {
    List<Reservation> findByUserId(Long userId);
    List<Reservation> findByBookId(Long bookId);
    List<Reservation> findByUserIdAndStatus(Long userId, ReservationStatus status);
    Optional<Reservation> findByUserIdAndBookIdAndStatus(Long userId, Long bookId, ReservationStatus status);
    boolean existsByUserIdAndBookIdAndStatus(Long userId, Long bookId, ReservationStatus status);
}

