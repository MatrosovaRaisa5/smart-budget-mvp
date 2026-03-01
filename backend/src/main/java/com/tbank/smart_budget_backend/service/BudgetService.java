package com.tbank.smart_budget_backend.service;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.tbank.smart_budget_backend.model.Budget;
import com.tbank.smart_budget_backend.model.Category;
import com.tbank.smart_budget_backend.model.User;
import com.tbank.smart_budget_backend.repository.BudgetRepository;
import com.tbank.smart_budget_backend.repository.UserRepository;

@Service
public class BudgetService {
    private final UserRepository userRepository;
    private final BudgetRepository budgetRepository;

    public BudgetService(UserRepository userRepository, BudgetRepository budgetRepository) {
        this.userRepository = userRepository;
        this.budgetRepository = budgetRepository;
    }

    @Transactional
    public String setupBudget(String email, Double income, Map<Category, Double> percentages) {
        double totalPercent = percentages.values().stream().mapToDouble(Double::doubleValue).sum();
        if (Math.abs(totalPercent - 1.0) > 0.001) {
            return "Ошибка: Сумма процентов должна быть 100%";
        }

        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new RuntimeException("User not found"));
        Budget budget = user.getBudget();
        if (budget == null) {
            budget = new Budget();
            budget.setUser(user);
            user.setBudget(budget);
        }
        budget.setMonthlyIncome(income);
        budget.setPercentages(percentages);
        budget.setSpent(new HashMap<>()); // сброс трат при изменении бюджета
        budgetRepository.save(budget);
        return "OK: Бюджет сохранен";
    }

    public Map<String, Object> getBudgetStatus(String email) {
        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new RuntimeException("User not found"));
        Budget budget = user.getBudget();
        Map<String, Object> result = new HashMap<>();
        result.put("income", budget != null ? budget.getMonthlyIncome() : 0);

        Map<String, Map<String, Double>> categoriesStatus = new HashMap<>();
        if (budget != null && budget.getPercentages() != null) {
            for (Map.Entry<Category, Double> entry : budget.getPercentages().entrySet()) {
                String catName = entry.getKey().toString();
                Double percent = entry.getValue();
                Double limit = budget.getMonthlyIncome() * percent;
                Double spent = budget.getSpent() != null ? budget.getSpent().getOrDefault(entry.getKey(), 0.0) : 0.0;
                Double remaining = limit - spent;

                Map<String, Double> catData = new HashMap<>();
                catData.put("limit", limit);
                catData.put("spent", spent);
                catData.put("remaining", remaining);
                catData.put("percent", percent * 100);
                categoriesStatus.put(catName, catData);
            }
        }
        result.put("categories", categoriesStatus);
        return result;
    }

    public List<String> checkAlerts(String email) {
        List<String> alerts = new ArrayList<>();
        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new RuntimeException("User not found"));
        Budget budget = user.getBudget();
        if (budget == null || budget.getPercentages() == null || budget.getSpent() == null) {
            return alerts;
        }

        for (Map.Entry<Category, Double> entry : budget.getPercentages().entrySet()) {
            Category cat = entry.getKey();
            Double limit = budget.getMonthlyIncome() * entry.getValue();
            Double spent = budget.getSpent().getOrDefault(cat, 0.0);
            if (limit > 0) {
                double percentSpent = spent / limit * 100;
                if (percentSpent >= 100) {
                    alerts.add("Лимит по категории " + cat + " ИСЧЕРПАН!");
                } else if (percentSpent >= 80) {
                    alerts.add("Вы потратили " + String.format("%.0f", percentSpent) + "% бюджета на " + cat);
                }
            }
        }
        return alerts;
    }
}