package com.tbank.smart_budget_backend.dto;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import lombok.Data;

@Data
public class ContributionDto {
    @NotNull @Positive
    private Double amount;
}