package com.thesis.exam.controller;

import com.thesis.exam.model.*;
import com.thesis.exam.repository.ExamRepository;
import com.thesis.exam.repository.StudentRepository;
import com.thesis.exam.repository.StudentSubjectMasteryRepository;
import com.thesis.exam.repository.ExamAssignmentRepository;
import jakarta.servlet.http.HttpSession;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

import java.util.Collections;
import java.util.List;
import java.util.OptionalDouble;

@Controller
@RequestMapping("/student")
public class StudentPortalController {

    @Autowired
    private ExamRepository examRepository;

    @Autowired
    private StudentRepository studentRepository;

    @Autowired
    private StudentSubjectMasteryRepository masteryRepository;

    @Autowired
    private ExamAssignmentRepository examAssignmentRepository;

    @GetMapping("/dashboard")
    public String viewDashboard(HttpSession session, Model model) {
        Long studentId = resolveStudentId(session);
        if (studentId == null) {
            return "redirect:/login";
        }

        addCommonAttributes(session, model);
        Student student = studentRepository.findById(studentId).orElse(null);
        if (student != null) {
            // Get pending/assigned exams for the student to take
            List<ExamAssignment> assignments = examAssignmentRepository.findByStudentOrderByAssignedAtDesc(student);
            model.addAttribute("assignments", assignments);
        } else {
            model.addAttribute("assignments", Collections.emptyList());
        }
        return "student-dashboard";
    }

    @GetMapping("/results")
    public String viewResults(HttpSession session, Model model) {
        Long studentId = resolveStudentId(session);
        if (studentId == null) {
            return "redirect:/login";
        }

        addCommonAttributes(session, model);
        Student student = studentRepository.findById(studentId).orElse(null);
        if (student != null) {
            List<ExamAssignment> assignments = examAssignmentRepository.findByStudentOrderByAssignedAtDesc(student);
            model.addAttribute("assignments", assignments);
        } else {
            model.addAttribute("assignments", Collections.emptyList());
        }
        return "student-results";
    }

    @GetMapping("/analytics")
    public String viewAnalytics(HttpSession session, Model model) {
        Long studentId = resolveStudentId(session);
        if (studentId == null) {
            return "redirect:/login";
        }

        addCommonAttributes(session, model);
        List<Exam> exams = examRepository.findByStudentIdOrderByCreatedAtDesc(studentId);
        model.addAttribute("exams", exams);

        OptionalDouble averageScore = exams.stream()
                .filter(exam -> exam.getScore() != null)
                .mapToDouble(Exam::getScore)
                .average();

        double bestScore = exams.stream()
                .filter(exam -> exam.getScore() != null)
                .mapToDouble(Exam::getScore)
                .max()
                .orElse(0);

        model.addAttribute("averageScore", averageScore.orElse(0));
        model.addAttribute("bestScore", bestScore);
        model.addAttribute("completedExams", exams.stream().filter(exam -> exam.getScore() != null).count());

        Student student = studentRepository.findById(studentId).orElse(null);
        List<StudentSubjectMastery> mastery = student == null
                ? Collections.emptyList()
                : masteryRepository.findByStudent(student);
        model.addAttribute("mastery", mastery);

        return "student-analytics";
    }

    @GetMapping("/profile")
    public String viewProfile(HttpSession session, Model model) {
        Long studentId = resolveStudentId(session);
        if (studentId == null) {
            return "redirect:/login";
        }

        addCommonAttributes(session, model);
        studentRepository.findById(studentId).ifPresent(student -> model.addAttribute("student", student));
        return "student-profile";
    }

    private Long resolveStudentId(HttpSession session) {
        Object id = session.getAttribute("userId");
        Object role = session.getAttribute("userRole");
        if (id instanceof Long && role == UserRole.STUDENT) {
            return (Long) id;
        }
        return null;
    }

    private void addCommonAttributes(HttpSession session, Model model) {
        model.addAttribute("userName", session.getAttribute("userName"));
    }
}
