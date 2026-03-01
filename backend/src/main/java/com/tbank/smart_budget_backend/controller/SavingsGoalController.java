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
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.tbank.smart_budget_backend.dto.SavingsGoalDto;
import com.tbank.smart_budget_backend.model.SavingsGoal;
import com.tbank.smart_budget_backend.service.SavingsGoalService;

import jakarta.validation.Valid;

@RestController
@RequestMapping("/api/goals")
@CrossOrigin(origins = "*")
public class SavingsGoalController {
    private final SavingsGoalService goalService;

    public SavingsGoalController(SavingsGoalService goalService) {
        this.goalService = goalService;
    }

    @PostMapping
    public ResponseEntity<SavingsGoal> createGoal(@AuthenticationPrincipal UserDetails userDetails,
                                                  @Valid @RequestBody SavingsGoalDto dto) {
        String email = userDetails.getUsername();
        return ResponseEntity.ok(goalService.createGoal(email, dto));
    }

    @GetMapping
    public ResponseEntity<List<SavingsGoal>> getGoals(@AuthenticationPrincipal UserDetails userDetails) {
        String email = userDetails.getUsername();
        return ResponseEntity.ok(goalService.getGoals(email));
    }

    @PutMapping("/{goalId}")
    public ResponseEntity<SavingsGoal> updateGoal(@AuthenticationPrincipal UserDetails userDetails,
                                                  @PathVariable Long goalId,
                                                  @Valid @RequestBody SavingsGoalDto dto) {
        String email = userDetails.getUsername();
        return ResponseEntity.ok(goalService.updateGoal(email, goalId, dto));
    }

    @DeleteMapping("/{goalId}")
    public ResponseEntity<?> deleteGoal(@AuthenticationPrincipal UserDetails userDetails,
                                        @PathVariable Long goalId) {
        String email = userDetails.getUsername();
        goalService.deleteGoal(email, goalId);
        return ResponseEntity.ok("Goal deleted");
    }
}