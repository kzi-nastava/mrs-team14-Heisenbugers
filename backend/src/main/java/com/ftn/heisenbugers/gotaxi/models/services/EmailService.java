package com.ftn.heisenbugers.gotaxi.models.services;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Service;

@Service
public class EmailService {
    /*public void sendActivationEmail(String toEmail, String activationLink) {
        // in log
        System.out.println("=== ACTIVATION EMAIL ===");
        System.out.println("To: " + toEmail);
        System.out.println("Link: " + activationLink);
        System.out.println("========================");
    }*/

    private final JavaMailSender mailSender;
    @Value("${app.mail.from}")
    private String from;

    public EmailService(JavaMailSender mailSender) {
        this.mailSender = mailSender;
    }

    public void sendActivationEmail(String toEmail, String activationLink) {
        SimpleMailMessage msg = new SimpleMailMessage();
        msg.setFrom(from);
        msg.setTo(toEmail);
        msg.setSubject("GoTaxi – Activate your account");
        msg.setText("""
                Thanks for registering!
                
                Activate your account (valid for 24h):
                %s
                """.formatted(activationLink));

        mailSender.send(msg);
    }

    public void sendMail(String toEmail, String subject, String body) {
        SimpleMailMessage msg = new SimpleMailMessage();
        msg.setFrom(from);
        msg.setTo(toEmail);
        msg.setSubject(subject);
        msg.setText(body);
        mailSender.send(msg);
    }

    public void sendPasswordResetEmail(String toEmail, String resetLink) {
        try{
        SimpleMailMessage msg = new SimpleMailMessage();
        msg.setFrom(from);
        msg.setTo(toEmail);
        msg.setSubject("GoTaxi – Password reset");
        msg.setText("""
            You requested a password reset.

            Click the link below to set a new password (valid for 24h):
            %s

            If you did not request this, please ignore this email.
            """.formatted(resetLink));

        mailSender.send(msg);
    }catch (Exception ex) {
        // Чтобы не ронять /forgot-password с 500, но видеть проблему в логах
        System.err.println("Failed to send password reset email: " + ex.getMessage());
        ex.printStackTrace();
    }
}
}
