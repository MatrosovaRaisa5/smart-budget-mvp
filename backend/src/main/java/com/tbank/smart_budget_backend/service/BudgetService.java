package com.tbank.smart_budget_backend.service;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
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
import com.tbank.smart_budget_backend.repository.CategoryRepository;
import com.tbank.smart_budget_backend.repository.IncomeRepository;
import com.tbank.smart_budget_backend.repository.UserRepository;

@Service
public class BudgetService {

    private final UserRepository userRepository;
    private final BudgetRepository budgetRepository;
    private final CategoryRepository categoryRepository;
    private final IncomeRepository incomeRepository;

    public BudgetService(UserRepository userRepository,
                         BudgetRepository budgetRepository,
                         CategoryRepository categoryRepository,
                         IncomeRepository incomeRepository) {
        this.userRepository = userRepository;
        this.budgetRepository = budgetRepository;
        this.categoryRepository = categoryRepository;
        this.incomeRepository = incomeRepository;
    }

    @Transactional
    public String setupBudget(String email, Double plannedIncome, Map<Long, Double> percentages) {
        double totalPercent = percentages.values().stream().mapToDouble(Double::doubleValue).sum();
        if (Math.abs(totalPercent - 100.0) > 0.001) {
            return "ERROR: Сумма процентов должна быть 100%";
        }

        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new RuntimeException("User not found"));
        Budget budget = user.getBudget();
        if (budget == null) {
            budget = new Budget();
            budget.setUser(user);
            user.setBudget(budget);
        }
        budget.setPlannedIncome(plannedIncome);

        // Преобразуем проценты в доли (для хранения в БД)
        Map<Long, Double> fractions = new HashMap<>();
        for (Map.Entry<Long, Double> entry : percentages.entrySet()) {
            fractions.put(entry.getKey(), entry.getValue() / 100.0);
        }
        budget.setPercentages(fractions);
        budget.setSpent(new HashMap<>());
        budgetRepository.save(budget);
        return "OK: Бюджет сохранён";
    }

    public Map<String, Object> getBudgetStatus(String email) {
        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new RuntimeException("User not found"));
        Budget budget = user.getBudget();

        // Фактические доходы за текущий месяц
        LocalDateTime startOfMonth = LocalDate.now().withDayOfMonth(1).atStartOfDay();
        LocalDateTime endOfMonth = LocalDate.now().withDayOfMonth(LocalDate.now().lengthOfMonth()).atTime(LocalTime.MAX);
        Double actualIncome = incomeRepository.sumByUserAndDateBetween(user, startOfMonth, endOfMonth);

        Map<String, Object> result = new HashMap<>();
        result.put("plannedIncome", budget != null ? budget.getPlannedIncome() : 0.0);
        result.put("actualIncome", actualIncome != null ? actualIncome : 0.0);

        List<Map<String, Object>> categoriesStatus = new ArrayList<>();
        if (budget != null && budget.getPercentages() != null) {
            for (Map.Entry<Long, Double> entry : budget.getPercentages().entrySet()) {
                Long catId = entry.getKey();
                Double fraction = entry.getValue(); // доля от 0 до 1
                Double limit = budget.getPlannedIncome() * fraction;
                Double spent = budget.getSpent() != null ? budget.getSpent().getOrDefault(catId, 0.0) : 0.0;
                Double remaining = limit - spent;

                Category category = categoryRepository.findById(catId).orElse(null);
                String catName = (category != null) ? category.getName() : "Unknown";

                Map<String, Object> catData = new HashMap<>();
                catData.put("categoryId", catId);
                catData.put("categoryName", catName);
                catData.put("limit", limit);
                catData.put("spent", spent);
                catData.put("remaining", remaining);
                catData.put("percent", fraction * 100);          // возвращаем проценты
                catData.put("spentPercent", limit > 0 ? (spent / limit * 100) : 0);
                categoriesStatus.add(catData);
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

        for (Map.Entry<Long, Double> entry : budget.getPercentages().entrySet()) {
            Long catId = entry.getKey();
            Double limit = budget.getPlannedIncome() * entry.getValue();
            Double spent = budget.getSpent().getOrDefault(catId, 0.0);
            if (limit > 0) {
                double percentSpent = spent / limit * 100;
                Category category = categoryRepository.findById(catId).orElse(null);
                String catName = (category != null) ? category.getName() : "Unknown";
                if (percentSpent >= 100) {
                    alerts.add("Лимит по категории " + catName + " ИСЧЕРПАН!");
                } else if (percentSpent >= 80) {
                    alerts.add(String.format("Вы потратили %.0f%% бюджета на %s", percentSpent, catName));
                }
            }
        }
        return alerts;
    }
}