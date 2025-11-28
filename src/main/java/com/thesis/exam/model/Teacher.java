package com.thesis.exam.model;

import jakarta.persistence.*;
import lombok.Data;

@Entity
@Data
public class Teacher {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String name;
    private String email;
    private String password;
    
    @Enumerated(EnumType.STRING)
    private UserRole role = UserRole.TEACHER;
    
    private Boolean isActive = true;
    
    private String department;
    
    // Email verification
    private Boolean emailVerified = false;
    private String verificationToken;
    private java.time.LocalDateTime tokenExpiryDate;
}
