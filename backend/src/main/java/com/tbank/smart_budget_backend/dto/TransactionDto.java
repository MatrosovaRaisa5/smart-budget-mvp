package com.tbank.smart_budget_backend.dto;

import java.time.LocalDate;

import com.fasterxml.jackson.annotation.JsonFormat;

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

    @NotNull
    private Long categoryId;

    @NotNull
    @JsonFormat(pattern = "dd.MM.yyyy")
    private LocalDate date;
}