package com.thesis.exam.model;

import jakarta.persistence.*;
import lombok.Data;

@Entity
@Data
public class StudentSubjectMastery {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    @JoinColumn(name = "student_id")
    private Student student;

    @ManyToOne
    @JoinColumn(name = "subject_id")
    private Subject subject;

    // A score from 0.0 to 1.0 (or 0 to 100) representing mastery
    // 0.0 - 0.3: Beginner (Needs Easy)
    // 0.3 - 0.7: Intermediate (Needs Medium)
    // 0.7 - 1.0: Advanced (Needs Hard)
    private Double masteryLevel; 
}
