package com.tbank.smart_budget_backend.model;

import java.util.Map;

import lombok.Data;

@Data
public class Budget {
    private Double monthlyIncome; // Месячный доход
    private Map<Category, Double> percentages; // Проценты по категориям (0.25 = 25%)
    private Map<Category, Double> spent; // Сколько уже потрачено
}