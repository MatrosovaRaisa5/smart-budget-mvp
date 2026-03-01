package com.tbank.smart_budget_backend.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import com.tbank.smart_budget_backend.model.Budget;

public interface BudgetRepository extends JpaRepository<Budget, Long> {
}