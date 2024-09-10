package com.JobBazaar.Backend.Services;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.JavaMailSenderImpl;
import org.springframework.stereotype.Service;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;

@Service
public class EmailService {


    private final JavaMailSender mailSender = new JavaMailSenderImpl();

    @Value("${spring.mail.username}")
    private String from;

    public void sendWelcomeEmail(String recipient, String userName) throws IOException {
        String template = getEmailTemplate();
        String[] parts = template.split("\n\n", 2);
        String subject = parts[0].replace("Subject: ", "");
        String body = parts[1].replace("{}", userName);

        SimpleMailMessage message = new SimpleMailMessage();
        message.setFrom(from);
        message.setTo(recipient);
        message.setSubject(subject);
        message.setText(body);
        mailSender.send(message);
    }

    public String getEmailTemplate() throws IOException {
        // Access the template file from the resources directory
        InputStream inputStream = getClass().getClassLoader().getResourceAsStream("Email/welcome_email.txt");

        if (inputStream == null) {
            throw new IllegalArgumentException("Template file not found");
        }

        try (BufferedReader reader = new BufferedReader(new InputStreamReader(inputStream))) {
            StringBuilder templateBuilder = new StringBuilder();
            String line;

            while ((line = reader.readLine()) != null) {
                templateBuilder.append(line).append("\n");
            }

            return templateBuilder.toString();
        } catch (IOException e) {
            throw new RuntimeException("Error reading email template", e);
        }
    }
}
