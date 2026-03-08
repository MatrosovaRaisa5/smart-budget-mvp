package com.tbank.smart_budget_backend.config;

import java.util.Arrays;
import java.util.List;

import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

import com.tbank.smart_budget_backend.model.Category;
import com.tbank.smart_budget_backend.repository.CategoryRepository;

@Component
public class DataInitializer implements CommandLineRunner {

    private final CategoryRepository categoryRepository;

    public DataInitializer(CategoryRepository categoryRepository) {
        this.categoryRepository = categoryRepository;
    }

    @Override
    public void run(String... args) {
        List<String> defaultCategories = Arrays.asList(
                "Продукты", "Одежда", "Семья", "Образование/Саморазвитие",
                "Подарки", "Здоровье", "Транспорт", "ЖКХ", "Развлечения"
        );

        for (String name : defaultCategories) {
            if (categoryRepository.findAllAvailableForUser(null).stream()
                    .noneMatch(c -> c.getName().equals(name) && c.getUser() == null)) {
                Category category = new Category();
                category.setName(name);
                category.setUser(null); // глобальная
                categoryRepository.save(category);
            }
        }
    }
}