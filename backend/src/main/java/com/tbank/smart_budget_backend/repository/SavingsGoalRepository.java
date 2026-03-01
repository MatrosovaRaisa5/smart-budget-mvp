package com.tbank.smart_budget_backend.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

import com.tbank.smart_budget_backend.model.SavingsGoal;

public interface SavingsGoalRepository extends JpaRepository<SavingsGoal, Long> {
    List<SavingsGoal> findByUserId(Long userId);
}