package com.tbank.smart_budget_backend.service;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.tbank.smart_budget_backend.dto.TransactionDto;
import com.tbank.smart_budget_backend.model.Budget;
import com.tbank.smart_budget_backend.model.Category;
import com.tbank.smart_budget_backend.model.Transaction;
import com.tbank.smart_budget_backend.model.User;
import com.tbank.smart_budget_backend.repository.TransactionRepository;
import com.tbank.smart_budget_backend.repository.UserRepository;

@Service
public class TransactionService {
    private final UserRepository userRepository;
    private final TransactionRepository transactionRepository;

    public TransactionService(UserRepository userRepository, TransactionRepository transactionRepository) {
        this.userRepository = userRepository;
        this.transactionRepository = transactionRepository;
    }

    @Transactional
    public Transaction addTransaction(String email, TransactionDto dto) {
        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new RuntimeException("User not found"));

        Transaction transaction = new Transaction();
        transaction.setAmount(dto.getAmount());
        transaction.setDescription(dto.getDescription());
        transaction.setDate(LocalDateTime.now());
        transaction.setUser(user);

        // Простая классификация
        String desc = dto.getDescription().toLowerCase();
        Category cat = Category.OTHER;
        if (desc.contains("продукт") || desc.contains("магазин") || desc.contains("еда")) cat = Category.GROCERIES;
        else if (desc.contains("жкх") || desc.contains("квартплата")) cat = Category.UTILITIES;
        else if (desc.contains("кино") || desc.contains("кафе") || desc.contains("ресторан")) cat = Category.ENTERTAINMENT;
        else if (desc.contains("такси") || desc.contains("метро") || desc.contains("бензин")) cat = Category.TRANSPORT;
        else if (desc.contains("копилка") || desc.contains("вклад")) cat = Category.SAVINGS;

        transaction.setCategory(cat);
        transactionRepository.save(transaction);

        // Обновляем потраченные суммы в бюджете
        Budget budget = user.getBudget();
        if (budget != null) {
            Map<Category, Double> spent = budget.getSpent();
            if (spent == null) spent = new HashMap<>();
            spent.put(cat, spent.getOrDefault(cat, 0.0) + transaction.getAmount());
            budget.setSpent(spent);
            // budgetRepository.save(budget) // автоматически, тк budget уже в контексте
        }

        return transaction;
    }

    public List<Transaction> getTransactions(String email) {
        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new RuntimeException("User not found"));
        return transactionRepository.findByUserId(user.getId());
    }
}