package com.tbank.smart_budget_backend.repository;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.stereotype.Repository;

import com.tbank.smart_budget_backend.model.Budget;
import com.tbank.smart_budget_backend.model.Transaction;

@Repository
public class DataStore {
    // Хранилище для бюджета одного пользователя (ID пользователя = 1L)
    private final Map<Long, Budget> userBudgets = new HashMap<>();
    // Список всех транзакций
    private final List<Transaction> transactions = new ArrayList<>();
    private Long nextTransactionId = 1L;

    public Budget getBudget(Long userId) {
        return userBudgets.computeIfAbsent(userId, k -> new Budget());
    }

    public void saveBudget(Long userId, Budget budget) {
        userBudgets.put(userId, budget);
    }

    public Transaction addTransaction(Transaction transaction) {
        transaction.setId(nextTransactionId++);
        transactions.add(transaction);
        return transaction;
    }

    public List<Transaction> getTransactions() {
        return transactions;
    }
}