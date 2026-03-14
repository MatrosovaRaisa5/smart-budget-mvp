package com.tbank.smart_budget_backend.repository;

import java.time.LocalDateTime;
import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import com.tbank.smart_budget_backend.model.Transaction;

public interface TransactionRepository extends JpaRepository<Transaction, Long> {
    List<Transaction> findByUserIdOrderByDateDesc(Long userId);

    // поиск с фильтрами
    @Query("SELECT t FROM Transaction t WHERE t.user.id = :userId " +
           "AND (:search IS NULL OR LOWER(t.description) LIKE LOWER(CONCAT('%', :search, '%'))) " +
           "AND (:categoryId IS NULL OR t.category.id = :categoryId) " +
           "AND (:fromDate IS NULL OR t.date >= :fromDate) " +
           "AND (:toDate IS NULL OR t.date <= :toDate) " +
           "ORDER BY t.date DESC")
    List<Transaction> findWithFilters(@Param("userId") Long userId,
                                      @Param("search") String search,
                                      @Param("categoryId") Long categoryId,
                                      @Param("fromDate") LocalDateTime fromDate,
                                      @Param("toDate") LocalDateTime toDate);
}