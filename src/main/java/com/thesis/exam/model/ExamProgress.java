package com.thesis.exam.model;

import jakarta.persistence.*;
import lombok.Data;
import java.time.LocalDateTime;

/**
 * Stores exam progress for auto-save and resume functionality
 * Implements Event-Driven Architecture pattern
 */
@Entity
@Data
public class ExamProgress {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    @ManyToOne
    @JoinColumn(name = "exam_id")
    private Exam exam;
    
    @ManyToOne
    @JoinColumn(name = "student_id")
    private Student student;
    
    private Integer currentQuestionIndex = 0;
    
    @Column(length = 5000)
    private String answersJson;  // JSON string of answers
    
    private LocalDateTime lastSavedAt;
    
    private LocalDateTime startedAt;
    
    private Integer remainingTimeSeconds;
    
    private Boolean isCompleted = false;
    
    private Boolean isFocusLost = false;  // For focus validation detection
    
    private Integer tabSwitchCount = 0;
}
