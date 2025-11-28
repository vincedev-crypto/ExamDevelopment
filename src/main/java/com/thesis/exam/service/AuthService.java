package com.thesis.exam.service;

import com.thesis.exam.model.Student;
import com.thesis.exam.model.Teacher;
import com.thesis.exam.model.UserRole;
import com.thesis.exam.repository.StudentRepository;
import com.thesis.exam.repository.TeacherRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

@Service
public class AuthService {
    
    @Autowired
    private StudentRepository studentRepository;
    
    @Autowired
    private TeacherRepository teacherRepository;
    
    @Autowired
    private EmailService emailService;
    
    /**
     * Unified login for both students and teachers
     * Returns a map with user info and role
     */
    public Map<String, Object> login(String email, String password) {
        Map<String, Object> result = new HashMap<>();
        
        // Try to find student first
        Student student = studentRepository.findByEmail(email).orElse(null);
        if (student != null && student.getPassword().equals(password)) {
            if (!student.getEmailVerified()) {
                result.put("success", false);
                result.put("message", "Please verify your email before logging in. Check your inbox for verification link.");
                return result;
            }
            if (!student.getIsActive()) {
                result.put("success", false);
                result.put("message", "Your account has been deactivated. Please contact support.");
                return result;
            }
            result.put("success", true);
            result.put("userId", student.getId());
            result.put("name", student.getName());
            result.put("email", student.getEmail());
            result.put("role", UserRole.STUDENT);
            return result;
        }
        
        // Try to find teacher
        Teacher teacher = teacherRepository.findByEmail(email).orElse(null);
        if (teacher != null && teacher.getPassword().equals(password)) {
            if (!teacher.getEmailVerified()) {
                result.put("success", false);
                result.put("message", "Please verify your email before logging in. Check your inbox for verification link.");
                return result;
            }
            if (!teacher.getIsActive()) {
                result.put("success", false);
                result.put("message", "Your account has been deactivated. Please contact support.");
                return result;
            }
            result.put("success", true);
            result.put("userId", teacher.getId());
            result.put("name", teacher.getName());
            result.put("email", teacher.getEmail());
            result.put("role", UserRole.TEACHER);
            result.put("department", teacher.getDepartment());
            return result;
        }
        
        // Login failed
        result.put("success", false);
        result.put("message", "Invalid email or password");
        return result;
    }
    
    /**
     * Register a new student
     */
    public Map<String, Object> registerStudent(String name, String email, String password) {
        Map<String, Object> result = new HashMap<>();
        
        // Check if email already exists
        if (studentRepository.findByEmail(email).isPresent() || 
            teacherRepository.findByEmail(email).isPresent()) {
            result.put("success", false);
            result.put("message", "Email already registered");
            return result;
        }
        
        // Generate verification token
        String token = UUID.randomUUID().toString();
        
        Student student = new Student();
        student.setName(name);
        student.setEmail(email);
        student.setPassword(password); // In production, hash this!
        student.setRole(UserRole.STUDENT);
        student.setIsActive(true);
        student.setEmailVerified(false);
        student.setVerificationToken(token);
        student.setTokenExpiryDate(LocalDateTime.now().plusHours(24));
        
        studentRepository.save(student);
        
        // Send verification email
        emailService.sendVerificationEmail(email, name, token);
        
        result.put("success", true);
        result.put("message", "Registration successful! Please check your email to verify your account.");
        result.put("userId", student.getId());
        result.put("role", UserRole.STUDENT);
        return result;
    }
    
    /**
     * Register a new teacher
     */
    public Map<String, Object> registerTeacher(String name, String email, String password, String department) {
        Map<String, Object> result = new HashMap<>();
        
        // Check if email already exists
        if (studentRepository.findByEmail(email).isPresent() || 
            teacherRepository.findByEmail(email).isPresent()) {
            result.put("success", false);
            result.put("message", "Email already registered");
            return result;
        }
        
        // Generate verification token
        String token = UUID.randomUUID().toString();
        
        Teacher teacher = new Teacher();
        teacher.setName(name);
        teacher.setEmail(email);
        teacher.setPassword(password); // In production, hash this!
        teacher.setRole(UserRole.TEACHER);
        teacher.setDepartment(department);
        teacher.setIsActive(true);
        teacher.setEmailVerified(false);
        teacher.setVerificationToken(token);
        teacher.setTokenExpiryDate(LocalDateTime.now().plusHours(24));
        
        teacherRepository.save(teacher);
        
        // Send verification email
        emailService.sendVerificationEmail(email, name, token);
        
        result.put("success", true);
        result.put("message", "Registration successful! Please check your email to verify your account.");
        result.put("userId", teacher.getId());
        result.put("role", UserRole.TEACHER);
        return result;
    }
    
    /**
     * Verify email using token
     */
    public Map<String, Object> verifyEmail(String token) {
        Map<String, Object> result = new HashMap<>();
        
        // Check student
        Student student = studentRepository.findAll().stream()
                .filter(s -> token.equals(s.getVerificationToken()))
                .findFirst()
                .orElse(null);
        
        if (student != null) {
            if (student.getEmailVerified()) {
                result.put("success", false);
                result.put("message", "Email already verified. You can login now.");
                return result;
            }
            
            if (student.getTokenExpiryDate().isBefore(LocalDateTime.now())) {
                result.put("success", false);
                result.put("message", "Verification link has expired. Please register again.");
                return result;
            }
            
            student.setEmailVerified(true);
            student.setVerificationToken(null);
            student.setTokenExpiryDate(null);
            studentRepository.save(student);
            
            emailService.sendWelcomeEmail(student.getEmail(), student.getName(), "STUDENT");
            
            result.put("success", true);
            result.put("message", "Email verified successfully! You can now login.");
            result.put("role", "STUDENT");
            return result;
        }
        
        // Check teacher
        Teacher teacher = teacherRepository.findAll().stream()
                .filter(t -> token.equals(t.getVerificationToken()))
                .findFirst()
                .orElse(null);
        
        if (teacher != null) {
            if (teacher.getEmailVerified()) {
                result.put("success", false);
                result.put("message", "Email already verified. You can login now.");
                return result;
            }
            
            if (teacher.getTokenExpiryDate().isBefore(LocalDateTime.now())) {
                result.put("success", false);
                result.put("message", "Verification link has expired. Please register again.");
                return result;
            }
            
            teacher.setEmailVerified(true);
            teacher.setVerificationToken(null);
            teacher.setTokenExpiryDate(null);
            teacherRepository.save(teacher);
            
            emailService.sendWelcomeEmail(teacher.getEmail(), teacher.getName(), "TEACHER");
            
            result.put("success", true);
            result.put("message", "Email verified successfully! You can now login.");
            result.put("role", "TEACHER");
            return result;
        }
        
        result.put("success", false);
        result.put("message", "Invalid verification token.");
        return result;
    }
}
