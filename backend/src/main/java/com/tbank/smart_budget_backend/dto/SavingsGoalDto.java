package com.tbank.smart_budget_backend.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import lombok.Data;

@Data
public class SavingsGoalDto {
    @NotBlank
    private String name;
    @NotNull @Positive
    private Double targetAmount;
    private Double savedAmount; // 0 по умолчанию
}