package com.thesis.exam.model;

import jakarta.persistence.*;
import lombok.Data;
import org.hibernate.annotations.CreationTimestamp;

import java.time.LocalDateTime;

@Entity
@Data
public class ExamAssignment {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    @JoinColumn(name = "exam_id")
    private Exam exam;

    @ManyToOne
    @JoinColumn(name = "student_id")
    private Student student;

    @ManyToOne
    @JoinColumn(name = "assigned_by_teacher_id")
    private Teacher assignedBy;

    @Enumerated(EnumType.STRING)
    private ExamStatus status = ExamStatus.ASSIGNED;

    @CreationTimestamp
    private LocalDateTime assignedAt;

    private LocalDateTime completedAt;

    private Double score;
}