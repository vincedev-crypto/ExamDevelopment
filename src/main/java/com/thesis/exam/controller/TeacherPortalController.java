package com.thesis.exam.controller;

import com.thesis.exam.model.Exam;
import com.thesis.exam.model.Student;
import com.thesis.exam.model.UserRole;
import com.thesis.exam.repository.ExamRepository;
import com.thesis.exam.repository.StudentRepository;
import jakarta.servlet.http.HttpSession;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

import java.util.DoubleSummaryStatistics;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Controller
@RequestMapping("/teacher")
public class TeacherPortalController {

    @Autowired
    private ExamRepository examRepository;

    @Autowired
    private StudentRepository studentRepository;

    @GetMapping("/analytics")
    public String viewAnalytics(HttpSession session, Model model) {
        if (!isTeacher(session)) {
            return "redirect:/login";
        }

        addCommonAttributes(session, model);
        List<Exam> exams = examRepository.findAll();
        model.addAttribute("totalExams", exams.size());
        model.addAttribute("averageScore", exams.stream()
                .filter(exam -> exam.getScore() != null)
                .mapToDouble(Exam::getScore)
                .average()
                .orElse(0));

        Map<String, DoubleSummaryStatistics> subjectStats = exams.stream()
                .filter(exam -> exam.getSubject() != null && exam.getScore() != null)
                .collect(Collectors.groupingBy(exam -> exam.getSubject().getName(),
                        Collectors.summarizingDouble(Exam::getScore)));
        model.addAttribute("subjectStats", subjectStats);
        model.addAttribute("totalStudents", studentRepository.count());
        return "teacher-analytics";
    }

    @GetMapping("/students")
    public String manageStudents(HttpSession session, Model model) {
        if (!isTeacher(session)) {
            return "redirect:/login";
        }

        addCommonAttributes(session, model);
        List<Student> students = studentRepository.findAll();
        Map<Long, Long> examsPerStudent = examRepository.findAll().stream()
                .filter(exam -> exam.getStudent() != null)
                .collect(Collectors.groupingBy(exam -> exam.getStudent().getId(), Collectors.counting()));
        model.addAttribute("students", students);
        model.addAttribute("examsPerStudent", examsPerStudent);
        return "teacher-students";
    }

    private boolean isTeacher(HttpSession session) {
        Object role = session.getAttribute("userRole");
        return role == UserRole.TEACHER;
    }

    private void addCommonAttributes(HttpSession session, Model model) {
        model.addAttribute("userName", session.getAttribute("userName"));
        model.addAttribute("department", session.getAttribute("department"));
    }
}
