package com.tbank.smart_budget_backend.dto;

import jakarta.validation.constraints.NotBlank;
import lombok.Data;

@Data
public class ProfileUpdateDto {
    @NotBlank
    private String name;
}