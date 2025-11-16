// UserService.java
package com.finflow.auth.service;

import com.finflow.auth.entity.User;
import com.finflow.auth.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Transactional
public class UserService {
    
    private final UserRepository userRepository;

    @Cacheable(value = "users", key = "#userId")
    public User getUserById(Long userId) {
        return userRepository.findById(userId).orElse(null);
    }

    public void updateKYCStatus(Long userId, User.KYCStatus status) {
        User user = userRepository.findById(userId).orElse(null);
        if (user != null) {
            user.setKycStatus(status);
            userRepository.save(user);
        }
    }
}