// src/main/java/com/finflow/javafinflowauthservice/controller/AuthController.java
package com.finflow.javafinflowauthservice.controller;

import com.fasterxml.jackson.databind.JsonNode;
import com.finflow.javafinflowauthservice.service.AuthService;
import com.finflow.javafinflowauthservice.util.JwtUtil;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequestMapping("/api/auth")
public class AuthController {

    private final AuthService authService;
    private final JwtUtil jwtUtil;

    public AuthController(AuthService authService, JwtUtil jwtUtil) {
        this.authService = authService;
        this.jwtUtil = jwtUtil;
    }

    @PostMapping("/register")
    public ResponseEntity<Map<String, Object>> register(@RequestBody JsonNode body) {
        // Validate required fields
        if (!body.has("email") || !body.has("password")) {
            return ResponseEntity.badRequest().body(Map.of("error", "Missing required fields: email, password"));
        }

        String email = body.get("email").asText();
        String password = body.get("password").asText();
        String name = body.has("name") ? body.get("name").asText() : "";
        String phone = body.has("phone") ? body.get("phone").asText() : "";

        if (email == null || email.trim().isEmpty() || 
            password == null || password.trim().isEmpty()) {
            return ResponseEntity.badRequest().body(Map.of("error", "Email and password cannot be empty"));
        }

        try {
            Map<String, Object> response = authService.register(email, password, name, phone);
            return ResponseEntity.ok(response);
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest().body(Map.of("error", e.getMessage()));
        }
    }

    @PostMapping("/login")
    public Map<String, Object> login(@RequestParam String username, @RequestParam String password) {
        return authService.login(username, password);
    }

    // Internal endpoint for KYC Service (no auth required for internal calls)
    @PostMapping("/user/{id}/kyc")
    public Map<String, String> updateKycStatus(@PathVariable Long id, 
                                            @RequestBody Map<String, String> payload) {
        String status = payload.get("status");
        authService.updateKycStatus(id, status);
        return Map.of("status", "KYC status updated for user " + id);
    }

    @GetMapping("/user/{id}")
    public Map<String, Object> getUser(@PathVariable Long id) {
        var user = authService.getUserById(id);
        return Map.of(
            "id", user.getId(),
            "email", user.getEmail(),
            "name", user.getName(),
            "phone", user.getPhone(),
            "role", user.getRole(),
            "is_active", user.getIsActive(),
            "kyc_status", user.getKycStatus()
        );
    }

    @GetMapping("/verify-token")
    public Map<String, Object> verifyToken(@RequestHeader("Authorization") String authHeader) {
        String token = authHeader.replace("Bearer ", "");
        try {
            var claims = jwtUtil.parseToken(token);
            return Map.of(
                "valid", true,
                "user_id", claims.get("user_id"),
                "kyc_status", claims.get("kyc_status"),
                "email", claims.get("email")
            );
        } catch (Exception e) {
            return Map.of("valid", false);
        }
    }

    @GetMapping("/health")
    public Map<String, String> health() {
        return Map.of("status", "up");
    }
}