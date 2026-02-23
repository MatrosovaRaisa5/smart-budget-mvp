package com.tbank.smart_budget_backend.model;


import java.time.LocalDateTime;

import lombok.Data;

@Data
public class Transaction {
    private Long id;
    private Double amount;
    private String description;
    private LocalDateTime date;
    private Category category;
}