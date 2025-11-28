package com.thesis.exam.controller;

import com.thesis.exam.model.UserRole;
import jakarta.servlet.http.HttpSession;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class HomeController {
    
    @GetMapping("/")
    public String home(HttpSession session) {
        // Check if user is already logged in
        Object userId = session.getAttribute("userId");
        
        if (userId != null) {
            // User is logged in, redirect to appropriate dashboard
            UserRole role = (UserRole) session.getAttribute("userRole");
            
            if (role == UserRole.TEACHER) {
                return "redirect:/teacher/dashboard";
            } else {
                return "redirect:/student/dashboard";
            }
        }
        
        // User is not logged in, redirect to login page
        return "redirect:/login";
    }
}