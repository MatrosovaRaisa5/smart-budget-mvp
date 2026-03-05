package com.tbank.smart_budget_backend.service;

import java.util.List;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.tbank.smart_budget_backend.dto.SavingsGoalDto;
import com.tbank.smart_budget_backend.model.SavingsGoal;
import com.tbank.smart_budget_backend.model.User;
import com.tbank.smart_budget_backend.repository.SavingsGoalRepository;
import com.tbank.smart_budget_backend.repository.UserRepository;

@Service
public class SavingsGoalService {
    private final UserRepository userRepository;
    private final SavingsGoalRepository goalRepository;

    public SavingsGoalService(UserRepository userRepository, SavingsGoalRepository goalRepository) {
        this.userRepository = userRepository;
        this.goalRepository = goalRepository;
    }

    @Transactional
    public SavingsGoal createGoal(String email, SavingsGoalDto dto) {
        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new RuntimeException("User not found"));
        SavingsGoal goal = new SavingsGoal();
        goal.setName(dto.getName());
        goal.setTargetAmount(dto.getTargetAmount());
        goal.setSavedAmount(dto.getSavedAmount() != null ? dto.getSavedAmount() : 0.0);
        goal.setUser(user);
        return goalRepository.save(goal);
    }

    public List<SavingsGoal> getGoals(String email) {
        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new RuntimeException("User not found"));
        return goalRepository.findByUserId(user.getId());
    }

    @Transactional
    public SavingsGoal updateGoal(String email, Long goalId, SavingsGoalDto dto) {
        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new RuntimeException("User not found"));
        SavingsGoal goal = goalRepository.findById(goalId)
                .orElseThrow(() -> new RuntimeException("Goal not found"));
        if (!goal.getUser().getId().equals(user.getId())) {
            throw new RuntimeException("Access denied");
        }
        goal.setName(dto.getName());
        goal.setTargetAmount(dto.getTargetAmount());
        if (dto.getSavedAmount() != null) {
            goal.setSavedAmount(dto.getSavedAmount());
        }
        return goalRepository.save(goal);
    }

    @Transactional
    public void deleteGoal(String email, Long goalId) {
        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new RuntimeException("User not found"));
        SavingsGoal goal = goalRepository.findById(goalId)
                .orElseThrow(() -> new RuntimeException("Goal not found"));
        if (!goal.getUser().getId().equals(user.getId())) {
            throw new RuntimeException("Access denied");
        }
        goalRepository.delete(goal);
    }
}

