package com.kamylo.Scrtly_backend.service.impl;

import com.kamylo.Scrtly_backend.service.EmailService;
import lombok.RequiredArgsConstructor;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class EmailServiceImpl implements EmailService {

    private final JavaMailSender mailSender;

    @Override
    public void sendActivationEmail(String to, String subject, String activationLink, String message) {

    }
}
