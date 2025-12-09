// src/main/java/com/finflow/javafinflowauthservice/repository/UserRepository.java
package com.finflow.javafinflowauthservice.repository;

import com.finflow.javafinflowauthservice.model.User;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.Optional;

public interface UserRepository extends JpaRepository<User, Long> {
    Optional<User> findByEmailAndKycVerified(String email, Boolean kycVerified);

}