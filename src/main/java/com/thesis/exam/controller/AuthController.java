package com.thesis.exam.controller;

import com.thesis.exam.model.UserRole;
import com.thesis.exam.service.AuthService;
import jakarta.servlet.http.HttpSession;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@Controller
public class AuthController {
    
    @Autowired
    private AuthService authService;
    
    @GetMapping("/login")
    public String showLoginPage() {
        return "login";
    }
    
    @PostMapping("/login")
    public String login(@RequestParam String email, 
                       @RequestParam String password,
                       HttpSession session,
                       Model model) {
        
        Map<String, Object> result = authService.login(email, password);
        
        if ((Boolean) result.get("success")) {
            // Store user info in session
            session.setAttribute("userId", result.get("userId"));
            session.setAttribute("userName", result.get("name"));
            session.setAttribute("userEmail", result.get("email"));
            session.setAttribute("userRole", result.get("role"));
            
            UserRole role = (UserRole) result.get("role");
            
            // Redirect based on role
            if (role == UserRole.TEACHER) {
                session.setAttribute("department", result.get("department"));
                return "redirect:/teacher/dashboard";
            } else {
                return "redirect:/student/dashboard";
            }
        } else {
            model.addAttribute("error", result.get("message"));
            return "login";
        }
    }
    
    @GetMapping("/register")
    public String showRegisterPage() {
        return "register";
    }
    
    @PostMapping("/register")
    public String register(@RequestParam String name,
                          @RequestParam String email,
                          @RequestParam String password,
                          @RequestParam String confirmPassword,
                          @RequestParam String role,
                          @RequestParam(required = false) String department,
                          Model model) {
        
        // Validate passwords match
        if (!password.equals(confirmPassword)) {
            model.addAttribute("error", "Passwords do not match");
            return "register";
        }
        
        Map<String, Object> result;
        
        if ("TEACHER".equals(role)) {
            result = authService.registerTeacher(name, email, password, department);
        } else {
            result = authService.registerStudent(name, email, password);
        }
        
        if ((Boolean) result.get("success")) {
            model.addAttribute("success", result.get("message"));
            return "register-success";
        } else {
            model.addAttribute("error", result.get("message"));
            return "register";
        }
    }
    
    @GetMapping("/verify")
    public String verifyEmail(@RequestParam String token, Model model) {
        Map<String, Object> result = authService.verifyEmail(token);
        
        model.addAttribute("success", result.get("success"));
        model.addAttribute("message", result.get("message"));
        
        return "verify-email";
    }
    
    @GetMapping("/logout")
    public String logout(HttpSession session) {
        session.invalidate();
        return "redirect:/login";
    }
    
    @GetMapping("/student/dashboard")
    public String studentDashboard(HttpSession session, Model model) {
        if (session.getAttribute("userId") == null) {
            return "redirect:/login";
        }
        
        model.addAttribute("userName", session.getAttribute("userName"));
        return "student-dashboard";
    }
    
    @GetMapping("/teacher/dashboard")
    public String teacherDashboard(HttpSession session, Model model) {
        if (session.getAttribute("userId") == null) {
            return "redirect:/login";
        }
        
        model.addAttribute("userName", session.getAttribute("userName"));
        model.addAttribute("department", session.getAttribute("department"));
        return "teacher-dashboard";
    }
}
