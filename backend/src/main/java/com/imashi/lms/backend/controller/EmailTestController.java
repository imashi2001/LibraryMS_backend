package com.imashi.lms.backend.controller;

import com.imashi.lms.backend.dto.response.ApiResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/test")
@CrossOrigin(origins = "http://localhost:3000")
public class EmailTestController {
    
    @Autowired
    private JavaMailSender mailSender;
    
    @GetMapping("/email")
    public ResponseEntity<ApiResponse<String>> testEmail(@RequestParam String to) {
        try {
            SimpleMailMessage message = new SimpleMailMessage();
            message.setTo(to);
            message.setSubject("Test Email from Library Management System");
            message.setText("This is a test email from your Library Management System backend!\n\n" +
                          "If you received this email, your email configuration is working correctly.\n\n" +
                          "Best regards,\n" +
                          "Library Management System");
            message.setFrom("ranaweeraimashi880@gmail.com");
            
            mailSender.send(message);
            
            ApiResponse<String> response = new ApiResponse<>(
                "SUCCESS",
                "Test email sent successfully to " + to,
                "Email sent"
            );
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            ApiResponse<String> response = new ApiResponse<>(
                "ERROR",
                "Failed to send email: " + e.getMessage(),
                null
            );
            return ResponseEntity.status(500).body(response);
        }
    }
}

