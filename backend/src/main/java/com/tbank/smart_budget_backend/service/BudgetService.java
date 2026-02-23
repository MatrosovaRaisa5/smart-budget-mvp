package com.tbank.smart_budget_backend.service;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.stereotype.Service;

import com.tbank.smart_budget_backend.model.Budget;
import com.tbank.smart_budget_backend.model.Category;
import com.tbank.smart_budget_backend.model.Transaction;
import com.tbank.smart_budget_backend.repository.DataStore;

@Service
public class BudgetService {

    private final DataStore dataStore;

    public BudgetService(DataStore dataStore) {
        this.dataStore = dataStore;
    }

    // Сохраняем настройки бюджета
    public String setupBudget(Long userId, Double income, Map<Category, Double> percentages) {
        // Проверяем, что сумма процентов равна 100% (1.0)
        double totalPercent = percentages.values().stream().mapToDouble(Double::doubleValue).sum();
        if (Math.abs(totalPercent - 1.0) > 0.001) {
            return "ОШИБКА: Сумма процентов должна быть 100%";
        }

        Budget budget = dataStore.getBudget(userId);
        budget.setMonthlyIncome(income);
        budget.setPercentages(percentages);
        // Сбрасываем потраченное, если меняем бюджет
        budget.setSpent(new HashMap<>());
        dataStore.saveBudget(userId, budget);
        return "OK: Бюджет сохранен";
    }

    // Обрабатываем новую транзакцию (имитация из внешнего API)
    public void processTransaction(Transaction transaction) {
        // Простейшая классификация по словам в описании
        String desc = transaction.getDescription().toLowerCase();
        Category cat = Category.OTHER;
        if (desc.contains("продукт") || desc.contains("магазин") || desc.contains("еда")) cat = Category.GROCERIES;
        else if (desc.contains("жкх") || desc.contains("квартплата")) cat = Category.UTILITIES;
        else if (desc.contains("кино") || desc.contains("кафе") || desc.contains("ресторан")) cat = Category.ENTERTAINMENT;
        else if (desc.contains("такси") || desc.contains("метро") || desc.contains("бензин")) cat = Category.TRANSPORT;
        else if (desc.contains("копилка") || desc.contains("вклад")) cat = Category.SAVINGS;

        transaction.setCategory(cat);
        dataStore.addTransaction(transaction);

        // Обновляем потраченные суммы в бюджете
        Budget budget = dataStore.getBudget(1L); // Для пользователя с ID 1
        if (budget.getSpent() != null) {
            Double currentSpent = budget.getSpent().getOrDefault(cat, 0.0);
            budget.getSpent().put(cat, currentSpent + transaction.getAmount());
        } else {
            Map<Category, Double> spentMap = new HashMap<>();
            spentMap.put(cat, transaction.getAmount());
            budget.setSpent(spentMap);
        }
    }

    // Получить статус бюджета (сколько потрачено, сколько осталось)
    public Map<String, Object> getBudgetStatus(Long userId) {
        Budget budget = dataStore.getBudget(userId);
        Map<String, Object> result = new HashMap<>();
        result.put("income", budget.getMonthlyIncome());

        Map<String, Map<String, Double>> categoriesStatus = new HashMap<>();
        if (budget.getPercentages() != null) {
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
                catData.put("percent", percent * 100); // в процентах для удобства
                categoriesStatus.put(catName, catData);
            }
        }
        result.put("categories", categoriesStatus);
        return result;
    }

    // Проверка на превышение лимитов
    public List<String> checkAlerts(Long userId) {
        List<String> alerts = new ArrayList<>();
        Budget budget = dataStore.getBudget(userId);
        if (budget.getPercentages() == null || budget.getSpent() == null) {
            return alerts;
        }

        for (Map.Entry<Category, Double> entry : budget.getPercentages().entrySet()) {
            Category cat = entry.getKey();
            Double limit = budget.getMonthlyIncome() * entry.getValue();
            Double spent = budget.getSpent().getOrDefault(cat, 0.0);
            if (limit > 0) {
                double percentSpent = spent / limit * 100;
                if (percentSpent >= 100) {
                    alerts.add("⚠️ Лимит по категории " + cat + " ИСЧЕРПАН!");
                } else if (percentSpent >= 80) {
                    alerts.add("⚠️ Вы потратили " + String.format("%.0f", percentSpent) + "% бюджета на " + cat);
                }
            }
        }
        return alerts;
    }
}