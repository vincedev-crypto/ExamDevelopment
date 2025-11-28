package com.thesis.exam.controller;

import com.thesis.exam.model.*;
import com.thesis.exam.repository.TeacherRepository;
import com.thesis.exam.service.ExamGenerationService;
import com.thesis.exam.service.StudentService;
import com.thesis.exam.service.SubjectService;
import com.thesis.exam.repository.ExamAssignmentRepository;
import jakarta.servlet.http.HttpSession;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

@Controller
@RequestMapping("/exam")
public class ExamController {

    @Autowired
    private ExamGenerationService examGenerationService;

    @Autowired
    private StudentService studentService;

    @Autowired
    private SubjectService subjectService;

    @Autowired
    private ExamAssignmentRepository examAssignmentRepository;

    @Autowired
    private TeacherRepository teacherRepository;

    @GetMapping("/generate")
    public String showGenerateForm(Model model, HttpSession session) {
        if (!isTeacher(session)) {
            return "redirect:/login";
        }
        model.addAttribute("students", studentService.getAllStudents());
        model.addAttribute("subjects", subjectService.getAllSubjects());
        return "generate-exam";
    }

    @PostMapping("/generate")
    public String generateExam(@RequestParam Long studentId, 
                              @RequestParam Long subjectId, 
                              @RequestParam(defaultValue = "20") int totalQuestions,
                              Model model,
                              HttpSession session) {
        if (!isTeacher(session)) {
            return "redirect:/login";
        }
        Student student = studentService.getStudent(studentId);
        Subject subject = subjectService.getSubject(subjectId);
        
        Exam exam = examGenerationService.generateExam(student, subject, totalQuestions);
        
        model.addAttribute("exam", exam);
        model.addAttribute("students", studentService.getAllStudents());
        return "exam-result";
    }

    @PostMapping("/assign")
    public String assignExam(@RequestParam Long examId,
                            @RequestParam Long studentId,
                            HttpSession session,
                            Model model) {
        if (!isTeacher(session)) {
            return "redirect:/login";
        }
        
        Long teacherId = (Long) session.getAttribute("userId");
        Teacher teacher = teacherRepository.findById(teacherId).orElse(null);
        
        Exam exam = examRepository.findById(examId).orElse(null);
        Student student = studentService.getStudent(studentId);
        
        if (exam != null && student != null && teacher != null) {
            ExamAssignment assignment = new ExamAssignment();
            assignment.setExam(exam);
            assignment.setStudent(student);
            assignment.setAssignedBy(teacher);
            assignment.setStatus(ExamStatus.ASSIGNED);
            
            examAssignmentRepository.save(assignment);
            model.addAttribute("message", "Exam assigned successfully to " + student.getName());
        } else {
            model.addAttribute("error", "Failed to assign exam");
        }
        
        return "redirect:/exam/generate";
    }

    @Autowired
    private com.thesis.exam.repository.ExamRepository examRepository;

    @PostMapping("/submit")
    public String submitExam(@RequestParam Long examId, 
                           @RequestParam(required = false) java.util.List<Long> correctQuestionIds,
                           Model model,
                           HttpSession session) {
        if (!isTeacher(session)) {
            return "redirect:/login";
        }
        
        Exam exam = examRepository.findById(examId).orElseThrow();
        
        // Handle null list if no questions were marked correct
        if (correctQuestionIds == null) {
            correctQuestionIds = java.util.Collections.emptyList();
        }

        // Process the full results: calculate score, update mastery, AND update question stats
        examGenerationService.processExamResult(exam, correctQuestionIds);
        
        model.addAttribute("message", "Exam submitted successfully! Mastery and Question Stats updated.");
        return "redirect:/exam/generate";
    }

    @GetMapping("/take/{assignmentId}")
    public String takeExam(@PathVariable Long assignmentId, HttpSession session, Model model) {
        Long studentId = resolveStudentId(session);
        if (studentId == null) {
            return "redirect:/login";
        }
        
        ExamAssignment assignment = examAssignmentRepository.findById(assignmentId).orElse(null);
        if (assignment == null || !assignment.getStudent().getId().equals(studentId)) {
            return "redirect:/student/results";
        }
        
        if (assignment.getStatus() != ExamStatus.ASSIGNED) {
            return "redirect:/student/results";
        }
        
        // Mark as in progress
        assignment.setStatus(ExamStatus.IN_PROGRESS);
        examAssignmentRepository.save(assignment);
        
        model.addAttribute("assignment", assignment);
        model.addAttribute("exam", assignment.getExam());
        return "take-exam";
    }

    @PostMapping("/submit/{assignmentId}")
    public String submitStudentExam(@PathVariable Long assignmentId,
                                  @RequestParam(required = false) java.util.List<Long> correctQuestionIds,
                                  HttpSession session,
                                  Model model) {
        Long studentId = resolveStudentId(session);
        if (studentId == null) {
            return "redirect:/login";
        }
        
        ExamAssignment assignment = examAssignmentRepository.findById(assignmentId).orElse(null);
        if (assignment == null || !assignment.getStudent().getId().equals(studentId)) {
            return "redirect:/student/results";
        }
        
        // Calculate score
        if (correctQuestionIds == null) {
            correctQuestionIds = java.util.Collections.emptyList();
        }
        
        int totalQuestions = assignment.getExam().getQuestions().size();
        double score = totalQuestions > 0 ? (correctQuestionIds.size() * 100.0) / totalQuestions : 0;
        
        // Update assignment
        assignment.setStatus(ExamStatus.COMPLETED);
        assignment.setScore(score);
        assignment.setCompletedAt(java.time.LocalDateTime.now());
        examAssignmentRepository.save(assignment);
        
        // Process results with algorithm
        examGenerationService.processExamResult(assignment.getExam(), correctQuestionIds);
        
        model.addAttribute("message", "Exam submitted successfully! Score: " + String.format("%.1f", score) + "%");
        return "redirect:/student/results";
    }

    private boolean isTeacher(HttpSession session) {
        if (session == null) {
            return false;
        }
        Object role = session.getAttribute("userRole");
        return role == UserRole.TEACHER;
    }

    private Long resolveStudentId(HttpSession session) {
        Object id = session.getAttribute("userId");
        Object role = session.getAttribute("userRole");
        if (id instanceof Long && role == UserRole.STUDENT) {
            return (Long) id;
        }
        return null;
    }
}
