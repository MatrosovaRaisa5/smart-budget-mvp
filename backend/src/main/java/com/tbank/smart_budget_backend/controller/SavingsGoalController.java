package com.tbank.smart_budget_backend.controller;

import com.tbank.smart_budget_backend.dto.ContributionDto;
import com.tbank.smart_budget_backend.dto.SavingsGoalDto;
import com.tbank.smart_budget_backend.model.SavingsGoal;
import com.tbank.smart_budget_backend.service.SavingsGoalService;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;

import java.util.List;

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

    @PostMapping("/{goalId}/contributions")
    public ResponseEntity<SavingsGoal> addContribution(@AuthenticationPrincipal UserDetails userDetails,
                                                       @PathVariable Long goalId,
                                                       @Valid @RequestBody ContributionDto dto) {
        String email = userDetails.getUsername();
        SavingsGoal updatedGoal = goalService.addContribution(email, goalId, dto.getAmount());
        return ResponseEntity.ok(updatedGoal);
    }
}