package com.tbank.smart_budget_backend.model;

import java.util.Map;

import jakarta.persistence.CollectionTable;
import jakarta.persistence.Column;
import jakarta.persistence.ElementCollection;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.MapKeyEnumerated;
import jakarta.persistence.OneToOne;
import jakarta.persistence.Table;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "budgets")
@Data
@NoArgsConstructor
public class Budget {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private Double monthlyIncome;

    @ElementCollection
    @CollectionTable(name = "budget_percentages", joinColumns = @JoinColumn(name = "budget_id"))
    @MapKeyEnumerated(EnumType.STRING)
    @Column(name = "percentage")
    private Map<Category, Double> percentages;

    @ElementCollection
    @CollectionTable(name = "budget_spent", joinColumns = @JoinColumn(name = "budget_id"))
    @MapKeyEnumerated(EnumType.STRING)
    @Column(name = "spent")
    private Map<Category, Double> spent;

    @OneToOne
    @JoinColumn(name = "user_id")
    private User user;
}