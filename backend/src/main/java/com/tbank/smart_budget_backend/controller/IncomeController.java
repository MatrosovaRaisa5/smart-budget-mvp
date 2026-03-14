package com.tbank.smart_budget_backend.controller;

import java.util.List;

import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.tbank.smart_budget_backend.dto.IncomeDto;
import com.tbank.smart_budget_backend.model.Income;
import com.tbank.smart_budget_backend.model.User;
import com.tbank.smart_budget_backend.repository.IncomeRepository;
import com.tbank.smart_budget_backend.repository.UserRepository;

import jakarta.validation.Valid;

@RestController
@RequestMapping("/api/incomes")
@CrossOrigin(origins = "*")
public class IncomeController {

    private final IncomeRepository incomeRepository;
    private final UserRepository userRepository;

    public IncomeController(IncomeRepository incomeRepository, UserRepository userRepository) {
        this.incomeRepository = incomeRepository;
        this.userRepository = userRepository;
    }

    @PostMapping
    public ResponseEntity<Income> addIncome(@AuthenticationPrincipal UserDetails userDetails,
                                            @Valid @RequestBody IncomeDto dto) {
        User user = userRepository.findByEmail(userDetails.getUsername())
                .orElseThrow(() -> new RuntimeException("User not found"));
        Income income = new Income();
        income.setSource(dto.getSource());
        income.setAmount(dto.getAmount());
        income.setDate(dto.getDate().atStartOfDay());
        income.setUser(user);
        income = incomeRepository.save(income);
        return ResponseEntity.ok(income);
    }

    @GetMapping
    public ResponseEntity<List<Income>> getIncomes(@AuthenticationPrincipal UserDetails userDetails) {
        User user = userRepository.findByEmail(userDetails.getUsername())
                .orElseThrow(() -> new RuntimeException("User not found"));
        List<Income> incomes = incomeRepository.findByUserIdOrderByDateDesc(user.getId());
        return ResponseEntity.ok(incomes);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<?> deleteIncome(@AuthenticationPrincipal UserDetails userDetails,
                                          @PathVariable Long id) {
        User user = userRepository.findByEmail(userDetails.getUsername())
                .orElseThrow(() -> new RuntimeException("User not found"));
        Income income = incomeRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Income not found"));
        if (!income.getUser().getId().equals(user.getId())) {
            return ResponseEntity.status(403).build();
        }
        incomeRepository.delete(income);
        return ResponseEntity.ok("Income deleted");
    }
}