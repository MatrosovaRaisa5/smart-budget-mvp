package com.tbank.smart_budget_backend.model;

import java.util.HashMap;
import java.util.Map;

import jakarta.persistence.CollectionTable;
import jakarta.persistence.Column;
import jakarta.persistence.ElementCollection;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.MapKeyColumn;
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

    private Double plannedIncome;  // плановый доход (задаётся один раз)

    @ElementCollection
    @CollectionTable(name = "budget_percentages",
            joinColumns = @JoinColumn(name = "budget_id"))
    @MapKeyColumn(name = "category_id")
    @Column(name = "percentage")
    private Map<Long, Double> percentages = new HashMap<>(); // categoryId -> процент

    @ElementCollection
    @CollectionTable(name = "budget_spent",
            joinColumns = @JoinColumn(name = "budget_id"))
    @MapKeyColumn(name = "category_id")
    @Column(name = "spent")
    private Map<Long, Double> spent = new HashMap<>();       // categoryId -> потрачено

    @OneToOne
    @JoinColumn(name = "user_id")
    private User user;
}