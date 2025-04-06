package com.kamylo.Scrtly_backend.service;

import com.kamylo.Scrtly_backend.email.EmailTemplateName;
import jakarta.mail.MessagingException;

public interface EmailService {
    void sendEmail(String to, String username, EmailTemplateName emailTemplate, String confirmationURL, String subject) throws MessagingException;
}
