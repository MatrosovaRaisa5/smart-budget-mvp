package com.tbank.smart_budget_backend.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

import com.tbank.smart_budget_backend.model.Transaction;

public interface TransactionRepository extends JpaRepository<Transaction, Long> {
    List<Transaction> findByUserId(Long userId);
}

