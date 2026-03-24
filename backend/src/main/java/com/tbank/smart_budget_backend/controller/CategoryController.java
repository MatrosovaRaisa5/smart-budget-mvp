package com.tbank.smart_budget_backend.controller;

import com.tbank.smart_budget_backend.dto.CategoryDto;
import com.tbank.smart_budget_backend.model.Category;
import com.tbank.smart_budget_backend.model.User;
import com.tbank.smart_budget_backend.repository.CategoryRepository;
import com.tbank.smart_budget_backend.repository.UserRepository;
import jakarta.validation.constraints.NotBlank;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/categories")
@CrossOrigin(origins = "*")
public class CategoryController {

    private final CategoryRepository categoryRepository;
    private final UserRepository userRepository;

    public CategoryController(CategoryRepository categoryRepository, UserRepository userRepository) {
        this.categoryRepository = categoryRepository;
        this.userRepository = userRepository;
    }

    @GetMapping
    public ResponseEntity<List<CategoryDto>> getAvailableCategories(@AuthenticationPrincipal UserDetails userDetails) {
        User user = userRepository.findByEmail(userDetails.getUsername())
                .orElseThrow(() -> new RuntimeException("User not found"));
        List<Category> categories = categoryRepository.findAllAvailableForUser(user);
        List<CategoryDto> dtos = categories.stream()
                .map(c -> new CategoryDto(c.getId(), c.getName(), c.getUser() == null))
                .collect(Collectors.toList());
        return ResponseEntity.ok(dtos);
    }

    @PostMapping
    public ResponseEntity<CategoryDto> createCategory(@AuthenticationPrincipal UserDetails userDetails,
                                                       @RequestParam @NotBlank String name) {
        User user = userRepository.findByEmail(userDetails.getUsername())
                .orElseThrow(() -> new RuntimeException("User not found"));
        if (categoryRepository.existsByNameAndUser(name, user)) {
            return ResponseEntity.badRequest().build();
        }
        Category category = new Category();
        category.setName(name);
        category.setUser(user);
        category = categoryRepository.save(category);
        CategoryDto dto = new CategoryDto(category.getId(), category.getName(), false);
        return ResponseEntity.ok(dto);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<?> deleteCategory(@AuthenticationPrincipal UserDetails userDetails,
                                            @PathVariable Long id) {
        User user = userRepository.findByEmail(userDetails.getUsername())
                .orElseThrow(() -> new RuntimeException("User not found"));
        Category category = categoryRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Category not found"));
        if (category.getUser() == null || !category.getUser().getId().equals(user.getId())) {
            return ResponseEntity.status(403).build();
        }
        categoryRepository.delete(category);
        return ResponseEntity.ok("Category deleted");
    }
}