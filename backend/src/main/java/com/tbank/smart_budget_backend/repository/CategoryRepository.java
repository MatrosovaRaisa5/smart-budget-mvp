package com.tbank.smart_budget_backend.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import com.tbank.smart_budget_backend.model.Category;
import com.tbank.smart_budget_backend.model.User;

public interface CategoryRepository extends JpaRepository<Category, Long> {

    @Query("SELECT c FROM Category c WHERE c.user IS NULL OR c.user = :user")
    List<Category> findAllAvailableForUser(@Param("user") User user);

    @Query("SELECT COUNT(c) > 0 FROM Category c WHERE c.name = :name AND (c.user IS NULL OR c.user = :user)")
    boolean existsByNameAndUser(@Param("name") String name, @Param("user") User user);
}