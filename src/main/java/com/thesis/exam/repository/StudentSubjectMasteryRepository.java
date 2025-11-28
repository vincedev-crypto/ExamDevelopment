package com.thesis.exam.repository;

import com.thesis.exam.model.Student;
import com.thesis.exam.model.Subject;
import com.thesis.exam.model.StudentSubjectMastery;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface StudentSubjectMasteryRepository extends JpaRepository<StudentSubjectMastery, Long> {
    Optional<StudentSubjectMastery> findByStudentAndSubject(Student student, Subject subject);
    List<StudentSubjectMastery> findByStudent(Student student);
}
