package com.finflow.auth.controller;

import com.finflow.auth.entity.User;
import com.finflow.auth.service.AuthService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequestMapping("/api/auth")
@RequiredArgsConstructor
public class AuthController {
    private final AuthService authService;

    @PostMapping("/register")
    public ResponseEntity<User> register(@RequestBody User user) {
        return ResponseEntity.ok(authService.register(user));
    }

    @PostMapping("/login")
    public ResponseEntity<String> login(@RequestParam String email, @RequestParam String password) {
        return ResponseEntity.ok(authService.login(email, password));
    }

    @PostMapping("/logout")
    public ResponseEntity<String> logout(@RequestHeader("Authorization") String token, @RequestParam Long userId) {
        String jwtToken = token.replace("Bearer ", "");
        authService.logout(jwtToken, userId);
        return ResponseEntity.ok("Logged out");
    }

    @GetMapping("/verify-token")
    public ResponseEntity<Boolean> verifyToken(@RequestHeader("Authorization") String token) {
        String jwtToken = token.replace("Bearer ", "");
        return ResponseEntity.ok(authService.verifyToken(jwtToken));
    }

    @GetMapping("/user/{userId}")
    public ResponseEntity<User> getUser(@PathVariable Long userId) {
        return ResponseEntity.ok(authService.getUser(userId));
    }

    @PutMapping("/user/{userId}")
    public ResponseEntity<User> updateUser(@PathVariable Long userId, @RequestBody Map<String, Object> updates) {
        return ResponseEntity.ok(authService.updateUser(userId, updates));
    }
}
