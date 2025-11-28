package com.thesis.exam.model;

import jakarta.persistence.*;
import lombok.Data;

@Entity
@Data
public class Question {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(length = 1000)
    private String content;

    @Enumerated(EnumType.STRING)
    private Difficulty difficulty; // Teacher assigned difficulty

    @ManyToOne
    @JoinColumn(name = "subject_id")
    private Subject subject;

    // --- IRT with Bloom's Taxonomy ---
    @Enumerated(EnumType.STRING)
    private BloomLevel bloomLevel; // Cognitive complexity level
    
    @Enumerated(EnumType.STRING)
    private QuestionType questionType; // Type of question
    
    // IRT Parameters
    private Double irtDifficulty = 0.0;      // b parameter: item difficulty (-3 to +3)
    private Double irtDiscrimination = 1.0;  // a parameter: how well it discriminates (0 to 2)
    private Double irtGuessing = 0.0;        // c parameter: probability of guessing correctly (0 to 0.5)
    
    // Multimedia support
    private String imageUrl;
    private String audioUrl;
    private String videoUrl;
    
    // For multiple choice
    private String optionA;
    private String optionB;
    private String optionC;
    private String optionD;
    
    private String correctAnswer;

    // --- Statistics for Adaptive Algorithms ---
    
    private int usageCount = 0;
    private int correctCount = 0;
    
    // For Discrimination Index (DI)
    // DI = (Correct by High Mastery) - (Correct by Low Mastery)
    private int usageByHighMastery = 0;
    private int correctByHighMastery = 0;
    
    private int usageByLowMastery = 0;
    private int correctByLowMastery = 0;

    private Double discriminationIndex = 0.0;
}
