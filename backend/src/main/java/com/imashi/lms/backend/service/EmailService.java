package com.imashi.lms.backend.service;

import com.imashi.lms.backend.entity.Book;
import com.imashi.lms.backend.entity.Reservation;
import com.imashi.lms.backend.entity.User;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Service;

import java.time.format.DateTimeFormatter;

@Service
public class EmailService {
    
    @Autowired
    private JavaMailSender mailSender;
    
    public void sendReservationConfirmation(User user, Book book, Reservation reservation) {
        try {
            SimpleMailMessage message = new SimpleMailMessage();
            message.setTo(user.getEmail());
            message.setSubject("Book Reservation Confirmation - " + book.getTitle());
            message.setText(buildReservationConfirmationEmail(user, book, reservation));
            message.setFrom("ranaweeraimashi880@gmail.com");
            
            mailSender.send(message);
        } catch (Exception e) {
            // Log error but don't throw - email failure shouldn't break the reservation
            System.err.println("Failed to send reservation confirmation email: " + e.getMessage());
        }
    }
    
    public void sendReturnReminder(User user, Book book, Reservation reservation) {
        try {
            SimpleMailMessage message = new SimpleMailMessage();
            message.setTo(user.getEmail());
            message.setSubject("Return Reminder - " + book.getTitle());
            message.setText(buildReturnReminderEmail(user, book, reservation));
            message.setFrom("ranaweeraimashi880@gmail.com");
            
            mailSender.send(message);
        } catch (Exception e) {
            // Log error but don't throw - email failure shouldn't break the system
            System.err.println("Failed to send return reminder email: " + e.getMessage());
        }
    }
    
    private String buildReservationConfirmationEmail(User user, Book book, Reservation reservation) {
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("MMM dd, yyyy");
        
        return String.format(
            "Dear %s,\n\n" +
            "Your book reservation has been confirmed!\n\n" +
            "Book Details:\n" +
            "- Title: %s\n" +
            "- Author: %s\n" +
            "- ISBN: %s\n\n" +
            "Reservation Details:\n" +
            "- Reservation Date: %s\n" +
            "- Due Date: %s\n\n" +
            "Please return the book on or before the due date to avoid any penalties.\n\n" +
            "Thank you for using our library!\n\n" +
            "Best regards,\n" +
            "Library Management System",
            user.getName() != null && !user.getName().isEmpty() ? user.getName() : user.getEmail(),
            book.getTitle(),
            book.getAuthor(),
            book.getIsbn() != null ? book.getIsbn() : "N/A",
            reservation.getReservationDate().format(formatter),
            reservation.getDueDate().format(formatter)
        );
    }
    
    private String buildReturnReminderEmail(User user, Book book, Reservation reservation) {
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("MMM dd, yyyy");
        
        return String.format(
            "Dear %s,\n\n" +
            "This is a reminder that your reserved book is due soon.\n\n" +
            "Book Details:\n" +
            "- Title: %s\n" +
            "- Author: %s\n\n" +
            "Reservation Details:\n" +
            "- Reservation Date: %s\n" +
            "- Due Date: %s\n\n" +
            "Please return the book on or before the due date to avoid any penalties.\n\n" +
            "Thank you!\n\n" +
            "Best regards,\n" +
            "Library Management System",
            user.getName() != null && !user.getName().isEmpty() ? user.getName() : user.getEmail(),
            book.getTitle(),
            book.getAuthor(),
            reservation.getReservationDate().format(formatter),
            reservation.getDueDate().format(formatter)
        );
    }
}

