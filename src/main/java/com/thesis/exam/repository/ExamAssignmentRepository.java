package com.thesis.exam.repository;

import com.thesis.exam.model.ExamAssignment;
import com.thesis.exam.model.Student;
import com.thesis.exam.model.ExamStatus;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface ExamAssignmentRepository extends JpaRepository<ExamAssignment, Long> {
    List<ExamAssignment> findByStudentOrderByAssignedAtDesc(Student student);
    List<ExamAssignment> findByStudentAndStatusOrderByAssignedAtDesc(Student student, ExamStatus status);
}