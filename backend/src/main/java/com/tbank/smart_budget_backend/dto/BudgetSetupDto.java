package com.tbank.smart_budget_backend.dto;

import java.util.Map;

import com.tbank.smart_budget_backend.model.Category;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import lombok.Data;

@Data
public class BudgetSetupDto {
    @NotNull @Positive
    private Double income;
    @NotNull
    private Map<Category, Double> percentages;
}