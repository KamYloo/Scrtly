package com.kamylo.Scrtly_backend.service;

public interface EmailService {
    void sendActivationEmail(String to, String subject, String activationLink, String message);
}
