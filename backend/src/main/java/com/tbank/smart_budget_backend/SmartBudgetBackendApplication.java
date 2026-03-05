package com.tbank.smart_budget_backend;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

import io.github.cdimascio.dotenv.Dotenv;

@SpringBootApplication
public class SmartBudgetBackendApplication {

    public static void main(String[] args) {
        Dotenv dotenv = Dotenv.configure()
                .directory("./")
                .filename(".env")
                .load();

        dotenv.entries().forEach(entry -> {
            System.setProperty(entry.getKey(), entry.getValue());
        });

        String dbUrl = System.getProperty("DB_URL");
        if (dbUrl == null) {
            throw new RuntimeException("DB_URL не загружен из .env");
        }

        SpringApplication.run(SmartBudgetBackendApplication.class, args);
    }
}