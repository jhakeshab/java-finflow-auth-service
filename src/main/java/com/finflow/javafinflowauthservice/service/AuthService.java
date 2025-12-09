// src/main/java/com/finflow/javafinflowauthservice/service/AuthService.java
package com.finflow.javafinflowauthservice.service;

import com.finflow.javafinflowauthservice.model.User;
import com.finflow.javafinflowauthservice.repository.UserRepository;
import com.finflow.javafinflowauthservice.util.JwtUtil;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Map;
import java.lang.Boolean

@Service
public class AuthService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtUtil jwtUtil;
    private final KafkaTemplate<String, String> kafkaTemplate;

    public AuthService(UserRepository userRepository, 
                       PasswordEncoder passwordEncoder, 
                       JwtUtil jwtUtil, KafkaTemplate<String, String> kafkaTemplate) {
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
        this.jwtUtil = jwtUtil;
        this.kafkaTemplate = kafkaTemplate;
    }

    @Transactional
    public Map<String, Object> register(String email, String password, String name, String phone) {
        if (userRepository.findByEmail(email).isPresent()) {
            throw new RuntimeException("Email already exists");
        }

        User user = new User();
        user.setEmail(email);
        user.setPasswordHash(passwordEncoder.encode(password));
        user.setName(name);
        user.setPhone(phone);
        /*user.setKycStatus("pending"); // ← NOT verified!*/
        user.setKycVerified(false);
        user.setCreatedAt(LocalDateTime.now());
        user.setUpdatedAt(LocalDateTime.now());

        User saved = userRepository.save(user);

        String event = String.format("{\"user_id\":%d,\"email\":\"%s\"}", 
        saved.getId(), saved.getEmail());
        kafkaTemplate.send("user.created", event);

        String token = jwtUtil.generateToken(saved.getId(), saved.getKycStatus(), email);

        Map<String, Object> response = new HashMap<>();
        response.put("token", token);
        response.put("user_id", saved.getId());
        return response;
    }

    public Map<String, Object> login(String email, String password) {
        User user = userRepository.findByEmail(email)
            .orElseThrow(() -> new RuntimeException("Invalid credentials"));

        if (!passwordEncoder.matches(password, user.getPasswordHash())) {
            throw new RuntimeException("Invalid credentials");
        }

        String token = jwtUtil.generateToken(user.getId(), user.getKycVerified(), email);

        Map<String, Object> response = new HashMap<>();
        response.put("token", token);
        response.put("user_id", user.getId());
        return response;
    }

    public User getUserById(Long id) {
        return userRepository.findById(id)
            .orElseThrow(() -> new RuntimeException("User not found"));
    }

    @Transactional
    public void updateKycStatus(Long userId, String status) {
        User user = userRepository.findById(userId)
            .orElseThrow(() -> new RuntimeException("User not found"));
        user.setKycStatus(status);
        user.setUpdatedAt(LocalDateTime.now());
        userRepository.save(user);
    }
}