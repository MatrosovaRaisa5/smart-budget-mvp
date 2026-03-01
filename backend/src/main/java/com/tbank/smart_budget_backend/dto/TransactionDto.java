package com.tbank.smart_budget_backend.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import lombok.Data;

@Data
public class TransactionDto {
    @NotNull @Positive
    private Double amount;
    @NotBlank
    private String description;
}