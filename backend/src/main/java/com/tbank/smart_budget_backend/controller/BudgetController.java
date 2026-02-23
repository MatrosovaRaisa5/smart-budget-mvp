package com.tbank.smart_budget_backend.controller;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;

import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.tbank.smart_budget_backend.model.Category;
import com.tbank.smart_budget_backend.model.Transaction;
import com.tbank.smart_budget_backend.service.BudgetService;

@RestController
@RequestMapping("/api/budget")
@CrossOrigin(origins = "*") // Разрешаем запросы с мобильного приложения
public class BudgetController {

    private final BudgetService budgetService;

    public BudgetController(BudgetService budgetService) {
        this.budgetService = budgetService;
    }

    // Эндпоинт для настройки бюджета
    @PostMapping("/setup")
    public String setupBudget(@RequestBody Map<String, Object> payload) {
        Double income = Double.valueOf(payload.get("income").toString());
        Map<Category, Double> percentages = (Map<Category, Double>) payload.get("percentages");
        // В реальном проекте User ID брался бы из сессии, пока захардкодим 1L
        return budgetService.setupBudget(1L, income, percentages);
    }

    // Эндпоинт для получения статуса
    @GetMapping("/status")
    public Map<String, Object> getStatus() {
        return budgetService.getBudgetStatus(1L);
    }

    // Эндпоинт для добавления транзакции (имитация внешнего API)
    @PostMapping("/transaction")
    public String addTransaction(@RequestBody Map<String, Object> payload) {
        Transaction t = new Transaction();
        t.setAmount(Double.valueOf(payload.get("amount").toString()));
        t.setDescription(payload.get("description").toString());
        t.setDate(LocalDateTime.now());
        budgetService.processTransaction(t);
        return "OK";
    }

    // Эндпоинт для получения алертов
    @GetMapping("/alerts")
    public List<String> getAlerts() {
        return budgetService.checkAlerts(1L);
    }
}