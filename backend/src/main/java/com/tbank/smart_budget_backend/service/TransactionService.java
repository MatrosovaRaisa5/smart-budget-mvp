package com.tbank.smart_budget_backend.service;

import java.util.HashMap;
import java.util.Map;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.tbank.smart_budget_backend.dto.TransactionDto;
import com.tbank.smart_budget_backend.model.Budget;
import com.tbank.smart_budget_backend.model.Category;
import com.tbank.smart_budget_backend.model.Transaction;
import com.tbank.smart_budget_backend.model.User;
import com.tbank.smart_budget_backend.repository.BudgetRepository;
import com.tbank.smart_budget_backend.repository.CategoryRepository;
import com.tbank.smart_budget_backend.repository.TransactionRepository;
import com.tbank.smart_budget_backend.repository.UserRepository;

@Service
public class TransactionService {

    private final UserRepository userRepository;
    private final TransactionRepository transactionRepository;
    private final CategoryRepository categoryRepository;
    private final BudgetRepository budgetRepository;

    public TransactionService(UserRepository userRepository,
                              TransactionRepository transactionRepository,
                              CategoryRepository categoryRepository,
                              BudgetRepository budgetRepository) {
        this.userRepository = userRepository;
        this.transactionRepository = transactionRepository;
        this.categoryRepository = categoryRepository;
        this.budgetRepository = budgetRepository;
    }

    @Transactional
    public Transaction addTransaction(String email, TransactionDto dto) {
        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new RuntimeException("User not found"));

        Category category = categoryRepository.findById(dto.getCategoryId())
                .orElseThrow(() -> new RuntimeException("Category not found"));

        // проверяем, доступна ли категория пользователю (общая или его)
        if (category.getUser() != null && !category.getUser().getId().equals(user.getId())) {
            throw new RuntimeException("Category not available for this user");
        }

        Transaction transaction = new Transaction();
        transaction.setAmount(dto.getAmount());
        transaction.setDescription(dto.getDescription());
        transaction.setDate(dto.getDate().atStartOfDay());
        transaction.setCategory(category);
        transaction.setUser(user);
        transaction = transactionRepository.save(transaction);

        // Обновляем потраченные суммы в бюджете
        Budget budget = user.getBudget();
        if (budget != null) {
            Map<Long, Double> spent = budget.getSpent();
            if (spent == null) spent = new HashMap<>();
            spent.put(category.getId(), spent.getOrDefault(category.getId(), 0.0) + transaction.getAmount());
            budget.setSpent(spent);
        }

        return transaction;
    }

    @Transactional
    public void deleteTransaction(String email, Long transactionId) {
        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new RuntimeException("User not found"));
        Transaction transaction = transactionRepository.findById(transactionId)
                .orElseThrow(() -> new RuntimeException("Transaction not found"));
        if (!transaction.getUser().getId().equals(user.getId())) {
            throw new RuntimeException("Access denied");
        }

        // уменьшаем spent в бюджете
        Budget budget = user.getBudget();
        if (budget != null && transaction.getCategory() != null) {
            Map<Long, Double> spent = budget.getSpent();
            if (spent != null) {
                Long catId = transaction.getCategory().getId();
                spent.put(catId, spent.getOrDefault(catId, 0.0) - transaction.getAmount());
                budget.setSpent(spent);
            }
        }

        transactionRepository.delete(transaction);
    }
}