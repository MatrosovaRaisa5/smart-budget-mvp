package com.tbank.smart_budget_backend.repository;

import com.tbank.smart_budget_backend.model.Income;
import com.tbank.smart_budget_backend.model.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.time.LocalDateTime;
import java.util.List;

public interface IncomeRepository extends JpaRepository<Income, Long> {
    List<Income> findByUserIdOrderByDateDesc(Long userId);

    // сумма доходов пользователя за период
    @Query("SELECT COALESCE(SUM(i.amount), 0) FROM Income i WHERE i.user = :user AND i.date BETWEEN :start AND :end")
    Double sumByUserAndDateBetween(@Param("user") User user,
                                   @Param("start") LocalDateTime start,
                                   @Param("end") LocalDateTime end);
}