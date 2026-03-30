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
    @Query("""
        SELECT t FROM Transaction t
        WHERE t.user.id = :userId
        AND (:categoryId IS NULL OR t.category.id = :categoryId)
        AND t.date >= COALESCE(:fromDate, t.date)
        AND t.date <= COALESCE(:toDate, t.date)
        ORDER BY t.date DESC
       """)
    List<Transaction> findWithFilters(@Param("userId") Long userId,
                                      @Param("categoryId") Long categoryId,
                                      @Param("fromDate") LocalDateTime fromDate,
                                      @Param("toDate") LocalDateTime toDate);
}