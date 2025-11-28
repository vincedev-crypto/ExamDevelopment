package com.thesis.exam.service;

import com.thesis.exam.util.TokenEncryptor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Service;

@Service
public class EmailService {
    
    @Autowired
    private JavaMailSender mailSender;
    
    /**
     * Send verification email with token link
     */
    public void sendVerificationEmail(String toEmail, String userName, String verificationToken) {
        try {
            SimpleMailMessage message = new SimpleMailMessage();
            message.setFrom("noreply@examsystem.com");
            message.setTo(toEmail);
            message.setSubject("Verify Your Email - Adaptive Exam System");
            
            // Encrypt the token before including it in the URL
            String encryptedToken = TokenEncryptor.encrypt(verificationToken);
            String verificationLink = "http://localhost:8080/verify?token=" + encryptedToken;
            
            String emailBody = "Dear " + userName + ",\n\n" +
                    "Thank you for registering with the Adaptive Exam System!\n\n" +
                    "Please click the link below to verify your email address:\n" +
                    verificationLink + "\n\n" +
                    "This link will expire in 10 minutes.\n\n" +
                    "If you did not register for this account, please ignore this email.\n\n" +
                    "Best regards,\n" +
                    "Adaptive Exam System Team";
            
            message.setText(emailBody);
            
            mailSender.send(message);
        } catch (Exception e) {
            System.err.println("Failed to send email: " + e.getMessage());
            // In production, you might want to log this or retry
        }
    }
    
    /**
     * Send password reset email
     */
    public void sendPasswordResetEmail(String toEmail, String userName, String resetToken) {
        try {
            SimpleMailMessage message = new SimpleMailMessage();
            message.setFrom("noreply@examsystem.com");
            message.setTo(toEmail);
            message.setSubject("Password Reset - Adaptive Exam System");
            
            String resetLink = "http://localhost:8080/reset-password?token=" + resetToken;
            
            String emailBody = "Dear " + userName + ",\n\n" +
                    "We received a request to reset your password.\n\n" +
                    "Please click the link below to reset your password:\n" +
                    resetLink + "\n\n" +
                    "This link will expire in 1 hour.\n\n" +
                    "If you did not request a password reset, please ignore this email.\n\n" +
                    "Best regards,\n" +
                    "Adaptive Exam System Team";
            
            message.setText(emailBody);
            
            mailSender.send(message);
        } catch (Exception e) {
            System.err.println("Failed to send email: " + e.getMessage());
        }
    }
    
    /**
     * Send welcome email after verification
     */
    public void sendWelcomeEmail(String toEmail, String userName, String role) {
        try {
            SimpleMailMessage message = new SimpleMailMessage();
            message.setFrom("noreply@examsystem.com");
            message.setTo(toEmail);
            message.setSubject("Welcome to Adaptive Exam System!");
            
            String emailBody = "Dear " + userName + ",\n\n" +
                    "Your email has been successfully verified!\n\n" +
                    "You can now log in to your " + role.toLowerCase() + " account and start using the system.\n\n" +
                    "Login here: http://localhost:8080/login\n\n" +
                    "Best regards,\n" +
                    "Adaptive Exam System Team";
            
            message.setText(emailBody);
            
            mailSender.send(message);
        } catch (Exception e) {
            System.err.println("Failed to send email: " + e.getMessage());
        }
    }
}
