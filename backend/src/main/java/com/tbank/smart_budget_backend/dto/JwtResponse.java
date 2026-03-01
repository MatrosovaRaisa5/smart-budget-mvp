package com.tbank.smart_budget_backend.dto;

import lombok.Data;

@Data
public class JwtResponse {
    private final String token;
    private final String type = "Bearer";
    private final Long id;
    private final String email;
    private final String name;

    public JwtResponse(String token, Long id, String email, String name) {
        this.token = token;
        this.id = id;
        this.email = email;
        this.name = name;
    }
}