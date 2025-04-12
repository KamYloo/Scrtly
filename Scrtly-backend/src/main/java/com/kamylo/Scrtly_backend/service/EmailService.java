package com.kamylo.Scrtly_backend.service;

import com.kamylo.Scrtly_backend.email.EmailTemplateName;
import com.kamylo.Scrtly_backend.entity.UserEntity;
import jakarta.mail.MessagingException;

public interface EmailService {
    void sendEmail(String to, String username, EmailTemplateName emailTemplate,
                   String confirmationURL, String subject, String artistName, UserEntity user) throws MessagingException;
}
