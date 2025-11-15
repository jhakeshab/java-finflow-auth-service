package com.finflow.auth.service;

import com.finflow.auth.entity.User;
import com.finflow.auth.repository.UserRepository;
import com.finflow.auth.util.JwtUtil;
import lombok.RequiredArgsConstructor;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import java.time.LocalDateTime;
import java.util.Map;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class AuthService {
    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtUtil jwtUtil;
    private final RedisTemplate<String, Object> redisTemplate;

    public User register(User user) {
        user.setPasswordHash(passwordEncoder.encode(user.getPasswordHash()));
        user.setCreatedAt(LocalDateTime.now());
        user.setUpdatedAt(LocalDateTime.now());
        return userRepository.save(user);
    }

    public String login(String email, String password) {
        Optional<User> userOpt = userRepository.findByEmail(email);
        if (userOpt.isPresent() && passwordEncoder.matches(password, userOpt.get().getPasswordHash())) {
            User user = userOpt.get();
            String token = jwtUtil.generateToken(user);
            redisTemplate.opsForValue().set("session:" + user.getId() + ":" + token, "active");
            return token;
        }
        throw new RuntimeException("Invalid credentials");
    }

    public void logout(String token, Long userId) {
        redisTemplate.opsForValue().set("blacklist:" + token, "true");
    }

    public User getUser(Long id) {
        return userRepository.findById(id).orElse(null);
    }

    public User updateUser(Long id, Map<String, Object> updates) {
        return userRepository.findById(id).map(user -> {
            if (updates.containsKey("kycStatus")) {
                user.setKycStatus((String) updates.get("kycStatus"));
            }
            user.setUpdatedAt(LocalDateTime.now());
            return userRepository.save(user);
        }).orElse(null);
    }

    public boolean verifyToken(String token) {
        if (Boolean.TRUE.equals(redisTemplate.opsForValue().get("blacklist:" + token))) {
            return false;
        }
        return jwtUtil.validateToken(token);
    }
}
