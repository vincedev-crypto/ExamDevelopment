package com.thesis.exam.repository;

import com.thesis.exam.model.Exam;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface ExamRepository extends JpaRepository<Exam, Long> {
	List<Exam> findByStudentIdOrderByCreatedAtDesc(Long studentId);
}
