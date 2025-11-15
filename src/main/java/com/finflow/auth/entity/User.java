package com.finflow.auth.entity;

import jakarta.persistence.*;
import lombok.Data;
import java.time.LocalDateTime;

@Entity
@Table(name = "users")
@Data
public class User {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(unique = true)
    private String email;

    private String passwordHash;
    private String name;
    private String phone;
    private String role = "USER";
    private boolean isActive = true;
    private String kycStatus = "pending";

    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
}
