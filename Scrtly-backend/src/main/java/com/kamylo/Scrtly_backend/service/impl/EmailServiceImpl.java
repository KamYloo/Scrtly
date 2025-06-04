package com.kamylo.Scrtly_backend.service.impl;

import com.kamylo.Scrtly_backend.email.EmailTemplateName;
import com.kamylo.Scrtly_backend.entity.UserEntity;
import com.kamylo.Scrtly_backend.service.EmailService;
import jakarta.mail.MessagingException;
import jakarta.mail.internet.MimeMessage;
import lombok.RequiredArgsConstructor;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;
import org.thymeleaf.context.Context;
import org.thymeleaf.spring6.SpringTemplateEngine;

import java.nio.charset.StandardCharsets;
import java.util.HashMap;
import java.util.Map;

@Service
@RequiredArgsConstructor
public class EmailServiceImpl implements EmailService {

    private final JavaMailSender mailSender;
    private final SpringTemplateEngine templateEngine;

    @Override
    @Async("emailExecutor")
    public void sendEmail(String to, String username, EmailTemplateName emailTemplate, String confirmationURL, String subject, String artistName, UserEntity user) throws MessagingException {
        String templateName = emailTemplate == null ? "confirm-email" : emailTemplate.name();
        MimeMessage mimeMessage = mailSender.createMimeMessage();
        MimeMessageHelper mimeMessageHelper = new MimeMessageHelper(
                mimeMessage,
                MimeMessageHelper.MULTIPART_MODE_MIXED,
                StandardCharsets.UTF_8.name()
        );
        Map<String, Object> properties = new HashMap<>();
        properties.put("username", username);
        properties.put("confirmationUrl", confirmationURL);
        if (artistName != null) {
            properties.put("artistName", artistName);
        }
        if (user != null) {
            properties.put("fullNameUser", user.getFullName());
            properties.put("userEmail", user.getEmail());
        }


        Context context = new Context();
        context.setVariables(properties);

        mimeMessageHelper.setFrom("Scrly-Music");
        mimeMessageHelper.setTo(to);
        mimeMessageHelper.setSubject(subject);

        String template = templateEngine.process(templateName, context);

        mimeMessageHelper.setText(template, true);
        mailSender.send(mimeMessage);
    }
}
