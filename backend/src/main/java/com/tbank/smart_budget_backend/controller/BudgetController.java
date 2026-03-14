package com.tbank.smart_budget_backend.controller;

import com.tbank.smart_budget_backend.dto.BudgetSetupDto;
import com.tbank.smart_budget_backend.service.BudgetService;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/budget")
@CrossOrigin(origins = "*")
public class BudgetController {

    private final BudgetService budgetService;

    public BudgetController(BudgetService budgetService) {
        this.budgetService = budgetService;
    }

    @PostMapping("/setup")
    public ResponseEntity<?> setupBudget(@AuthenticationPrincipal UserDetails userDetails,
                                         @Valid @RequestBody BudgetSetupDto dto) {
        String email = userDetails.getUsername();
        String result = budgetService.setupBudget(email, dto.getPlannedIncome(), dto.getPercentages());
        if (result.startsWith("ERROR")) {
            return ResponseEntity.badRequest().body(result);
        }
        return ResponseEntity.ok(result);
    }

    @GetMapping("/status")
    public ResponseEntity<Map<String, Object>> getStatus(@AuthenticationPrincipal UserDetails userDetails) {
        String email = userDetails.getUsername();
        return ResponseEntity.ok(budgetService.getBudgetStatus(email));
    }

    @GetMapping("/alerts")
    public ResponseEntity<List<String>> getAlerts(@AuthenticationPrincipal UserDetails userDetails) {
        String email = userDetails.getUsername();
        return ResponseEntity.ok(budgetService.checkAlerts(email));
    }
}