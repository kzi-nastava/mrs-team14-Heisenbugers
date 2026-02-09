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

    public void sendActivationEmail(String toEmail,
                                    String webLink,
                                    String androidLink) {
        SimpleMailMessage msg = new SimpleMailMessage();
        msg.setFrom(from);
        msg.setTo(toEmail);
        msg.setSubject("GoTaxi – Activate your account");
        msg.setText("""
                Thanks for registering!

            Web:
            %s

            Android app:
            %s

            The link is valid for 24 hours.
            """.formatted(webLink, androidLink));

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

    public void sendPasswordResetEmail(String toEmail, String resetLink, String mobileDeepLink) {
        try{
            String subject = "Password reset request";

            String content = """
            You requested a password reset.

            Web version:
            %s

            Android app:
            %s

            If you did not request this, please ignore this email.
            """.formatted(resetLink, mobileDeepLink);

            SimpleMailMessage msg = new SimpleMailMessage();
            msg.setFrom(from);
            msg.setTo(toEmail);
            msg.setSubject(subject);
            msg.setText(content);

            mailSender.send(msg);
    }catch (Exception ex) {
        System.err.println("Failed to send password reset email: " + ex.getMessage());
        ex.printStackTrace();
    }
}
}
